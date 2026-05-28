# API Endpoints

Všetky endpointy okrem výslovne **verejných** vyžadujú platný **Bearer JWT token** z Keycloacku. Roly sa načítavajú z claimu `realm_access.roles`.

**Dostupné roly:** `ADMIN`, `LIBRARIAN`, `MEMBER`

**Verejné (nevyžadujú prihlásenie):** `GET /books`, `GET /books/{isbn}`, `GET /announcements`, `GET /announcements/{id}`

**Identita aktuálneho čitateľa** sa odvodzuje z JWT claimov `email`, `given_name`, `family_name`. Pri prvom volaní autentifikovaného endpointu sa MEMBER record automaticky vyrobí (provisioning) cez `MemberProvisioningService`.

---

## Knihy — `/books`

### `GET /books`
Vráti zoznam všetkých kníh. Voliteľne filtruje podľa názvu, autora alebo žánru (parametre sú navzájom výlučné — priorita: `title` > `author` > `genre`).

| Query param | Typ | Popis |
|---|---|---|
| `title` | string | Filtruje podľa názvu |
| `author` | string | Filtruje podľa autora |
| `genre` | BookGenre enum | Filtruje podľa žánru |

**Prístup:** Verejný  
**Odpovede:** `200 OK` — pole `Book` | `500`

---

### `GET /books/{isbn}`
Vráti detail jednej knihy podľa ISBN vrátane popisu a fotiek.

| Path param | Typ | Popis |
|---|---|---|
| `isbn` | string | ISBN-10 alebo ISBN-13 |

**Prístup:** Verejný  
**Odpovede:** `200 OK` — `Book` | `404`, `500`

---

### `POST /books`
Pridá novú knihu do katalógu.

**Request body:**
```json
{
  "isbn": "978-3-16-148410-0",
  "title": "Názov knihy",
  "author": "Meno Autora",
  "genre": "FICTION",
  "publisher": "Vydavateľstvo",
  "publicationYear": 2020,
  "totalCopies": 3,
  "description": "Voliteľný popis"
}
```

**Prístup:** `ADMIN`  
**Odpovede:** `201 Created` | `400`, `401`, `403`, `409` (ISBN už existuje), `500`

---

### `DELETE /books/{isbn}`
Odstráni knihu z katalógu. Knihu nie je možné odstrániť ak má aktívne výpožičky alebo rezervácie.

**Prístup:** `ADMIN`  
**Odpovede:** `204 No Content` | `401`, `403`, `404`, `409`, `500`

---

### `POST /books/{isbn}/copies`
Pridá ďalšie kópie existujúcej knihy do katalógu.

**Request body:**
```json
{ "count": 2 }
```

**Prístup:** `ADMIN`  
**Odpovede:** `200 OK` — aktualizovaný `Book` | `400`, `401`, `403`, `404`, `500`

---

### `PUT /books/{isbn}/description`
Aktualizuje popis knihy (max 2000 znakov).

**Request body:**
```json
{ "description": "Nový popis knihy…" }
```

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `200 OK` — aktualizovaný `Book` | `400`, `401`, `403`, `404`, `500`

---

### `POST /books/{isbn}/photos`
Nahrá fotku ku knihe (max 5 fotiek na knihu, max 5 MB, formáty JPG/PNG/WebP/GIF). Fotka sa ukladá do Azure Blob storage; backend vracia DTO s public URL.

**Request:** `multipart/form-data`
- `file` (binary, required)
- `caption` (string, optional)

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `201 Created` — `BookPhoto` | `400`, `401`, `403`, `404`, `413`, `415`, `500`

---

### `DELETE /books/{isbn}/photos/{photoId}`
Odstráni fotku knihy (z DB aj z Blob storage).

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `204 No Content` | `401`, `403`, `404`, `500`

---

## Členovia — `/members`

### `GET /members/me`
Vráti profil aktuálne prihláseného člena (identita z JWT). Ak člen ešte nie je v DB, automaticky sa vytvorí s 12-mesačným členstvom. Vrátený `Member` obsahuje aj zoznam pokút.

**Prístup:** Každý autentifikovaný používateľ  
**Odpovede:** `200 OK` — `Member` | `401`, `500`

---

### `GET /members`
Vráti zoznam všetkých členov knižnice vrátane ich členstva a pokút.

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `200 OK` — pole `Member` | `401`, `403`, `500`

---

### `GET /members/{id}`
Vráti detail jedného člena podľa ID vrátane členstva a pokút.

| Path param | Typ | Popis |
|---|---|---|
| `id` | int64 | ID člena |

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `200 OK` — `Member` | `401`, `403`, `404`, `500`

---

### `POST /members`
Zaregistruje nového člena knižnice. Členstvo sa automaticky nastaví na 12 mesiacov.

**Request body:**
```json
{
  "firstName": "Ján",
  "lastName": "Novák",
  "email": "jan.novak@email.sk",
  "memberRole": "MEMBER"
}
```

Možné hodnoty `memberRole`: `MEMBER`, `LIBRARIAN`, `ADMIN`

**Prístup:** `ADMIN`  
**Odpovede:** `201 Created` | `400`, `401`, `403`, `409` (email už existuje), `500`

---

### `POST /members/{id}/membership/renew`
Obnoví členstvo člena o ďalších 12 mesiacov.

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `200 OK` | `401`, `403`, `404`, `500`

---

### `POST /members/{id}/fines/{fineId}/pay`
Označí pokutu ako uhradenú.

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `200 OK` | `401`, `403`, `404`, `500`

---

### `POST /members/{id}/fines/{fineId}/waive`
Odpíše (promine) pokutu člena.

**Prístup:** `ADMIN`  
**Odpovede:** `200 OK` | `401`, `403`, `404`, `500`

---

## Výpožičky — `/loans`

### `GET /loans`
Vráti zoznam výpožičiek. Správanie závisí od roly:
- `ADMIN` / `LIBRARIAN` — vrátia všetky výpožičky, voliteľne filtrované cez `?memberId=`
- `MEMBER` — vždy vidí len vlastné výpožičky (parameter `memberId` sa ignoruje)

| Query param | Typ | Popis |
|---|---|---|
| `memberId` | int64 | Filtruje podľa člena (len ADMIN/LIBRARIAN) |

**Prístup:** Každý autentifikovaný používateľ  
**Odpovede:** `200 OK` — pole `Loan` | `401`, `403`, `500`

---

### `GET /loans/overdue`
Vráti všetky výpožičky po termíne vrátenia.

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `200 OK` — pole `Loan` | `401`, `403`, `500`

---

### `POST /loans`
Vytvorí novú výpožičku. Systém overí: platné členstvo, žiadne neuhradené pokuty, dostupnosť kópie knihy. Lehota vrátenia sa nastaví na 14 dní od dnešného dátumu. ID knihovníka, ktorý výpožičku vytvára, sa odvodí z JWT.

**Request body:**
```json
{
  "memberId": 1,
  "isbn": "978-3-16-148410-0"
}
```

**Prístup:** `LIBRARIAN`  
**Odpovede:** `201 Created` | `400`, `401`, `403`, `404`, `409`, `500`

---

### `POST /loans/{id}/return`
Zaznamená vrátenie knihy. Ak je kniha vrátená po termíne, systém automaticky vytvorí alebo aktualizuje pokutu (0,50 € / deň omeškania). Po vrátení sa publikuje `BookReturnedEvent` ktorý notifikuje prvého čakajúceho v rezervačnej rade.

**Prístup:** `LIBRARIAN`  
**Odpovede:** `200 OK` | `401`, `403`, `404`, `409`, `500`

---

### `POST /loans/{id}/renew`
Predĺži aktívnu výpožičku o ďalších 14 dní. Podmienky: výpožička nesmie byť po termíne, nesmie byť prekročený maximálny počet predĺžení (1×), na knihu nesmie existovať aktívna rezervácia iného člena. Pre MEMBER role je povolené len predĺžiť vlastnú výpožičku.

**Prístup:** `LIBRARIAN`, `MEMBER` (vlastná výpožička)  
**Odpovede:** `200 OK` | `400`, `401`, `403`, `404`, `409`, `500`

---

## Rezervácie — `/reservations`

### `GET /reservations`
Vráti zoznam rezervácií. Správanie závisí od roly:
- `ADMIN` / `LIBRARIAN` — vrátia všetky rezervácie, voliteľne filtrované cez `?memberId=`
- `MEMBER` — vždy vidí len vlastné rezervácie (parameter `memberId` sa ignoruje)

**Prístup:** Každý autentifikovaný používateľ  
**Odpovede:** `200 OK` — pole `Reservation` | `401`, `403`, `500`

---

### `POST /reservations`
Vytvorí novú rezerváciu na nedostupnú knihu. Člen dostane automatickú notifikáciu, keď bude kniha vrátená a bude prvý v rade. Rezervácia v stave `READY_FOR_PICKUP` expiruje po 3 dňoch, ak si člen knihu nevypožičí.

**Request body:**
```json
{
  "memberId": 1,
  "isbn": "978-3-16-148410-0"
}
```

**Prístup:** `MEMBER`, `LIBRARIAN`  
**Odpovede:** `201 Created` | `400`, `401`, `403`, `404`, `409`, `500`

---

### `POST /reservations/{id}/cancel`
Zruší rezerváciu. Člen môže zrušiť len vlastnú rezerváciu; staff môže zrušiť ktorúkoľvek. Po zrušení sa automaticky prebuduje poradie zvyšných rezervácií v rade pre danú knihu.

**Prístup:** Každý autentifikovaný používateľ (vlastná rezervácia pre MEMBER)  
**Odpovede:** `200 OK` | `401`, `403`, `404`, `409`, `500`

---

## Oznamy — `/announcements`

### `GET /announcements`
Vráti zoznam všetkých oznamov knižnice (zoradené od najnovších).

**Prístup:** Verejný  
**Odpovede:** `200 OK` — pole `Announcement` | `500`

---

### `GET /announcements/{id}`
Vráti detail jedného oznamu vrátane priložených fotiek.

**Prístup:** Verejný  
**Odpovede:** `200 OK` — `Announcement` | `404`, `500`

---

### `POST /announcements`
Vytvorí nový oznam. Author sa odvodí z JWT identity (aktuálny prihlásený LIBRARIAN/ADMIN).

**Request body:**
```json
{
  "title": "Letné otváracie hodiny",
  "content": "Od 1.7. bude knižnica otvorená …"
}
```

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `201 Created` — `Announcement` | `400`, `401`, `403`, `500`

---

### `PUT /announcements/{id}`
Upraví obsah existujúceho oznamu.

**Request body:**
```json
{
  "title": "Nový nadpis",
  "content": "Nový obsah…"
}
```

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `200 OK` — `Announcement` | `400`, `401`, `403`, `404`, `500`

---

### `DELETE /announcements/{id}`
Odstráni oznam (vrátane všetkých priložených fotiek v Blob storage).

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `204 No Content` | `401`, `403`, `404`, `500`

---

### `POST /announcements/{id}/photos`
Pridá fotku k oznamu (max 5 fotiek na oznam, max 5 MB, formáty JPG/PNG/WebP/GIF).

**Request:** `multipart/form-data`
- `file` (binary, required)
- `caption` (string, optional)

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `201 Created` — `AnnouncementPhoto` | `400`, `401`, `403`, `404`, `413`, `415`, `500`

---

### `DELETE /announcements/{id}/photos/{photoId}`
Odstráni fotku oznamu (z DB aj z Blob storage).

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `204 No Content` | `401`, `403`, `404`, `500`

---

## Administrácia — `/admin`

Všetky `/admin/**` endpointy sú prístupné výhradne pre rolu `ADMIN`.

### Manuálne spúšťanie jobov — `/admin/jobs`

Joby bežia automaticky každú noc cez ShedLock. Tieto endpointy ich umožňujú spustiť okamžite (napr. pri výpadku cron-u alebo na overenie logiky).

Odpoveď:
```json
{
  "job": "fine-accrual",
  "executedAt": "2026-05-28T00:00:00.123+02:00",
  "processed": 5
}
```

### `POST /admin/jobs/loan-status`
Prejde nevrátené výpožičky a tie po termíne označí ako `OVERDUE`. Beží automaticky o 23:55.

---

### `POST /admin/jobs/fine-accrual`
Prejde výpožičky v stave `OVERDUE` a pripočíta/aktualizuje pokutu (0,50 € / deň). Beží automaticky o 00:00.

---

### `POST /admin/jobs/reservation-expiration`
Označí rezervácie v stave `READY_FOR_PICKUP` po 3-dňovej lehote ako `EXPIRED` a notifikuje ďalšieho v rade. Beží automaticky o 00:05.

---

### `POST /admin/jobs/membership-expiry-notifications`
Pošle e-mail členom 7 dní pred expiráciou členstva. Beží automaticky o 00:10.

---

### `POST /admin/jobs/loan-due-notifications`
Pošle e-mail čitateľom 3 dni pred koncom výpožičky. Beží automaticky o 00:15.

---

### `POST /admin/jobs/membership-expiration`
Označí členstvá po dátume expirácie ako `EXPIRED`. Beží automaticky o 00:20.

---

### Admin overrides — `/admin/overrides`

Endpointy na manuálnu zmenu dátumov pre účely debugovania a recovery.

### `POST /admin/overrides/loans/{id}/due-date`
Zmení termín vrátenia výpožičky.

**Request body:**
```json
{ "date": "2026-06-15" }
```

**Odpovede:** `200 OK` — `{ "loanId": 1, "dueDate": "2026-06-15" }` | `400`, `401`, `403`, `404`

---

### `POST /admin/overrides/members/{id}/membership-expiry`
Zmení dátum expirácie členstva.

**Request body:**
```json
{ "date": "2027-05-28" }
```

**Odpovede:** `200 OK` — `{ "memberId": 1, "expiryDate": "2027-05-28" }` | `400`, `401`, `403`, `404`

---

### `POST /admin/overrides/reservations/{id}/created-on`
Zmení dátum vytvorenia rezervácie (ovplyvní výpočet expirácie READY_FOR_PICKUP stavu).

**Request body:**
```json
{ "date": "2026-05-25" }
```

**Odpovede:** `200 OK` — `{ "reservationId": 1, "createdOn": "2026-05-25" }` | `400`, `401`, `403`, `404`

---

## Prehľad prístupov

| Endpoint | Verejný | ADMIN | LIBRARIAN | MEMBER |
|---|:---:|:---:|:---:|:---:|
| `GET /books`, `GET /books/{isbn}` | ✓ | ✓ | ✓ | ✓ |
| `POST /books` | — | ✓ | — | — |
| `DELETE /books/{isbn}` | — | ✓ | — | — |
| `POST /books/{isbn}/copies` | — | ✓ | — | — |
| `PUT /books/{isbn}/description` | — | ✓ | ✓ | — |
| `POST /books/{isbn}/photos` | — | ✓ | ✓ | — |
| `DELETE /books/{isbn}/photos/{photoId}` | — | ✓ | ✓ | — |
| `GET /members/me` | — | ✓ | ✓ | ✓ |
| `GET /members`, `GET /members/{id}` | — | ✓ | ✓ | — |
| `POST /members` | — | ✓ | — | — |
| `POST /members/{id}/membership/renew` | — | ✓ | ✓ | — |
| `POST /members/{id}/fines/{fineId}/pay` | — | ✓ | ✓ | — |
| `POST /members/{id}/fines/{fineId}/waive` | — | ✓ | — | — |
| `GET /loans` | — | ✓ | ✓ | vlastné |
| `GET /loans/overdue` | — | ✓ | ✓ | — |
| `POST /loans` | — | — | ✓ | — |
| `POST /loans/{id}/return` | — | — | ✓ | — |
| `POST /loans/{id}/renew` | — | — | ✓ | vlastné |
| `GET /reservations` | — | ✓ | ✓ | vlastné |
| `POST /reservations` | — | — | ✓ | ✓ |
| `POST /reservations/{id}/cancel` | — | ✓ | ✓ | vlastné |
| `GET /announcements`, `GET /announcements/{id}` | ✓ | ✓ | ✓ | ✓ |
| `POST /announcements` | — | ✓ | ✓ | — |
| `PUT /announcements/{id}` | — | ✓ | ✓ | — |
| `DELETE /announcements/{id}` | — | ✓ | ✓ | — |
| `POST /announcements/{id}/photos` | — | ✓ | ✓ | — |
| `DELETE /announcements/{id}/photos/{photoId}` | — | ✓ | ✓ | — |
| `POST /admin/jobs/*` | — | ✓ | — | — |
| `POST /admin/overrides/*` | — | ✓ | — | — |

---

## Chybové odpovede

Všetky chybové odpovede majú formát:

```json
{
  "code": "NOT_FOUND",
  "message": "Člen s ID 42 neexistuje.",
  "details": ["…"],
  "path": "/members/42",
  "timestamp": "2026-05-27T20:50:13.123+02:00"
}
```

| HTTP | `code` (príklady) | Význam |
|---|---|---|
| `400` | `VALIDATION_ERROR`, `BAD_REQUEST` | Neplatný request body alebo parametre |
| `401` | `UNAUTHORIZED` | Chýba alebo neplatný JWT |
| `403` | `FORBIDDEN` | Autentifikovaný, ale nemá potrebnú rolu |
| `404` | `NOT_FOUND` | Zdroj neexistuje |
| `409` | `CONFLICT` | Konflikt stavu (napr. ISBN existuje, kniha nie je dostupná, výpožička už vrátená) |
| `413` | `PAYLOAD_TOO_LARGE` | Súbor presahuje 5 MB |
| `415` | `UNSUPPORTED_MEDIA_TYPE` | Nepodporovaný formát fotky |
| `500` | `INTERNAL_ERROR` | Neočakávaná chyba servera |