# fsa-isk

## Zadanie od zákazníka

Naša mestská knižnica by potrebovala moderný informačný systém na správu knižničného fondu a výpožičiek. Systém by mal umožniť čitateľom jednoducho vyhľadávať knihy v katalógu a hneď vidieť, či je kniha dostupná na vypožičanie.
Keď je kniha dostupná, člen by si ju mal vedieť vypožičať skrze knihovníka. Ak kniha nie je k dispozícii, čitateľ by si ju mal vedieť rezervovať. Osoba, ktorá má knihu aktuálne vypožičanú, si môže výpožičku predĺžiť. Používateľ s rezerváciou dostane automatickú notifikáciu keď je kniha vrátená do knižnice.
Každý čitateľ musí mať v systéme evidované členstvo s platnosťou. Systém by mal automaticky upozorňovať na blížiace sa uplynutie členstva. Čitatelia s vypršaným členstvom alebo neuhradenými pokutami by nemali mať možnosť si knihy vypožičať.
Pri výpožičke sa nastaví dvojtýždňová lehota na vrátenie. Ak čitateľ knihu vráti neskoro, systém by mal automaticky vypočítať pokutu. Knihovníci by v systéme potrebovali sledovať všetky výpožičky, rezervácie a pokuty. Administrátor by mal vedieť pridávať knihy a upravovať informácie o knihách.

---

## Zber požiadaviek

### Správa kníh
- **RQ01** Systém umožní administratorovi pridávanie a odstraňovanie kníh.
- **RQ02** Systém umožní administatovi úpravu informácií o knihách.
- **RQ03** Systém zabezpečí sledovanie počtu dostupných kópií jednotlivých kníh.
- **RQ04** Systém umožní vyhľadávanie kníh podľa rôznych kritérií.
- **RQ05** Systém zabezpečí zobrazovanie stavu dostupnosti jednotlivých kníh.
- **RQ06** Systém umožní knihovníkovi a administratorovi sledovať aktuálne výpožičky a filtrovať medzi nimi.

### Výpožičky
- **RQ07** Systém umožní knihovníkovi vytvoriť výpožičku priradením knihy k čitateľovi.
- **RQ08** Systém zabezpečí sledovanie aktívneho členstva čitateľov.
- **RQ09** Systém nastaví výpožičnú dobu (štandardne 14 dní).
- **RQ10** Systém zabezpečí sledovanie dátumov výpožičky a vrátenia.
- **RQ11** Systém umožní predĺženie výpožičky.
- **RQ12** Systém umožní knihovníkovi v systéme zaznačiť vrátenie knihy.

### Rezervácie
- **RQ13** Systém umožní rezervovať si knihu, ktorá momentálne nie je dostupná.
- **RQ14** Systém notifikuje čitateľa o dostupnosti rezervovanej knihy.
- **RQ15** Systém zruší rezerváciu automaticky ak kniha nie je vypožičaná do 3 dní od vrátenia.

### Členstvo
- **RQ16** Systém umožní registráciu nových čitateľov.
- **RQ17** Systém zabezpečí sledovanie typu členstva (študent, dospelý, senior).
- **RQ18** Systém zabezpečí sledovanie platnosti členstva.
- **RQ19** Systém zabezpečí upozornenie čitateľa o blížiacom sa konci členstva.
- **RQ20** Systém zabezpečí blokovanie výpožičiek od čitateľov s neplatným členstvom.
- **RQ21** Systém umožní čitateľovi obnoviť členstvo.

### Pokuty a poplatky
- **RQ22** Systém zabezpečí pokutovanie za omeškanie doby vrátenia knihy.
- **RQ23** Systém umožní knihovníkovi zaznamenať úhradu pokuty.
- **RQ24** Systém zabezpečí blokovanie výpožičiek od čitateľov s neuhradenými pokutami.

### Administrácia
- **RQ25** Systém umožní administratorovi spravovať používateľské účty.

---

## Slovník pojmov

| **Pojem** | **Anglický názov** | **Definícia** |
|---|---|---|
| **Kniha** | Book | Fyzická alebo digitálna publikácia dostupná v knižnici. |
| **Katalóg** | Catalog | Evidencia všetkých kníh dostupných v knižnici. |
| **ISBN** | ISBN | Medzinárodný štandardný identifikátor knihy. |
| **Čitateľ** | Member | Registrovaný používateľ knižnice s aktívnym členstvom. |
| **Knihovník** | Librarian | Používateľ s oprávnením na vytváranie výpožičiek a sledovanie ich stavu. |
| **Administrátor** | Admin | Správca informačného systému, schopný pridávať, odstraňovať a meniť záznamy o knihách a účtoch. |
| **Konto** | Account | Identita používateľa v systéme. |
| **Členstvo** | Membership | Evidovaný vzťah čitateľa s knižnicou s definovanou platnosťou a typom. |
| **Výpožička** | Loan | Vzťah medzi členom a vypožičanou knihou v určitom časovom období. |
| **Predĺženie** | Renewal | Predĺženie aktívnej výpožičky o ďalšie obdobie. |
| **Rezervácia** | Reservation | Požiadavka člena na budúcu výpožičku momentálne nedostupnej knihy. |
| **Fronta rezervácií** | Reservation Queue | Poradie čitateľov čakajúcich na tú istú knihu. |
| **Pokuta** | Fine | Finančná sankcia za neskoré vrátenie knihy alebo poškodenie. |

---

## Use-Case Analýza

---

**UC01 — Vyhľadanie knihy v katalógu**

**Účel** Systém umožní používateľom nájsť knihu v katalógu.

**Používateľ** Čitateľ, Knihovník, Administrátor

**Vstupné podmienky**
- Používateľ je prihlásený do systému.

**Výstup**
- Zoznam kníh zodpovedajúci vyhľadávacím kritériam.
- Informácie o dostupnosti vyhľadávaných kníh.

**Postup**
1. Používateľ zadá vyhľadávacie kritérium (názov, autor, ISBN, žáner).
2. Systém vyhľadá knihy zodpovedajúce kritériu v katalógu.
3. Systém zobrazí zoznam kníh a ich dostupnosť.

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
1. Knihovník vyhľadá člena v systéme.
2. Systém zobrazí informácie o čitateľovi.
3. Knihovník zadá ISBN kníh.
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

**Postup**
1. Knihovník vyhľadá člena v systéme.
2. Systém zobrazí informácie o čitateľovi a aktuálne výpožičky.
3. Knihovník zaeviduje vrátenie kníh.
4. Systém skontroluje dátum vrátenia voči dátumu splatnosti.
5. Ak je vrátenie oneskorené, systém vypočíta pokutu a priradí ju k účtu čitateľa.
6. Systém zvýši počet dostupných kópií.
7. Ak existuje rezervácia na danú knihu, systém notifikuje príslušného čitateľa.

---

**UC04 — Zobrazenie vlastných výpožičiek**

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

**UC05 — Rezervovanie nedostupnej knihy**

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

**UC06 — Zobrazenie vlastných rezervácií**

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

**UC07 — Zrušenie rezervácie čitateľom**

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

**UC08 — Predĺženie výpožičky**

**Účel** Systém umožní čitateľovi predĺžiť aktívnu výpožičku.

**Používateľ** Čitateľ

**Vstupné podmienky**
- Čitateľ je prihlásený v systéme.
- Čitateľ má aktívnu výpožičku, ktorá ešte nie je po termíne.
- Na danú knihu neexistuje aktívna rezervácia iného čitateľa.

**Výstup**
- Predĺžená výpožička s novým dátumom splatnosti.

**Postup**
1. Čitateľ zvolí „Moje výpožičky".
2. Systém zobrazí zoznam aktívnych výpožičiek.
3. Čitateľ zvolí „Predĺžiť" pri konkrétnej výpožičke.
4. Systém overí podmienky predĺženia (žiadna rezervácia, výpožička nie je po termíne).
5. Systém predĺži dátum splatnosti o ďalších 14 dní a zvýši počítadlo predĺžení.

---

**UC09 — Obnova členstva**

**Účel** Systém umožní čitateľovi obnoviť vypršané alebo expirujúce členstvo.

**Používateľ** Čitateľ

**Vstupné podmienky**
- Čitateľ je prihlásený v systéme.
- Členstvo čitateľa je aktívne s blížiacim sa koncom platnosti, alebo už vypršalo.

**Výstup**
- Obnovené členstvo s novým dátumom platnosti.

**Postup**
1. Čitateľ zvolí „Moje členstvo" alebo klikne na upozornenie o expirujúcom členstve.
2. Systém zobrazí aktuálny stav členstva a možnosti obnovy.
3. Čitateľ zvolí typ členstva a potvrdí obnovu.
4. Systém predĺži platnosť členstva a aktualizuje stav.

---

**UC10 — Správa kníh administrátorom**

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

**UC11 — Registrácia nového čitateľa**

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

**UC12 — Zaplatenie pokuty**

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

**UC13 — Správa používateľských účtov administrátorom**

**Účel** Systém umožní administrátorovi spravovať používateľské účty.

**Používateľ** Administrátor

**Vstupné podmienky**
- Administrátor je prihlásený v systéme.

**Výstup**
- Aktualizovaný, pozastavený alebo odstránený používateľský účet.

**Postup**
1. Administrátor zvolí „Správa účtov".
2. Systém zobrazí zoznam používateľov s možnosťou filtrovania.
3. Administrátor zvolí účet a vykoná požadovanú akciu (úprava údajov, zmena role, pozastavenie, odstránenie).
4. Systém uloží zmeny.