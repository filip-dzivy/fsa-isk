# API Endpoints

Všetky endpointy vyžadujú platný **Bearer JWT token**. Roly sa načítavajú z Keycloak claimu `realm_access.roles`.

Dostupné roly: `ADMIN`, `LIBRARIAN`, `MEMBER`

---

## Knihy — `/books`

### `GET /books`
Vráti zoznam všetkých kníh. Voliteľne filtruje podľa názvu, autora alebo žánru (parametre sú navzájom výlučné — priorita: `title` > `author` > `genre`).

| Query param | Typ | Popis |
|---|---|---|
| `title` | string | Filtruje podľa názvu |
| `author` | string | Filtruje podľa autora |
| `genre` | BookGenre enum | Filtruje podľa žánru |

**Prístup:** Každý prihlásený používateľ  
**Odpovede:** `200 OK` — pole `Book` | `401`, `403`, `500`

---

### `GET /books/{isbn}`
Vráti detail jednej knihy podľa ISBN.

| Path param | Typ | Popis |
|---|---|---|
| `isbn` | string | ISBN-10 alebo ISBN-13 |

**Prístup:** Každý prihlásený používateľ  
**Odpovede:** `200 OK` — `Book` | `401`, `403`, `404`, `500`

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
  "totalCopies": 3
}
```

**Prístup:** `ADMIN`  
**Odpovede:** `201 Created` | `400`, `401`, `403`, `409` (ISBN už existuje), `500`

---

## Členovia — `/members`

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

| Path param | Typ | Popis |
|---|---|---|
| `id` | int64 | ID člena |

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `200 OK` | `401`, `403`, `404`, `500`

---

### `POST /members/{id}/fines/{fineId}/pay`
Označí pokutu ako uhradenú.

| Path param | Typ | Popis |
|---|---|---|
| `id` | int64 | ID člena |
| `fineId` | int64 | ID pokuty |

**Prístup:** `ADMIN`, `LIBRARIAN`  
**Odpovede:** `200 OK` | `401`, `403`, `404`, `500`

---

### `POST /members/{id}/fines/{fineId}/waive`
Odpíše (promine) pokutu člena.

| Path param | Typ | Popis |
|---|---|---|
| `id` | int64 | ID člena |
| `fineId` | int64 | ID pokuty |

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

**Prístup:** Každý prihlásený používateľ  
**Odpovede:** `200 OK` — pole `Loan` | `401`, `403`, `500`

---

### `GET /loans/overdue`
Vráti všetky výpožičky po termíne vrátenia.

**Prístup:** Každý prihlásený používateľ  
**Odpovede:** `200 OK` — pole `Loan` | `401`, `403`, `500`

---

### `POST /loans`
Vytvorí novú výpožičku. Systém overí: platné členstvo, žiadne neuhradené pokuty, dostupnosť kópie knihy. Lehota vrátenia sa nastaví na 14 dní od dnešného dátumu.

**Request body:**
```json
{
  "memberId": 1,
  "isbn": "978-3-16-148410-0",
  "createdById": 5
}
```

`createdById` je ID knihovníka, ktorý výpožičku vytvára.

**Prístup:** `LIBRARIAN`  
**Odpovede:** `201 Created` | `400`, `401`, `403`, `404`, `409`, `500`

---

### `POST /loans/{id}/return`
Zaznamená vrátenie knihy. Ak je kniha vrátená po termíne, systém automaticky vytvorí alebo aktualizuje pokutu (0,50 € / deň omeškania). Po vrátení systém notifikuje prvého čakajúceho v rezervačnej rade.

| Path param | Typ | Popis |
|---|---|---|
| `id` | int64 | ID výpožičky |

**Prístup:** `LIBRARIAN`  
**Odpovede:** `200 OK` | `401`, `403`, `404`, `409`, `500`

---

### `POST /loans/{id}/renew`
Predĺži aktívnu výpožičku o ďalších 14 dní. Podmienky: výpožička nesmie byť po termíne, nesmie byť prekročený maximálny počet predĺžení (1×), na knihu nesmie existovať aktívna rezervácia iného člena.

| Path param | Typ | Popis |
|---|---|---|
| `id` | int64 | ID výpožičky |

**Prístup:** `LIBRARIAN`, `MEMBER`  
**Odpovede:** `200 OK` | `400`, `401`, `403`, `404`, `409`, `500`

---

## Rezervácie — `/reservations`

### `GET /reservations`
Vráti zoznam rezervácií. Správanie závisí od roly:
- `ADMIN` / `LIBRARIAN` — vrátia všetky rezervácie, voliteľne filtrované cez `?memberId=`
- `MEMBER` — vždy vidí len vlastné rezervácie (parameter `memberId` sa ignoruje)

| Query param | Typ | Popis |
|---|---|---|
| `memberId` | int64 | Filtruje podľa člena (len ADMIN/LIBRARIAN) |

**Prístup:** Každý prihlásený používateľ  
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
Zruší rezerváciu. Systém overí vlastníctvo — člen môže zrušiť len vlastnú rezerváciu. Po zrušení sa automaticky prebuduje poradie zvyšných rezervácií v rade.

| Path param | Typ | Popis |
|---|---|---|
| `id` | int64 | ID rezervácie |

**Prístup:** Každý prihlásený používateľ  
**Odpovede:** `200 OK` | `401`, `403`, `404`, `409`, `500`

---

## Prehľad prístupov

| Endpoint | ADMIN | LIBRARIAN | MEMBER |
|---|:---:|:---:|:---:|
| `GET /books` | ✓ | ✓ | ✓ |
| `GET /books/{isbn}` | ✓ | ✓ | ✓ |
| `POST /books` | ✓ | — | — |
| `GET /members` | ✓ | ✓ | — |
| `GET /members/{id}` | ✓ | ✓ | — |
| `POST /members` | ✓ | — | — |
| `POST /members/{id}/membership/renew` | ✓ | ✓ | — |
| `POST /members/{id}/fines/{fineId}/pay` | ✓ | ✓ | — |
| `POST /members/{id}/fines/{fineId}/waive` | ✓ | — | — |
| `GET /loans` | ✓ | ✓ | vlastné |
| `GET /loans/overdue` | ✓ | ✓ | ✓ |
| `POST /loans` | — | ✓ | — |
| `POST /loans/{id}/return` | — | ✓ | — |
| `POST /loans/{id}/renew` | — | ✓ | ✓ |
| `GET /reservations` | ✓ | ✓ | vlastné |
| `POST /reservations` | — | ✓ | ✓ |
| `POST /reservations/{id}/cancel` | ✓ | ✓ | ✓ |
