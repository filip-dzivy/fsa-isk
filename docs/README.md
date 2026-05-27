# fsa-isk

## Zadanie od zákazníka

Naša mestská knižnica by potrebovala moderný informačný systém na správu knižničného fondu a výpožičiek. Systém by mal umožniť čitateľom jednoducho vyhľadávať knihy v katalógu a hneď vidieť, či je kniha dostupná na vypožičanie.
Keď je kniha dostupná, člen by si ju mal vedieť vypožičať skrze knihovníka. Ak kniha nie je k dispozícii, čitateľ by si ju mal vedieť rezervovať. Osoba, ktorá má knihu aktuálne vypožičanú, si môže výpožičku predĺžiť. Používateľ s rezerváciou dostane automatickú notifikáciu keď je kniha vrátená do knižnice.
Každý čitateľ musí mať v systéme evidované členstvo s platnosťou. Systém by mal automaticky upozorňovať na blížiace sa uplynutie členstva. Čitatelia s vypršaným členstvom alebo neuhradenými pokutami by nemali mať možnosť si knihy vypožičať.
Pri výpožičke sa nastaví dvojtýždňová lehota na vrátenie. Ak čitateľ knihu vráti neskoro, systém by mal automaticky vypočítať pokutu. Knihovníci by v systéme potrebovali sledovať všetky výpožičky, rezervácie a pokuty. Administrátor by mal vedieť pridávať knihy a upravovať informácie o knihách.

---

## Zber požiadaviek

### Správa kníh
- **RQ01** Systém umožní administrátorovi pridávanie a odstraňovanie kníh.
- **RQ02** Systém umožní administrátorovi a knihovníkovi úpravu informácií o knihách (popis, fotky).
- **RQ03** Systém zabezpečí sledovanie počtu dostupných kópií jednotlivých kníh.
- **RQ04** Systém umožní vyhľadávanie kníh podľa rôznych kritérií (názov, autor, ISBN, žáner, dostupnosť).
- **RQ05** Systém zabezpečí zobrazovanie stavu dostupnosti jednotlivých kníh aj neprihláseným návštevníkom.
- **RQ06** Systém umožní knihovníkovi a administrátorovi sledovať aktuálne výpožičky a filtrovať medzi nimi.
- **RQ06a** Systém umožní pripájať fotografie obálok ku knihám (max 5 fotiek/kniha).

### Výpožičky
- **RQ07** Systém umožní knihovníkovi vytvoriť výpožičku priradením knihy k čitateľovi.
- **RQ08** Systém zabezpečí sledovanie aktívneho členstva čitateľov pri vytváraní výpožičky.
- **RQ09** Systém nastaví výpožičnú dobu (štandardne 14 dní).
- **RQ10** Systém zabezpečí sledovanie dátumov výpožičky a vrátenia.
- **RQ11** Systém umožní predĺženie výpožičky (max 1×, ak nie je rezervácia od iného člena).
- **RQ12** Systém umožní knihovníkovi v systéme zaznačiť vrátenie knihy.
- **RQ12a** Systém ponúkne knihovníkovi pri vrátení knihy po termíne okamžitú úhradu pokuty.

### Rezervácie
- **RQ13** Systém umožní rezervovať si knihu, ktorá momentálne nie je dostupná.
- **RQ14** Systém notifikuje čitateľa o dostupnosti rezervovanej knihy.
- **RQ15** Systém zruší rezerváciu automaticky ak kniha nie je vypožičaná do 3 dní od vrátenia.

### Členstvo
- **RQ16** Systém umožní registráciu nových čitateľov.
- **RQ16a** Systém automaticky vyrobí čitateľský záznam pri prvom prihlásení (cez Keycloak identity).
- **RQ17** Systém zabezpečí sledovanie platnosti členstva.
- **RQ18** Systém zabezpečí upozornenie čitateľa o blížiacom sa konci členstva (30 dní vopred).
- **RQ19** Systém zabezpečí blokovanie výpožičiek od čitateľov s neplatným členstvom.
- **RQ20** Systém umožní knihovníkovi/administrátorovi obnoviť členstvo.
- **RQ20a** Systém umožní čitateľovi vidieť stav svojho členstva, dátum expirácie a pokuty.

### Pokuty a poplatky
- **RQ21** Systém zabezpečí pokutovanie za omeškanie doby vrátenia knihy (0,50 € / deň).
- **RQ22** Systém umožní knihovníkovi zaznamenať úhradu pokuty.
- **RQ23** Systém zabezpečí blokovanie výpožičiek od čitateľov s neuhradenými pokutami.
- **RQ24** Systém umožní administrátorovi odpísať (waive) pokutu.

### Oznamy
- **RQ26** Systém umožní administrátorovi/knihovníkovi publikovať a spravovať oznamy knižnice.
- **RQ27** Systém zobrazí oznamy aj neprihláseným návštevníkom.
- **RQ28** Systém umožní pripájať fotografie k oznamom (max 5 fotiek/oznam).

### Administrácia
- **RQ25** Systém umožní administrátorovi spravovať používateľské účty.

---

## Slovník pojmov

### Knihy a katalóg
| **Pojem** | **Anglický názov** | **Definícia** |
|---|---|---|
| **Kniha** | Book | Publikácia evidovaná v knižnici (názov, autor, ISBN, žáner, vydavateľ, rok vydania, počet kópií, popis, fotky). |
| **Katalóg** | Catalog | Evidencia všetkých kníh dostupných v knižnici, verejne prístupná pre vyhľadávanie. |
| **ISBN** | ISBN | Medzinárodný štandardný identifikátor knihy (ISBN-10 alebo ISBN-13), zároveň prirodzený identifikátor entity Book. |
| **Kópia** | Copy | Fyzický exemplár knihy. Kniha má `totalCopies` celkovo a `availableCopies` momentálne dostupných na výpožičku. |
| **Žáner** | BookGenre | Kategória knihy (FICTION, FANTASY, BIOGRAPHY, …) — enum s 32 hodnotami. |
| **Fotka knihy** | BookPhoto | Obrázok pripojený ku knihe (obálka, ilustrácia); max 5 fotiek na knihu, uložené v cloudovom úložisku. |

### Používatelia a identita
| **Pojem** | **Anglický názov** | **Definícia** |
|---|---|---|
| **Čitateľ** | Member | Registrovaný používateľ knižnice s aktívnym členstvom, ktorý si môže vypožičiavať a rezervovať knihy. |
| **Knihovník** | Librarian | Používateľ s oprávnením vytvárať výpožičky, vracať knihy, vybavovať rezervácie a uhrádzať pokuty. |
| **Administrátor** | Admin | Správca systému, schopný pridávať/odstraňovať knihy, registrovať čitateľov a odpisovať pokuty. |
| **Konto** | Account | Identita používateľa, spravovaná v identity provideri (Keycloak); v doméne reprezentovaná entitou Member. |
| **Rola** | Role | Oprávnenie priradené čitateľovi v JWT (MEMBER / LIBRARIAN / ADMIN). |
| **Auto-evidencia** | Auto-provisioning | Automatické vytvorenie záznamu Member pri prvom prihlásení používateľa, pomocou údajov z JWT. |

### Členstvo
| **Pojem** | **Anglický názov** | **Definícia** |
|---|---|---|
| **Členstvo** | Membership | Evidovaný vzťah čitateľa s knižnicou s definovanou platnosťou (štandardne 12 mesiacov). |
| **Stav členstva** | Membership Status | Aktuálny stav: `ACTIVE`, `EXPIRED`, `SUSPENDED`. |
| **Expirácia členstva** | Membership Expiry | Dátum, ku ktorému platnosť členstva uplynie. |
| **Blížiaca sa expirácia** | Expiring Soon | Členstvo, ktorému zostáva menej ako 30 dní platnosti — spúšťa notifikáciu. |
| **Obnova členstva** | Membership Renewal | Predĺženie platnosti členstva o ďalších 12 mesiacov. |

### Výpožičky
| **Pojem** | **Anglický názov** | **Definícia** |
|---|---|---|
| **Výpožička** | Loan | Vzťah medzi čitateľom a vypožičanou knihou s definovaným termínom vrátenia. |
| **Stav výpožičky** | Loan Status | `ACTIVE` (v lehote), `OVERDUE` (po termíne), `RETURNED` (vrátená). |
| **Termín vrátenia** | Due Date | Dátum, do ktorého má byť kniha vrátená (loanDate + 14 dní). |
| **Predĺženie** | Renewal | Posunutie termínu vrátenia o ďalších 14 dní (max 1× a len ak nie je rezervácia od iného čitateľa). |
| **Vrátenie knihy** | Return | Zaznamenanie odovzdania vypožičanej knihy späť do knižnice. |
| **Omeškanie** | Overdue | Stav, keď kniha nebola vrátená do termínu; spúšťa výpočet pokuty. |

### Rezervácie
| **Pojem** | **Anglický názov** | **Definícia** |
|---|---|---|
| **Rezervácia** | Reservation | Požiadavka čitateľa na budúcu výpožičku momentálne nedostupnej knihy. |
| **Stav rezervácie** | Reservation Status | `PENDING`, `READY_FOR_PICKUP`, `FULFILLED`, `CANCELLED`, `EXPIRED`. |
| **Fronta rezervácií** | Reservation Queue | Poradie čitateľov čakajúcich na tú istú knihu (`positionInQueue`). |
| **Pripravená rezervácia** | Ready for Pickup | Rezervácia, ktorej kniha bola vrátená a čaká na vyzdvihnutie (expiruje po 3 dňoch). |

### Pokuty a poplatky
| **Pojem** | **Anglický názov** | **Definícia** |
|---|---|---|
| **Pokuta** | Fine | Finančná sankcia za neskoré vrátenie knihy (0,50 € za každý deň omeškania). |
| **Stav pokuty** | Fine Status | `PENDING` (čaká na úhradu), `PAID` (uhradená), `WAIVED` (odpísaná administrátorom). |
| **Úhrada pokuty** | Pay Fine | Zaznamenanie zaplatenia pokuty knihovníkom alebo administrátorom. |
| **Odpísanie pokuty** | Waive Fine | Zrušenie pokuty administrátorom bez platby (napr. ak bola udelená omylom). |
| **Suma** | Money | Hodnota pokuty s menou (value object — amount + currency). |

### Oznamy a komunikácia
| **Pojem** | **Anglický názov** | **Definícia** |
|---|---|---|
| **Oznam** | Announcement | Verejne viditeľný príspevok knižnice (aktuality, podujatia, zmeny otváracích hodín). |
| **Fotka oznamu** | Announcement Photo | Obrázok pripojený k oznamu; max 5 fotiek na oznam. |
| **Notifikácia** | Notification | Automatická správa čitateľovi (rezervovaná kniha je pripravená, blíži sa expirácia členstva). |

### Doménové koncepty
| **Pojem** | **Anglický názov** | **Definícia** |
|---|---|---|
| **Doménová udalosť** | Domain Event | Fakt, ktorý sa stal v doméne a môže spustiť reakcie v iných častiach systému (napr. `BookReturnedEvent`). |
| **Port** | Port | Abstraktné rozhranie definované v doméne, implementované adaptérom (`NotificationPort`, `PhotoStoragePort`). |
| **Adaptér** | Adapter | Technologická implementácia portu (napr. Azure Blob storage adaptér pre `PhotoStoragePort`). |

---

## Use-Case Analýza

---

**UC01 — Vyhľadanie knihy v katalógu**

**Účel** Systém umožní používateľom nájsť knihu v katalógu.

**Používateľ** Verejný návštevník, Čitateľ, Knihovník, Administrátor

**Vstupné podmienky**
- Žiadne — katalóg je verejne prístupný.

**Výstup**
- Zoznam kníh zodpovedajúci vyhľadávacím kritériam.
- Pri každej knihe obrázok obálky, názov, autor a stav dostupnosti.

**Postup**
1. Používateľ otvorí katalóg.
2. Zadá vyhľadávacie kritérium (názov, autor, ISBN), zvolí žáner cez chip filter alebo prepne na "Iba dostupné".
3. Systém v reálnom čase (debounced) filtruje a stránkuje výsledky.
4. Systém zobrazí mriežku kariet alebo tabuľku s ich dostupnosťou.
5. Pri hover nad kartou sa zobrazí náhľad s popisom knihy.
6. Filtre sa zachovajú v URL parametroch — refresh stránky ani zdieľanie linku ich nestratí.

---

**UC02 — Vytvorenie výpožičky**

**Účel** Systém umožní knihovníkovi vytvoriť výpožičku pre čitateľa.

**Používateľ** Knihovník

**Vstupné podmienky**
- Knihovník je prihlásený v systéme.
- Člen prišiel do knižnice s knihami, ktoré si chce vypožičať.

**Výstup**
- Vytvorená výpožička.
- Znížený počet dostupných kópií knihy.

**Postup**
1. Knihovník otvorí modál „Nová výpožička" alebo klikne na tlačidlo „Vypožičať" priamo na karte knihy v katalógu (ISBN sa predvyplní automaticky).
2. Knihovník vyhľadá čitateľa.
3. Systém zobrazí informácie o čitateľovi vrátane prípadných blokovacích faktorov (neplatné členstvo, neuhradené pokuty).
4. Systém validuje pravidlá (platné členstvo, žiadne neuhradené pokuty, dostupnosť kópie).
5. Systém vytvorí výpožičku s dátumom výpožičky a dátumom splatnosti (+ 14 dní).

---

**UC03 — Zaznamenanie vrátenia knihy**

**Účel** Systém umožní knihovníkovi zaznamenať vrátenie vypožičanej knihy.

**Používateľ** Knihovník

**Vstupné podmienky**
- Knihovník je prihlásený v systéme.
- Člen prišiel vrátiť knihu do knižnice.
- Existuje aktívna výpožička pre danú knihu.

**Výstup**
- Zmenený stav výpožičky s dátumom vrátenia.
- Zvýšený počet dostupných kópií knihy.
- Notifikovaný člen s rezerváciou (ak existuje).
- Vypočítaná pokuta (ak bola kniha vrátená po termíne).
- Ak má čitateľ nezaplatené pokuty, systém ponúkne ich okamžitú úhradu.

**Postup**
1. Knihovník otvorí kartu Výpožičky, vyhľadá výpožičku.
2. Knihovník zvolí „Vrátiť". Systém zobrazí potvrdzovací dialóg — pri výpožičke po termíne s upozornením, že bude vytvorená pokuta.
3. Systém skontroluje dátum vrátenia voči dátumu splatnosti.
4. Ak je vrátenie oneskorené, systém vypočíta pokutu (0,50 € × dni omeškania) a priradí ju k účtu čitateľa.
5. Systém zvýši počet dostupných kópií.
6. Ak existuje rezervácia na danú knihu, systém publikuje `BookReturnedEvent` ktorý notifikuje prvého čitateľa v rade.
7. Ak vrátenie bolo po termíne, systém ponúkne knihovníkovi okamžitú úhradu pokuty (otvorí pay-fine modal).

---

**UC04 — Zobrazenie výpožičiek - čítateľ**

**Účel** Systém umožní čitateľovi zobraziť svoje aktívne výpožičky.

**Používateľ** Čitateľ

**Vstupné podmienky**
- Čitateľ je prihlásený.

**Výstup**
- Zoznam aktuálnych výpožičiek s ich stavom a termínmi vrátenia.

**Postup**
1. Čitateľ zvolí v systéme „Moje výpožičky".
2. Systém zobrazí zoznam aktuálnych výpožičiek a ich stavu.

---

---

**UC05 — Zobrazenie výpožičiek - admin, knihovník**

**Účel** Systém umožní adminom a knihovníkom zobraziť všetky aktívne výpožičky a filtrovať medzi nimi.

**Používateľ** Knihovník, Admin

**Vstupné podmienky**
- Knihovník, admin je prihlásený.

**Výstup**
- Zoznam aktuálnych výpožičiek s ich stavom a termínmi vrátenia.

**Postup**
1. Používateľ zvolí v systéme ,,Zobraziť výpožičky".
2. Systém zobrazí zoznam aktuálnych výpožičiek a ich stavu.

---

**UC06 — Rezervovanie nedostupnej knihy**

**Účel** Systém umožní čitateľovi rezervovať si aktuálne nedostupnú knihu.

**Používateľ** Čitateľ

**Vstupné podmienky**
- Čitateľ je prihlásený v systéme.
- Čitateľ má aktívne členstvo.
- Zvolená kniha je momentálne nedostupná.

**Výstup**
- Vytvorená rezervácia s pozíciou vo fronte.

**Postup**
1. Čitateľ vyhľadá v systéme knihu.
2. Systém zobrazí knihu ako nedostupnú.
3. Čitateľ zvolí tlačidlo „Rezervovať".
4. Systém overí platnosť členstva čitateľa.
5. Systém vytvorí rezerváciu a zaradí čitateľa do fronty.
6. Systém uloží rezerváciu.

---

**UC07 — Zobrazenie vlastných rezervácií**

**Účel** Systém umožní čitateľovi zobraziť svoje rezervácie.

**Používateľ** Čitateľ

**Vstupné podmienky**
- Čitateľ je prihlásený v systéme.

**Výstup**
- Zobrazený zoznam aktuálnych rezervácií s ich stavom a pozíciou vo fronte.

**Postup**
1. Čitateľ klikne na „Moje rezervácie".
2. Systém načíta a zobrazí zoznam rezervácií.

---

**UC08 — Zrušenie rezervácie čitateľom**

**Účel** Systém umožní čitateľovi zrušiť vlastnú rezerváciu.

**Používateľ** Čitateľ

**Vstupné podmienky**
- Čitateľ je prihlásený v systéme.
- Čitateľ má aspoň jednu aktívnu rezerváciu.

**Výstup**
- Zrušená zvolená rezervácia.
- Aktualizovaná fronta rezervácií pre danú knihu.

**Postup**
1. Čitateľ klikne na „Moje rezervácie".
2. Systém načíta zoznam rezervácií.
3. Čitateľ zvolí „Zrušiť rezerváciu".
4. Systém zruší rezerváciu a aktualizuje poradie zvyšných rezervácií.

---

**UC09 — Predĺženie výpožičky**

**Účel** Systém umožní čitateľovi predĺžiť aktívnu výpožičku.

**Používateľ** Čitateľ

**Vstupné podmienky**
- Čitateľ je prihlásený v systéme.
- Čitateľ má aktívnu výpožičku, ktorá ešte nie je po termíne.

**Výstup**
- Predĺžená výpožička s novým dátumom splatnosti.

**Postup**
1. Čitateľ zvolí „Moje výpožičky".
2. Systém zobrazí zoznam aktívnych výpožičiek.
3. Čitateľ zvolí „Predĺžiť" pri konkrétnej výpožičke.
4. Systém overí podmienky predĺženia (výpožička nie je po termíne).
5. Systém predĺži dátum splatnosti o ďalších 14 dní a zvýši počítadlo predĺžení.

---

**UC10 — Obnova členstva**

**Účel** Systém umožní adminovi alebo knihovníkovi obnoviť vypršané alebo expirujúce členstvo.

**Používateľ** Čitateľ

**Vstupné podmienky**
- Používateľ je prihlásený v systéme.
- Členstvo čitateľa je aktívne s blížiacim sa koncom platnosti, alebo už vypršalo.

**Výstup**
- Obnovené členstvo s novým dátumom platnosti.

**Postup**
1. Používateľ zvolí „členstvo" alebo klikne na upozornenie o expirujúcom členstve.
2. Systém zobrazí aktuálny stav členstva a možnosti obnovy.
3. Používateľ zvolí typ členstva a potvrdí obnovu.
4. Systém predĺži platnosť členstva a aktualizuje stav.

---

**UC11 — Správa kníh administrátorom**

**Účel** Systém umožní administrátorovi pridávať, upravovať a odstraňovať knihy v katalógu.

**Používateľ** Administrátor

**Vstupné podmienky**
- Administrátor je prihlásený v systéme.

**Výstup**
- Vytvorený, upravený alebo odstránený záznam knihy v katalógu.

**Postup — Pridanie knihy**
1. Administrátor zvolí „Pridať knihu".
2. Administrátor vyplní údaje (ISBN, názov, autor, žáner, vydavateľ, rok vydania, počet kópií).
3. Systém validuje ISBN a unikátnosť záznamu.
4. Systém uloží novú knihu do katalógu.

**Postup — Úprava knihy**
1. Administrátor vyhľadá knihu v katalógu.
2. Administrátor zvolí „Upraviť".
3. Administrátor zmení požadované údaje.
4. Systém uloží zmeny.

**Postup — Odstránenie knihy**
1. Administrátor vyhľadá knihu v katalógu.
2. Administrátor zvolí „Odstrániť".
3. Systém overí, že kniha nemá aktívne výpožičky ani rezervácie.
4. Systém odstráni záznam knihy z katalógu.

---

**UC12 — Registrácia nového čitateľa**

**Účel** Systém umožní registráciu nového čitateľa do systému.

**Používateľ** Administrátor, Knihovník

**Vstupné podmienky**
- Administrátor alebo knihovník je prihlásený v systéme.

**Výstup**
- Vytvorený používateľský účet nového čitateľa s aktívnym členstvom.

**Postup**
1. Administrátor/knihovník zvolí „Registrovať čitateľa".
2. Vyplní osobné údaje (meno, priezvisko, e-mail) a typ členstva (študent, dospelý, senior).
3. Systém overí unikátnosť e-mailovej adresy.
4. Systém vytvorí konto a nastaví platnosť členstva.

---

**UC13 — Zaplatenie pokuty**

**Účel** Systém umožní zaznamenať úhradu pokuty čitateľa.

**Používateľ** Knihovník

**Vstupné podmienky**
- Knihovník je prihlásený v systéme.
- Čitateľ má aspoň jednu neuhradenú pokutu.

**Výstup**
- Pokuta označená ako uhradená.
- Čitateľ odblokovaný pre ďalšie výpožičky (ak nemá ďalšie neuhradené pokuty).

**Postup**
1. Knihovník vyhľadá čitateľa v systéme.
2. Systém zobrazí zoznam neuhradených pokút.
3. Knihovník zvolí „Zaznamenať platbu" pri príslušnej pokute.
4. Systém zmení stav pokuty na uhradenú.
5. Systém skontroluje, či má čitateľ ešte ďalšie neuhradené pokuty a podľa toho aktualizuje jeho stav.

---

**UC14 — Správa používateľských účtov administrátorom**

**Účel** Systém umožní administrátorovi spravovať používateľské účty.

**Používateľ** Administrátor

**Vstupné podmienky**
- Administrátor je prihlásený v systéme.

**Výstup**
- Aktualizovaný, pozastavený alebo odstránený používateľský účet.

**Postup**
1. Administrátor zvolí „Čitatelia".
2. Systém zobrazí zoznam používateľov so štatistikami (celkom, aktívne, expirované, s pokutami).
3. Administrátor klikne na ľubovoľnú stat-card pre rýchle filtrovanie (napr. „S neuhradenými pokutami").
4. Administrátor zvolí účet a vykoná požadovanú akciu (úprava údajov, zmena role, pozastavenie, odstránenie).
5. Systém uloží zmeny.

---

**UC15 — Prehľad vlastného členstva (Môj účet)**

**Účel** Systém poskytne čitateľovi okamžitý prehľad o stave jeho členstva, výpožičiek, rezervácií a pokút.

**Používateľ** Čitateľ

**Vstupné podmienky**
- Čitateľ je prihlásený v systéme.

**Výstup**
- Lišta „Môj účet" pod horným menu, viditeľná na všetkých stránkach.
- Stav členstva (aktívne / expirované) s dátumom expirácie.
- Počet aktívnych výpožičiek + termín najbližšieho vrátenia.
- Počet rezervácií + pripravené na vyzdvihnutie.
- Suma neuhradených pokút (ak nejaké sú).

**Postup**
1. Čitateľ sa prihlási.
2. Systém načíta `/members/me`, `/loans` a `/reservations` (identita členu sa odvodí z JWT).
3. Systém zobrazí kompaktnú farebnú lištu — žlté zafarbenie ak členstvo expiruje do 30 dní, červené pri pokutách alebo expirovanom členstve.
4. Kliknutím na pill „Výpožičky" / „Rezervácie" sa čitateľ presunie na detailnú stránku.

---

**UC16 — Publikovanie oznamu**

**Účel** Systém umožní knihovníkovi/administrátorovi publikovať aktuality a oznamy knižnice.

**Používateľ** Administrátor, Knihovník

**Vstupné podmienky**
- Používateľ je prihlásený v systéme.

**Výstup**
- Verejne viditeľný oznam (videný aj neprihlásenými návštevníkmi).
- Možnosť pripojiť až 5 fotiek.

**Postup**
1. Používateľ otvorí kartu „Oznamy".
2. Zvolí „+ Nový oznam".
3. Vyplní nadpis a obsah a potvrdí.
4. Systém vytvorí oznam; author sa odvodí z JWT.
5. Voliteľne v detaile oznamu pridá fotky (JPG/PNG/WebP/GIF, max 5 MB).
6. Oznam sa okamžite objaví na hlavnej stránke a vo verejnom zozname.

---

**UC17 — Správa fotografií ku knihám**

**Účel** Systém umožní administrátorovi/knihovníkovi pripojiť obálky a fotografie ku knihám.

**Používateľ** Administrátor, Knihovník

**Vstupné podmienky**
- Používateľ je prihlásený v systéme.
- Kniha existuje v katalógu.

**Výstup**
- Fotka uložená v cloudovom úložisku (Azure Blob), URL prepojená s knihou.
- V katalógu sa namiesto generického placeholderu zobrazí skutočná obálka.

**Postup**
1. Používateľ otvorí detail knihy.
2. Zvolí „+ Pridať fotku" (max 5 fotiek/kniha).
3. Vyberie súbor (max 5 MB, JPG/PNG/WebP/GIF).
4. Systém validuje typ a veľkosť, uploadne do Azure Blob a uloží metadata.
5. Fotky sa dajú jednotlivo zmazať cez × tlačidlo v náhľade.

---

## Architektúra

Projekt dodržiava princípy **Domain-Driven Design (DDD)** a **hexagonálnej (porty & adaptéry) architektúry**.

```
fsa-isk/
└── application/
    ├── domain/                       ← Doménový model (jadrá biznis logiky)
    │   └── sk.posam.fsa.isk.domain
    │       ├── catalog            (Book, BookPhoto, ISBN, BookGenre)
    │       ├── announcement       (Announcement, AnnouncementPhoto)
    │       ├── member             (Member, Email, Membership, MemberRole)
    │       ├── lending            (Loan, LoanStatus, BookReturnedEvent)
    │       ├── reservation        (Reservation, ReservationStatus)
    │       ├── finance            (Fine, Money, FineFactory, FineService)
    │       └── shared             (DomainEvent, NotificationPort, PhotoStoragePort)
    │
    ├── api-spec/                     ← OpenAPI 3.0 kontrakt (generuje Java stubs)
    │
    ├── inbound-controller-rest/      ← REST adaptér (Spring MVC)
    │   ├── controller             (CatalogRest, MemberRest, LoanRest, ReservationRest, AnnouncementRest)
    │   ├── mapper                 (Domain ↔ DTO)
    │   ├── security               (Keycloak JWT, CurrentUserDetailService)
    │   └── application            (MemberProvisioningService — JWT → Member identity)
    │
    ├── outbound-repository-jpa/      ← JPA adaptér (Hibernate, PostgreSQL)
    │
    └── springboot/                   ← Composition root, Azure Blob storage, scheduled jobs
        ├── storage                (AzureBlobPhotoStoragePort, NoopPhotoStoragePort)
        └── jobs                   (MembershipExpiryNotificationJob — denný)
```

**Kľúčové porty (interfaces v `domain.shared`):**
- `NotificationPort` — abstrakcia notifikácií (rezervácie ready, expiry warning)
- `PhotoStoragePort` — abstrakcia cloud storage pre fotky
- `DomainEventPublisher` — publikácia doménových udalostí (`BookReturnedEvent`)

**Doménové repozitáre** (`*Repository` interfaces) sú definované v doménovej vrstve a implementované v `outbound-repository-jpa`.

---

## Frontend

Stack: **Angular 18+** (standalone components, signals, `@if`/`@for` control flow), **Bootstrap 5**, **SCSS**.

**Hlavné stránky:**
- `/` — Domov: hero, member-account-bar (pre čitateľov), oznamy, vybrané knihy
- `/catalog` — Katalóg s mriežkou/tabuľkou, chip filtre, search debounce, URL query params
- `/announcements` — Verejne dostupné oznamy
- `/loans`, `/reservations` — Pre staff všetky, pre membera vlastné
- `/members` — Pre staff: stat-cards ako rýchle filtre (active/expired/with fines)

**UX prvky:**
- Vlastný `ConfirmDialog` namiesto natívnych `confirm()`/`alert()`/`prompt()`
- Toast notifikácie na všetky operácie
- Lazy loading obrázkov, skeleton loaders
- Mobile-first responsívnosť, hamburger menu pod 768px
- Filter state zachovaný v URL (deep-linking, refresh-friendly)
- Sticky lišta „Môj účet" pod top navbarom pre prihláseného člena

---

## Bezpečnosť a identita

- **Keycloak** ako Identity Provider; backend overuje JWT cez Spring Security OAuth2 Resource Server.
- Rolové pravidlá deklaratívne v `SecurityConfiguration.java`.
- Identita aktuálneho čitateľa sa vždy odvodí z JWT claimov — frontend nikdy neposiela vlastný `memberId`/`createdById` pre vlastné operácie.
- Pri prvom autentifikovanom dotaze sa MEMBER záznam automaticky vyrobí (`MemberProvisioningService`) — žiadny ručný registračný krok.

---

## Dokumentácia

- **API endpointy:** `docs/api-endpoints/api-documentation.md`
- **Doménový diagram:** `docs/diagrams/isk-domain.puml` (PlantUML)
- **OpenAPI kontrakt:** `application/api-spec/src/main/resources/openapi/isk.yaml`