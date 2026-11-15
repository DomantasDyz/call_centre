# Call Centre

Šiame projekte įgyvendinau paprastą skambučių centro prototipą. Sistema leidžia operatoriui matyti jam priklausančius skambučius, pridėti naujus skambučius, priimti kitą skambutį pagal nustatytas prioriteto taisykles, užbaigti aktyvų skambutį ir keisti VIP prioritetą.

Pagrindinę sistemos logiką atskyriau nuo vartotojo sąsajos ir duomenų saugojimo.

## Naudotos technologijos

Projektui naudojau:

- Java 21
- Gradle
- JUnit 5
- SQLite
- SQLite JDBC
- IntelliJ IDEA

## Projekto struktūra

Pagrindinės projekto dalys:

- `Main.java` – komandinės eilutės vartotojo sąsaja ir meniu.
- `Call.java` – skambučio duomenų modelis.
- `CallStatus.java` – skambučio būsenos.
- `CallQueueService.java` – pagrindinė skambučių eilės ir prioriteto logika.
- `CallRepository.java` – repository sąsaja.
- `SQLiteCallRepository.java` – duomenų saugojimas SQLite duomenų bazėje.
- `InMemoryCallRepository.java` – naudojamas automatiniuose testuose.
- `CallQueueServiceTest.java` – automatiniai pagrindinės logikos testai.

## Duomenų bazė

Naudoju SQLite duomenų bazę.

Duomenų bazės failas sukuriamas projekto kataloge:

```text
data/callcentre.db
```

Pirmą kartą paleidus programą lentelė `calls` sukuriama automatiškai.

Kiekvienas skambutis turi:

- ID
- operatoriaus ID
- telefono numerį
- VIP požymį
- sukūrimo laiką
- būseną

Skambučio ID yra unikalus konkretaus operatoriaus ribose.

## Pavyzdiniai duomenys

Sistemoje naudoju du operatorius:

- Domantas
- Ernest

Kiekvienas operatorius turi savo skambučių sąrašą.

Vienas operatorius negali matyti arba keisti kitam operatoriui priklausančių skambučių.

## Pagrindinės sistemos taisyklės

Įgyvendinau šias pagrindines taisykles:

1. Operatorius gali dirbti tik su savo skambučiais.
2. VIP skambutis turi aukštesnį prioritetą už paprastą skambutį.
3. Jei keli skambučiai turi tokį pat prioritetą, pirmiausia parenkamas anksčiausiai sukurtas skambutis.
4. Jei operatorius jau turi aktyvų skambutį, kito skambučio priimti negalima.
5. Jei nėra laukiančių skambučių, sistema pateikia klaidos pranešimą.
6. VIP prioritetą galiu keisti tik tam skambučiui, kuris priklauso šiuo metu pasirinktam operatoriui.

## Projekto paleidimas

Projektą paleidžiu per IntelliJ IDEA.

### Reikalinga aplinka

- JDK 21
- IntelliJ IDEA
- Gradle projektas turi būti sėkmingai užkrautas

### Paleidimo žingsniai

1. Atidarau projektą IntelliJ IDEA.
2. Palaukiu, kol Gradle užkraus visas projekto priklausomybes.
3. Atidarau failą:

```text
src/main/java/lt/callcentre/Main.java
```

4. Spaudžiu žalią `Run` mygtuką prie `main()` metodo.
5. Pasirenku:

```text
Run 'Main.main()'
```

Po paleidimo IntelliJ `Run` lange parodomas programos CLI meniu.

## Programos meniu

Paleidus programą galiu:

```text
1. Rodyti aktyvius skambučius
2. Pridėti naują skambutį
3. Atsiliepti į sekantį skambutį
4. Nutraukti aktyvų skambutį
5. Persijungti į kitą operatorių
6. Keisti skambučio prioritetą (VIP)
0. Baigti darbą
```

## Automatinių testų paleidimas

Automatinius testus galiu paleisti per IntelliJ.

Atidarau:

```text
src/test/java/lt/callcentre/service/CallQueueServiceTest.java
```

ir spaudžiu žalią `Run` mygtuką prie testų klasės.

Taip pat testus galima paleisti Gradle lange pasirinkus:

```text
Tasks -> verification -> test
```

## Automatiniai testai

Testais tikrinu:

- ar iš dviejų paprastų skambučių parenkamas seniausias;
- ar VIP skambutis turi prioritetą prieš paprastą skambutį;
- ar gaunama klaida, kai nėra laukiančių skambučių;
- ar vienas operatorius negali pasiekti kito operatoriaus duomenų;
- ar negalima priimti antro skambučio, kai vienas jau yra aktyvus.

## Demonstravimo žingsniai

Atsiskaitymo metu planuoju parodyti tokį scenarijų:

1. Paleidžiu programą per IntelliJ.
2. Parodau pirmo operatoriaus skambučių sąrašą.
3. Parodau, kad VIP skambutis parenkamas pirmiau už paprastą.
4. Bandau priimti dar vieną skambutį, kai vienas jau aktyvus, ir parodau klaidos pranešimą.
5. Užbaigiu aktyvų skambutį.
6. Persijungiu į kitą operatorių.
7. Parodau, kad kitas operatorius mato tik savo skambučius.
8. Bandau pakeisti kito operatoriaus skambučio duomenis ir parodau, kad sistema to neleidžia.
9. Paleidžiu automatinius testus ir parodau, kad jie sėkmingai veikia.

## Konfigūracija

Papildomų API raktų ar išorinių servisų projektui nereikia.

SQLite duomenų bazė sukuriama automatiškai, todėl atskiro duomenų bazės serverio paleisti nereikia.
