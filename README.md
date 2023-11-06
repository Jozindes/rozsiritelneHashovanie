**Semestrálna práca z predmetu Algoritmy a údajové štruktúry 2**

_Ide o jednoduchý informačný systém pracujúci na princípe **rozšíriteľného hashovania**._


**Popis systému:**

_Tento informačný systém spravuje a ukladá dáta o nehnuteľnostiach do blokov v súboroch na pevnom disku. Pri práci s dátami sú kľúčové atribúty transofrmované na hash, na základe ktorého sa podľa určených pravidiel vyberie blok, s ktorým sa bude pracovať. V prípade, že sa základný súbor s blokmi naplní, resp. došlo k maximálnemu možnému deleniu blokov, tak sa dáta začnú ukladať do blokov v preplňovacom súbore. Jednou z úloh tejto semestrálnej práce bolo popísať počet prístupov do súborov pri jednotlivých funkcionalitách._

-------------------

**Počet prístupov do súboru**

**Vkladanie**

_Ak je v základnom súbore voľné miesto v bloku: 1 čítanie + 1 zápis v základnom súbore_

_Ak nie je v základnom súbore voľné miesto v bloku a blok nemá pokračovanie v preplňujúcom súbore: 1 čítanie + 1 zápis v základnom súbore, 1 zápis v preplňujúcom súbore_

_Ak nie je v základnom súbore voľné miesto v bloku a blok má pokračovanie v preplňujúcom súbore, v ktorom je voľné miesto: 1 čítanie v základnom súbore, 1 čítanie + 1 zápis v preplňujúcom súbore_

_Ak nie je v základnom súbore voľné miesto v bloku a blok má pokračovanie v preplňujúcom súbore, v ktorom nie je voľné miesto: 1 čítanie v základnom súbore, X čítanií + 2 zápisy v preplňujúcom_

**Vyhľadanie**

_Ak sú dáta v bloku, ktorý sa nachádza v základnom súbore: 1 čítanie v základnom súbore_

_Ak sú dáta v bloku, ktorý sa nachádza v preplňujúcom súbore: 1 čítanie základnom súbore, X čítanií v preplňujúcom súbore (X – počet blokov, ktoré boli načítané do pamäte, kým sa nenašli hľadané dáta)_

**Zmazanie**

_Ak sú dáta v bloku, ktorý sa nachádza v základnom súbore: 1 čítanie v základnom súbore + 1 zápis v základnom súbore_

_Ak sú dáta v bloku, ktorý sa nachádza v základnom súbore a dochádza k spájaniu: 2 čítania + 2 zápisy v základnom súbore_

_Ak sú dáta v bloku, ktorý sa nachádza v základnom súbore a ak dochádza k presunu záznamu z preplňujúceho súboru do základného súboru: 1 čítanie v základnom súbore + 1 zápis v základnom súbore, 1 čítanie v preplňujúcom súbore a 1 zápis v preplňujúcom súbore*_

_Ak sú dáta v bloku, ktorý sa nachádza v preplňujúcom súbore: 1 čítanie v základnom súbore, X čítanií (X – počet blokov, ktoré boli načítané do pamäte, kým sa nenašli hľadané dáta) + 1 zápis v preplňujúcom súbore*_

_* Y čítanií a Y zápisov v preplňujúcom súbore, ak dochádza k striasaniu (Y - počet preplňujúcich blokov, ktoré má blok zo základného súboru)_

**Úprava**

_Ak sú dáta v bloku, ktorý sa nachádza v základnom súbore: 1 čítanie a 1 zápis v základnom súbore_

_Ak sú dáta v bloku, ktorý sa nachádza v preplňujúcom súbore: 1 čítanie základnom súbore, X čítanií a 1 zápis v preplňujúcom súbore (X – počet blokov, ktoré boli načítané do pamäte, kým sa nenašli hľadané dáta)_

**AKÉKOĽVEK KOPÍROVANIE ČASTÍ ALEBO CELÉHO ZDROJOVÉHO KÓDU JE ZAKÁZANÉ!**
