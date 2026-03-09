# fsa-isk

## Zadanie od zákazníka ##
Naša mestská knižnica by potrebovala moderný informačný systém na správu knižničného fondu a výpožičiek. Systém by mal umožniť čitateľom jednoducho vyhľadávať knihy v katalógu a hneď vidieť, či je kniha dostupná na vypožičanie.
Keď je kniha dostupná, čitateľ by si ju mal vedieť vypožičať prostredníctvom knihovníka. Ak kniha nie je k dispozícii, čitateľ by si ju mal vedieť rezervovať. Osoba, ktorá má knihu aktuálne vypožičanú, si môže výpožičku predĺžiť. Používateľ s rezerváciou dostane automatickú notifikáciu keď je kniha vrátená do knižnice.
Každý čitateľ musí mať v systéme evidované členstvo s platnosťou. Systém by mal automaticky upozorňovať na blížiace sa uplynutie členstva. Čitatelia s vypršaným členstvom alebo neuhradenými pokutami by nemali mať možnosť si knihy vypožičať.
Pri výpožičke sa nastaví dvojtýždňová lehota na vrátenie. Ak čitateľ knihu vráti neskoro, systém by mal automaticky vypočítať pokutu. Knihovníci by v systéme potrebovali sledovať všetky výpožičky, rezervácie a pokuty. Administrátor by mal vedieť pridávať knihy a upravovať informácie o knihách.

## Zber požiadaviek ##

### Správa kníh ##
- **RQ01** Systém umožní pridávanie nových kníh.
- **RQ02** Systém umožní úpravu informácií o knihách.
- **RQ03** Systém zabezpečí sledovanie počtu dostupných kópií jednotlivých kníh.
- **RQ04** Systém umožní vyhľadávanie kníh podľa rôzných kritérií.
- **RQ05** Systém zabezpečí zobrazovanie aktuálneho počtu dostupných kópií.

### Výpožičky ##
- **RQ06** Systém umožní registrovaným používateľom vypožičať si dostupnú knihu.
- **RQ07** Systém zabezpečí sledovanie aktívneho členstva používateľov.
- **RQ08** Systém nastaví vypôžičnú dobu (štandardne 14 dní).
- **RQ09** Systém zabezpečí sledovanie dátumov vypožičky a vrátenia.
- **RQ10** Systém umožní predĺženie vypožičky.
- **RQ11** Systém eviduje vrátenie knihy.

### Rezervácie ##
- **RQ12** Systém umožní rezervovať si knihu, ktorá momentálne nie je dostupná.
- **RQ13** Systém notifikuje používateľa o dostupnosti rezervovanej knihy.

### Členstvo ###
- **RQ14** Systém umožní registráciu nových členov.
- **RQ15** Systém zabezpečí sledovanie typu členstva (študent, dospelý, senior).
- **RQ16** Systém zabezpeči sledovanie plastnosti členstva.
- **RQ17** Systém zabezpečí upozornenie používateľa o blížiacom sa konci členstva.
- **RQ18** Systém zabezpečí blokovanie vypožičiek od používateľov s neplatným členstvom.

### Pokuty a poplatky ###
- **RQ19** Systém zabezpeči pokutovanie za omeškanie doby vrátenia knihy.
- **RQ22** Systém zabezpečí blokovanie vypožičiek od používateľov s neuhradenými pokutami.

### Administrácia ###
- **RQ23** Systém musí mať rôzne používateľské role.

## Slovník pojmov ##
| **Pojem** | **Anglický názov** | **Definícia** |
|-----------|----------------|-------------|
| **Kniha** | Book | Fyzická alebo digitálna publikácia dostupná v knižnici. |
| **Člen** | Member | Registrovaný používateľ knižnice s aktívnym členstvom. |
| **Členstvo** | Membership | Časovo obmedzené oprávnenie na využívanie služieb knižnice. |
| **Vypožička** | Loan | Vzťah medzi členom a vypožičanou knihou v určitom časovom období. |
| **Rezervácia** | Rezervácia | Požiadavka člena na budúcu výpožičku momentálne nedostupnej knihy. |
| **Pokuta** | Fine | Finančná sankcia za neskoré vrátenie knihy alebo poškodenie. |
  
