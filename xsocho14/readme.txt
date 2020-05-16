---------------------------------------------------------------------------------------------
Názov: Bus Tracker 
Predmet: IJA - Seminár Java
Dátum: Marec-Máj 2020
Tým: xsocho14
Členovia: Adrián Piaček (xpiace00), Terézia Sochová (xsocho14)
---------------------------------------------------------------------------------------------

Popis aplikácie
Hlavnou funkciou aplikácie je zobrazenie liniek hromadnej dopravy a sledovanie ich pohybu. 

Mapa
Po spustení aplikácie sa načíta mapa, ktorá je vytvorená na základe dát v súbore data.json.
Súbor obsahuje 5 liniek a 25 ulíc. Hlavné komponenty mapy sú ulice, zastávky a autobusy. 
Ulica je reprezentovaná čiernou čiarou a názvom nad čiarou. Zástavka je vykreslená ako modrý 
kruh s názvom v strede. Autobus sa zobrazuje ako červený kruh s názvom svojej linky.

Po kliknutí na konkrétny autobus sa v dolnej časti kontrolného panela zobrazí trasa spoja s 
časmi príchodov na dané zastávky a informácia pri akej zastávke sa nachádza najbližšie. 
Okrem toho sa daná trasa vyznačí žltou farbou na mape. Detail spoja zmizne po opätovnom 
kliknutí v priestore mapy. Ak je nastavená sťažená situácia alebo obchádzka, tak sa čas 
príchodov na dané zastávky prepočíta. 

Autobus sa objaví na mape ak bol dosiahnutý čas začiatku jeho spoja. Po dosiahnutí cieľovej
zastávky autobus zmizne z mapy. 
---------------------------------------------------------------------------------------------

Kontrolný panel
Rozhranie aplikácie ponúka okrem mapy aj kontrolný panel nachádzajúci sa v ľavej časti
okna. Panel obsahuje funkcie:

Zmena rýchlosti času
Vstup očakáva hodnoty od nula po nekonečno. Ak je zadané číslo, ktoré je väčšie ako nula, a
zároveň menšie ako jedna simulované je zrýchlenie pohybu autobusov. Ak je hodnota väčšia ako 
jedna simuluje sa spomalenie. Číslo nula zastaví pohyb na mape a jednotka vráti pohyb do 
pôvodnej rýchlosti. Ak je zadaný neplatný vstup používateľ je na to upozornení. 

Nastavenie sťaženej dopravnej situácie
Používateľ si vyberie ulicu, na ktorej chce vytvoriť sťaženú dopravnú situáciu. Následne
vyberie stupeň sťaženia. Čím vyšší stupeň, tým pomalšia rýchlosť prechodu na danej ulici.
Svoj výber potvrdí tlačidlom v tejto sekcii. Ak chceme zrušiť sťaženie prechodu na danej 
ulici, tak stupeň sťaženia nastavíme na nulu. 

Uzavretie ulice a nastavenie obchádzky 
V tretej sekcii sa vyberie ulica, ktorá bude uzatvorená. Po potvrdení tlačidlom sa otvorí nové 
okno s mapou, na ktorej je vyznačená uzavretá ulica. Používateľ môže vybrať obchádzku 
naklikaním zastávok cez, ktoré chce, aby viedla obchádzková trasa. Následne sleduje zmenený 
pohyb v hlavnom okne aplikácie. Aplikácia nepodporuje undo operáciu zrušenia obchádzky. 
--------------------------------------------------------------------------------------------- 