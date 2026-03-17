# fsa-isk

## Zadanie od zákazníka ##
Naša mestská knižnica by potrebovala moderný informačný systém na správu knižničného fondu a výpožičiek. Systém by mal umožniť čítateľ jednoducho vyhľadávať knihy v katalógu a hneď vidieť, či je kniha dostupná na vypožičanie.
Keď je kniha dostupná, člen by si ju mal vedieť vypožičať skrze knihovníka. Ak kniha nie je k dispozícii, čitateľ by si ju mal vedieť rezervovať. Osoba, ktorá má knihu aktuálne vypožičanú, si môže výpožičku predĺžiť. Používateľ s rezerváciou dostane automatickú notifikáciu keď je kniha vrátená do knižnice.
Každý čitateľ musí mať v systéme evidované členstvo s platnosťou. Systém by mal automaticky upozorňovať na blížiace sa uplynutie členstva. Čitatelia s vypršaným členstvom alebo neuhradenými pokutami by nemali mať možnosť si knihy vypožičať.
Pri výpožičke sa nastaví dvojtýždňová lehota na vrátenie. Ak čitateľ knihu vráti neskoro, systém by mal automaticky vypočítať pokutu. Knihovníci by v systéme potrebovali sledovať všetky výpožičky, rezervácie a pokuty. Administrátor by mal vedieť pridávať knihy a upravovať informácie o knihách.

## Zber požiadaviek ##

### Správa kníh ##
- **RQ01** Systém umožní administratorovi pridávanie a odstraňovanie kníh.
- **RQ02** Systém umožní administatovi úpravu informácií o knihách.
- **RQ03** Systém zabezpečí sledovanie počtu dostupných kópií jednotlivých kníh.
- **RQ04** Systém umožní vyhľadávanie kníh podľa rôzných kritérií.
- **RQ05** Systém zabezpečí zobrazovanie stavu dostupnosti jednotlivých kníh.
- **RQ06** Systém umožní knihovníkovi a administratorovi sledovať aktualne vypožičky a filtrovať medzi nimi.

### Výpožičky ##
- **RQ06** Systém umožní knihovníkovi vytvoriť vypožičku priradením knihy k čitateľovi.
- **RQ07** Systém zabezpečí sledovanie aktívneho členstva čitateľov.
- **RQ08** Systém nastaví vypôžičnú dobu (štandardne 14 dní).
- **RQ09** Systém zabezpečí sledovanie dátumov vypožičky a vrátenia.
- **RQ10** Systém umožní predĺženie vypožičky.
- **RQ11** Systém umožní knihovníkovi v systéme zaznačiť vrátenie knihy.

### Rezervácie ##
- **RQ12** Systém umožní rezervovať si knihu, ktorá momentálne nie je dostupná.
- **RQ13** Systém notifikuje čítateľa o dostupnosti rezervovanej knihy.
- **RQ13** Systém zruší rezerváciu automaticky ak kniha nie je vypožičaná do 3 dní od vrátenia.

### Členstvo ###
- **RQ14** Systém umožní registráciu nových čítateľov.
- **RQ15** Systém zabezpečí sledovanie typu členstva (študent, dospelý, senior).
- **RQ16** Systém zabezpeči sledovanie plastnosti členstva.
- **RQ17** Systém zabezpečí upozornenie čítateľa o blížiacom sa konci členstva.
- **RQ18** Systém zabezpečí blokovanie vypožičiek od čítateľov s neplatným členstvom.

### Pokuty a poplatky ###
- **RQ19** Systém zabezpeči pokutovanie za omeškanie doby vrátenia knihy.
- **RQ22** Systém zabezpečí blokovanie vypožičiek od čítateľov s neuhradenými pokutami.

### Administrácia ###
- **RQ23** Systém umožní administratorovi spravovať použivateľské účty.

## Slovník pojmov ##
| **Pojem** | **Anglický názov** | **Definícia** |
|-----------|----------------|-------------|
| **Kniha** | Book | Fyzická alebo digitálna publikácia dostupná v knižnici. |
| **Čitateľ** | Member | Registrovaný používateľ knižnice s aktívnym členstvom. |
| **Knihovník** | Librarian | Používateľ s oprávnením na vytváranie vypožičiek a sledovanie ich stavu. |
| **Administrátor** | Admin | Správca informačného systému, schopný pridávať, odstranovať a meniť záznamy o knihách. |
| **Konto** | Account | Idendita použivateľa v systéme. |
| **Vypožička** | Loan | Vzťah medzi členom a vypožičanou knihou v určitom časovom období. |
| **Rezervácia** | Rezervácia | Požiadavka člena na budúcu výpožičku momentálne nedostupnej knihy. |
| **Pokuta** | Fine | Finančná sankcia za neskoré vrátenie knihy alebo poškodenie. |
  
## Use-Case Analýza ##
### UC01 ###
**Účel** 
Systém umožní používateľom nájsť knihu v katalógu.
**Používateľ**
 čítateľ, knihovník, administrátor
**Vstupné podmienky** 
- Používateľ je prihlásený do systému.
**Výstup**
- Zoznam kníh zodpovedajúci vyhľadácim kritériam.
- Informácie o dostupnosti vyhľadávaných kníh.
**Postup**
1. Používateľ zadá vyhľadávacie kritérium.
2. Systém vyhľadá knihy zodpovedajúce kritériu v systéme.
3. Systém zobrazí zoznam kníh a ich dostupnosť.

### UC02 ###
**Účel**
Systém umožní knihovníkovi vytvoriť výpožičku pre čítateľa.
**Používateľ**
Knihovník
**Vstupné podmienky** 
- Knihovník je prihlásený v systéme.
- Člen prišiel do knižnice s knihami, ktoré si chce vypožičať.
**Výstup**
- Vytvorená vypožička.
- Znížený počet dostupných kníh.
**Postup**
1. Knihovník vyhľadá člena v systéme.
2. Systém zobrazí informácie o čítateľovi.
3. Knihovník zadá ISBN kníh.
4. Systém validuje business pravidlá (platné členstvo, počet pokút).     
5. Systém vytvorí vypožičkú s dátumom.

### UC03 ###
**Účel**
Systém umožní knihovníkovi zaznamenať vrátenie vypožičanej knihy.
**Používateľ**
Knihovník
**Vstupné podmienky** 
- Knihovník je prihlásený v systéme.
- Člen prišiel do vrátiť knihu do knižnice.
- Existuje aktívna vypožička pre danú knihu.
**Výstup**
- Zmenený stav vypožičky, s dátumom vrátenia.
- Zvýšený počet dostupných kníh.
- Notifikovaný člen s rezerváciou.
**Postup**
1. Knihovník vyhľadá člena v systéme.
2. Systém zobrazí informácie o čítateľovi a aktuálne vypožičky.
3. Knihovník v systéme zaeviduje vrátenie kníh a zmenu stavu.

### UC04 ###
**Účel**
Systém umožní čítateľovi zobraziť svoje aktívne vypožičky.
**Používateľ**
Čítateľ
**Vstupné podmienky** 
- Čítateľ je prihlásený.
**Výstup**
- Zoznam aktualných vypožičiek.
**Postup**
1. Čítateľ zvolí v systéme "moje vypožičky".
2. Systém zobrazí zoznam aktuálných vypožičiek a ich stavu.

### UC05 ###
**Účel**
Systém umožní čítateľovi rezervovať si aktuálne nedostupnú knihu.
**Používateľ**
Čítateľ
**Vstupné podmienky** 
- Čitateľ je prihlásený v systéme.
- Čitateľ ma aktívne členstvo.
- Zobrazené knihy su momentálne nedostupné.
**Výstup**
- Vytvorená rezervácia.
**Postup**
1. Čítateľ vyhľadá v systéme knihu.
2. Systém zobrazí knihu ako nedostupnú.
3. Čitateľ zvolí tlačidlo rezervovať.
4. Systém vytvorí rezerváciu.     
5. Systém uloží rezerváciu.

### UC06 ###
**Účel**
Systém umožní čitateľovi zobraziť svoje rezervácie.
**Používateľ**
Čítateľ
**Vstupné podmienky** 
- Čítateľ je prihlásený v systéme.
**Výstup**
- Zobrazený zoznam aktuálnych rezervácií.
**Postup**
1. Čítateľ klikne na "Moje rezervácie".
2. Systém načíta zoznam rezervácií.

### UC07 ###
**Účel**
Systém umožní čitateľovi zrušenie rezervácie.
**Používateľ**
Čítateľ
**Vstupné podmienky** 
- Čítateľ je prihlásený v systéme.
**Výstup**
- Zrušená zvolená rezervácia.
**Postup**
1. Čítateľ klikne na "Moje rezervácie".
2. Systém načíta zoznam rezervácií.
3. Čítateľ zvolí "Zrušiť rezerváciu".
4. Systém zruší rezerváciu.

### UC08 ###
**Účel**
Systém umožní čitateľovi predlžíť výpožičku.
### UC09 ###
**Účel**
Systém umožní čitateľovi zrušiť rezerváciu
### UC10 ###
**Účel**
Systém umožní čitateľovi obnoviť vypršané alebo exspirujúce členstvo 

