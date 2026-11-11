# 🧮 Windows 11 Skaičiuotuvas (Android / Jetpack Compose)

Moderni, greita ir patikima **Android** skaičiuotuvo programėlė, sukurta su **Jetpack Compose** (Material 3). Programėlės vartotojo sąsaja, mygtukų išdėstymas, spalvų paletė bei matematinė elgsena atkartoja operacinės sistemos „Windows 11 Calculator“ standartinį (Standard) režimą.

---

## 📋 Turinys

- [Trumpas aprašymas](#-trumpas-aprašymas)
- [Funkcijų sąrašas](#-funkcijų-sąrašas)
  - [Bazinės matematinės operacijos](#bazinės-matematinės-operacijos)
  - [Papildomos ir unarinės operacijos](#papildomos-ir-unarinės-operacijos)
  - [Atminties (Memory) valdymas](#atminties-memory-valdymas)
  - [Skaičiavimų istorija (History)](#skaičiavimų-istorija-history)
  - [Klaidų apdorojimas](#klaidų-apdorojimas)
- [Naudojimo instrukcija](#-naudojimo-instrukcija)
  - [Paprasti skaičiavimai ir prioritetai](#paprasti-skaičiavimai-ir-prioritetai)
  - [Papildomų funkcijų naudojimas](#papildomų-funkcijų-naudojimas)
  - [Darbas su atmintimi](#darbas-su-atmintimi)
  - [Istorijos peržiūra ir panaudojimas](#istorijos-peržiūra-ir-panaudojimas)
  - [Įvesties taisymas (CE, C, ⌫)](#įvesties-taisymas-ce-c-)
- [Diegimo ir paleidimo instrukcija per Android Studio](#-diegimo-ir-paleidimo-instrukcija-per-android-studio)
  - [Sisteminiai reikalavimai](#sisteminiai-reikalavimai)
  - [Žingsniai paleidimui](#žingsniai-paleidimui)
  - [Automatinių testų vykdymas](#automatinių-testų-vykdymas)
- [Projekto architektūra ir struktūra](#-projekto-architektūra-ir-struktūra)
- [Kaip pridėti naują kalbą (Lokalizacija)](#-kaip-pridėti-naują-kalbą-lokalizacija)
- [Naudotos technologijos ir bibliotekos](#-naudotos-technologijos-ir-bibliotekos)
- [Pritaikymas neįgaliesiems (Accessibility)](#-pritaikymas-neįgaliesiems-accessibility)

---

## 📖 Trumpas aprašymas

Ši programėlė siūlo pažįstamą ir patogią „Windows 11“ skaičiuotuvo patirtį išmaniuosiuose telefonuose bei planšetiniuose kompiuteriuose su „Android“. 

Programėlėje įdiegtas nepriklausomas, nuo nulio parašytas matematinis variklis (`CalculatorEngine`), kuris atlieka skaičiavimus naudodamas `java.math.BigDecimal`. Tai užtikrina absoliutų skaičiavimų tikslumą ir apsaugo nuo standartinių slankiojo kablelio (angl. *floating-point*) apvalinimo netikslumų (pavyzdžiui, `0.1 + 0.2` visada lygus tiksliai `0.3`).

---

## ✨ Funkcijų sąrašas

### Bazinės matematinės operacijos
- **Sudėtis (`+`)**, **Atimtis (`−`)**, **Daugyba (`×`)**, **Dalyba (`÷`)**.
- **Algebrinis operacijų eiliškumas (PEMDAS)**: Daugyba ir dalyba vykdomos pirmiau nei sudėtis ir atimtis (pvz., išraiškos `2 + 3 × 4` rezultatas yra `14`, o ne `20`).
- **Pakartotinis lygybės (`=`) paspaudimas**: Pakartotinai spaudžiant lygybės ženklą, automatiškai kartojamas paskutinis veiksmas su tuo pačiu operandu (pvz., `5 + 2 = 7`, vėl paspaudus `=` gausite `9`, dar kartą paspaudus – `11`).
- **Skaičių įvedimas iki 16 reikšminių skaitmenų**.

### Papildomos ir unarinės operacijos
- **Procentai (`%`)**: Išmanus procentų apskaičiavimas pagal atliekamą operaciją:
  - Sudėtyje / atimtyje veikia kaip procentinė dalis nuo pradinės sumos (pvz., `200 + 10% = 220`, `200 − 10% = 180`).
  - Daugyboje / dalyboje paverčia skaičių trupmena (pvz., `50 × 10% = 5`).
- **Atvirkštinis skaičius (`1/x`)**: Apskaičiuoja $1 / x$ reikšmę.
- **Kėlimas kvadratu (`x²`)**: Pakelia einamąją reikšmę kvadratu (`sqr(x)`).
- **Kvadratinė šaknis (`²√x`)**: Tikslus kvadratinės šaknies traukimas naudojant *Newton-Raphson* aproksimacijos algoritmą (`√(x)`).
- **Ženklo keitimas (`+/−`)**: Pakeičia skaičiaus ženklą iš teigiamo į neigiamą arba atvirkščiai.
- **Dešimtainis kablelis / taškas (`.`)**: Trupmeninių skaičių įvedimui (su apsauga nuo kelių taškų įvedimo).

### Atminties (Memory) valdymas
Skaičiuotuvas turi pilnavertį atminties registrą su galimybe saugoti kelias reikšmes:
- **`MC` (Memory Clear)**: Išvalo visas atmintyje esančias reikšmes.
- **`MR` (Memory Recall)**: Iškviečia paskutinę (viršutinę) atminties reikšmę į ekraną.
- **`M+` (Memory Add)**: Prideda rodomą skaičių prie paskutinės atminties reikšmės.
- **`M−` (Memory Subtract)**: Atima rodomą skaičių iš paskutinės atminties reikšmės.
- **`MS` (Memory Store)**: Įrašo naują reikšmę į atminties sąrašą.
- **`M˅` (Memory Flyout)**: Atidaro apatinį skydelį (`BottomSheet`), kuriame matomas visas išsaugotų reikšmių sąrašas su galimybe atkurti pasirinktą reikšmę arba ištrinti visą atmintį.
- *Pastaba*: kai atmintyje nieko nėra, `MC` ir `MR` mygtukai yra pasyvūs (pilki ir nepaspaudžiami).

### Skaičiavimų istorija (History)
- Kiekvienas atliktas skaičiavimas (pvz., `125 × 8 = 1000`) automatiškai registruojamas istorijoje.
- Paspaudus viršutinėje juostoje esantį **Istorijos** mygtuką (laikrodžio / atsukimo piktogramą), iš apačios išvažiuoja skydelis su ankstesniais skaičiavimais.
- **Interaktyvumas**: Paspaudus ant bet kurio istorijos įrašo, jo išraiška ir rezultatas grąžinami į pagrindinį skaičiuotuvo ekraną.
- **Išvalymas**: Viršutinis šiukšliadėžės mygtukas leidžia vienu paspaudimu išvalyti visą istoriją.
- **Išsaugojimas (Persistence)**: Istorija ir atmintis išsaugoma įrenginio vidinėje atmintyje (`SharedPreferences`), todėl duomenys nedingsta išjungus programėlę (saugoma iki 100 paskutinių įrašų).

### Klaidų apdorojimas
Programėlė korektiškai atpažįsta ir vizualiai praneša apie matematines klaidas:
- **Dalyba iš nulio** („Dalyba iš nulio negalima“ / „Cannot divide by zero“).
- **Šaknis iš neigiamo skaičiaus** („Netinkama įvestis“ / „Invalid input“).
- **Skaitinis perpildymas** („Perpildymas“ / „Overflow“).
- Esant klaidai, atminties mygtukai ir tolimesni veiksmai apsaugomi nuo nekorektiško elgesio.

---

## 📱 Naudojimo instrukcija

### Paprasti skaičiavimai ir prioritetai
1. Įveskite skaičių (pvz., `1`, `0`).
2. Paspauskite norimą veiksmą: `+`, `−`, `×` arba `÷`. Viršutiniame ekrane pamatysite vykdomą išraišką.
3. Įveskite kitą skaičių.
4. Paspauskite `=` norėdami pamatyti galutinį rezultatą.
5. **PEMDAS pavyzdys**:
   - Paspauskite `2` $\rightarrow$ `+` $\rightarrow$ `3` $\rightarrow$ `×` $\rightarrow$ `4` $\rightarrow$ `=`.
   - Rezultatas: **`14`** (programėlė pirma atlieka `3 × 4 = 12`, o po to `2 + 12 = 14`).

### Papildomų funkcijų naudojimas
- **Kvadratas**: Įveskite `5`, paspauskite `x²` $\rightarrow$ Ekrane rodoma `25`, o viršuje `sqr(5)`.
- **Kvadratinė šaknis**: Įveskite `81`, paspauskite `²√x` $\rightarrow$ Ekrane rodoma `9`, viršuje `√(81)`.
- **Atvirkštinė reikšmė**: Įveskite `4`, paspauskite `1/x` $\rightarrow$ Ekrane rodoma `0.25`, viršuje `1/(4)`.
- **Procentai**:
  - Norite prie 500 pridėti 15% PVM?
  - Spauskite: `500` $\rightarrow$ `+` $\rightarrow$ `15` $\rightarrow$ `%` (ekrane matysite `75`) $\rightarrow$ `=` (rezultatas: `575`).

### Darbas su atmintimi
1. Suskaičiuokite reikšmę (pvz., `150`).
2. Paspauskite `MS` – reikšmė įrašyta į atmintį (mygtukai `MC` ir `MR` tampa aktyvūs).
3. Atlikite kitus skaičiavimus.
4. Norėdami atimti naują rezultatą (pvz., `50`) iš išsaugotos atminties, spauskite `M−`.
5. Paspauskite `MR` – ekrane atsiras atnaujinta atminties reikšmė (`100`).
6. Paspauskite `M˅` – pamatysite visas išsaugotas reikšmes sąraše.

### Istorijos peržiūra ir panaudojimas
1. Ekrano viršuje dešinėje spustelėkite istorijos piktogramą.
2. Slinkite per atliktų skaičiavimų sąrašą.
3. Spustelėkite bet kurį įrašą – jis bus įkeltas į skaičiuotuvo langą tolesniam redagavimui ar skaičiavimui.

### Įvesties taisymas (CE, C, ⌫)
- **`⌫` (Backspace)**: Ištrina paskutinį įvestą skaitmenį.
- **`CE` (Clear Entry)**: Išvalo tik einamojo skaičiaus įvestį, neanuliuodamas jau pradėtos išraiškos (pvz., suvedus `50 + 999`, paspaudus `CE` išsivalo tik `999`, o `50 +` lieka).
- **`C` (Clear)**: Pilnas atstatymas – išvalo visą formulę ir sugrąžina skaičiuotuvą į pradinę būseną `0`.

---

## 🚀 Diegimo ir paleidimo instrukcija per Android Studio

### Sisteminiai reikalavimai
- **Android Studio**: Jellyfish (2023.3.1), Koala (2024.1.1) arba naujesnė versija.
- **Java Development Kit (JDK)**: Versija 17 (paprastai įtraukta į Android Studio).
- **Android SDK**:
  - Minimali palaikoma versija (`minSdk`): **24** (Android 7.0 Nougat).
  - Tikslinė versija (`targetSdk`): **34** (Android 14).
  - Kompiliavimo versija (`compileSdk`): **34**.
- **Įrenginys testavimui**: Fizinis telefonas su įjungtu „USB Debugging“ arba sukonfigūruotas emuliatorius (AVD) su API 24 ar naujesne versija.

### Žingsniai paleidimui

1. **Atidarykite projektą**:
   - Paleiskite programą **Android Studio**.
   - Pasveikinimo lange pasirinkite **Open** (arba meniu: `File` $\rightarrow$ `Open...`).
   - Naršyklėje suraskite ir pasirinkite projekto šakninį aplanką `calc`.
   - Spustelėkite **OK**.

2. **Gradle sinchronizavimas**:
   - Android Studio automatiškai pradės `Gradle Sync` procesą ir parsiųs reikalingas priklausomybes.
   - Jei procesas neprasideda automatiškai, spustelėkite drambliuko piktogramą **Sync Project with Gradle Files** viršutinėje įrankių juostoje.
   - Sulaukite pranešimo apatinėje būsenos juostoje: `Gradle sync finished in ...`.

3. **Paruoškite įrenginį / emuliatorių**:
   - Viršutinėje įrankių juostoje, įrenginių pasirinkimo laukelyje (šalia žalio paleidimo mygtuko), pasirinkite virtualų emuliatorių (AVD) arba prijungtą fizinį telefoną.
   - Jei emuliatorius dar nesukurtas, eikite į `Tools` $\rightarrow$ `Device Manager` $\rightarrow$ `Create Device` (rekomenduojama pasirinkti pvz., *Pixel 7*, API 34).

4. **Paleiskite programėlę**:
   - Spustelėkite žalią paleidimo mygtuką **Run 'app'** (▶) arba klaviatūros trumpinį:
     - **Windows / Linux**: `Shift + F10`
     - **macOS**: `Control + R`
   - Programėlė bus sukompiliuota, įdiegta ir automatiškai pasileis ekrane.

### Automatinių testų vykdymas

Projekte parašyti išsamūs vienetų testai (Unit tests), tikrinantys matematinį variklį (`CalculatorEngineTest`) ir ViewModel būsenų valdymą (`CalculatorViewModelTest`).

Testus galima paleisti keliais būdais:
- **Per Android Studio vartotojo sąsają**:
  1. Projekto lange kairėje atverkite: `app/src/test/java/com/example/win11calculator/`.
  2. Spustelėkite dešiniuoju pelės mygtuku ant failo ar aplanko.
  3. Pasirinkite **Run 'Tests in 'win11calculator''** (arba `Ctrl + Shift + F10`).
- **Per terminalą (Gradle)**:
  ```bash
  ./gradlew test
  ```
  *(Windows sistemoje: `gradlew.bat test`)*

---

## 🏗 Projekto architektūra ir struktūra

Projektas sukurtas vadovaujantis oficialiomis **Modern Android Development (MAD)** rekomendacijomis ir **MVVM (Model-View-ViewModel)** šablonu:

```
calc/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/win11calculator/
│   │   │   │   │
│   │   │   │   ├── MainActivity.kt               # Pagrindinė Activity, kraunanti Compose sąsają
│   │   │   │   │
│   │   │   │   ├── data/                         # Duomenų sluoksnis (Modeliai ir saugykla)
│   │   │   │   │   ├── HistoryItem.kt            # Istorijos įrašo duomenų klasė
│   │   │   │   │   ├── MemoryItem.kt             # Atminties įrašo duomenų klasė
│   │   │   │   │   └── CalculatorRepository.kt   # Repozitorijos sąsaja ir SharedPreferences realizacija
│   │   │   │   │
│   │   │   │   ├── engine/                       # Grynoji matematinė logika (atskirta nuo UI)
│   │   │   │   │   ├── CalculatorEngine.kt       # Skaičiuotuvo būsenų automatas ir įvesties apdorojimas
│   │   │   │   │   ├── ExpressionEvaluator.kt    # PEMDAS algebrinis skaičiuotuvas su BigDecimal
│   │   │   │   │   └── Operation.kt              # Operacijų enum'ai, prioritetai ir klaidos
│   │   │   │   │
│   │   │   │   └── ui/                           # Vartotojo sąsajos sluoksnis (Jetpack Compose)
│   │   │   │       ├── CalculatorScreen.kt       # Pagrindinis skaičiuotuvo langas ir mygtukų tinklelis
│   │   │   │       ├── CalculatorViewModel.kt    # Valdo būsenas (StateFlow) ir komunikuoja su Engine
│   │   │   │       │
│   │   │   │       ├── components/               # Daugkartinio naudojimo Compose komponentai
│   │   │   │       │   ├── CalculatorButton.kt   # Windows 11 stiliaus mygtukas su animacija ir šešėliais
│   │   │   │       │   ├── DisplayHeader.kt      # Viršutinė antraštė, pirminis ir antrinis ekranai
│   │   │   │       │   ├── HistoryBottomSheet.kt # Istorijos peržiūros modalinis skydelis
│   │   │   │       │   ├── MemoryBar.kt          # Atminties valdymo mygtukų juosta (MC, MR, M+...)
│   │   │   │       │   └── MemoryBottomSheet.kt  # Išsaugotų atminties reikšmių skydelis
│   │   │   │       │
│   │   │   │       └── theme/                    # Windows 11 Fluent dizaino temos
│   │   │   │           ├── Color.kt              # Spalvų paletė (Fluent Light/Dark akcentai)
│   │   │   │           ├── Theme.kt              # Material 3 temos konfigūracija
│   │   │   │           └── Type.kt               # Tipografija ir šriftai
│   │   │   │
│   │   │   └── res/                              # Resursai
│   │   │       ├── values/strings.xml            # Anglų kalbos tekstiniai resursai (numatytieji)
│   │   │       ├── values-lt/strings.xml         # Lietuvių kalbos tekstiniai resursai
│   │   │       └── mipmap-.../                   # Programėlės piktogramos
│   │   │
│   │   └── test/java/com/example/win11calculator/# Unit testai
│   │       ├── CalculatorEngineTest.kt           # Matematinių taisyklių ir PEMDAS testai
│   │       └── CalculatorViewModelTest.kt        # Būsenų sinchronizacijos ir saugyklos testai
│   │
│   └── build.gradle.kts                          # Modulio lygio Gradle konfigūracija
├── build.gradle.kts                              # Šakninis Gradle failas
└── settings.gradle.kts                           # Projekto modulio nustatymai
```

---

## 🌍 Kaip pridėti naują kalbą (Lokalizacija)

Programėlė palaiko standartinį „Android“ lokalizacijos mechanizmą per resursų failus `strings.xml`. Šiuo metu yra palaikomos **anglų** (`values/`) ir **lietuvių** (`values-lt/`) kalbos.

Norėdami pridėti naują kalbą (pavyzdžiui, **lenkų k. `pl`**, **vokiečių k. `de`** ar **prancūzų k. `fr`**), atlikite šiuos veiksmus:

### 1 žingsnis: Sukurkite naują reikšmių aplanką
Projekto kataloge `app/src/main/res/` sukurkite naują aplanką su atitinkamu kalbos kodu:
- Lenkų kalbai: `values-pl`
- Vokiečių kalbai: `values-de`
- Ispanų kalbai: `values-es`

### 2 žingsnis: Sukurkite `strings.xml` failą
Naujame aplanke sukurkite failą `strings.xml`.

### 3 žingsnis: Nukopijuokite ir išverskite tekstinius raktus
Pavyzdys lenkų kalbai (`app/src/main/res/values-pl/strings.xml`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Programėlės antraštės -->
    <string name="app_name">Kalkulator</string>
    <string name="mode_standard">Standardowy</string>
    <string name="history_title">Historia</string>
    <string name="memory_title">Pamięć</string>
    <string name="history_empty">Brak historii</string>
    <string name="memory_empty">Brak zapisanych wartości w pamięci</string>
    <string name="clear_history">Wyczyść całą historię</string>
    <string name="clear_memory">Wyczyść pamięć</string>

    <!-- Klaidų pranešimai -->
    <string name="error_div_zero">Nie można dzielić przez zero</string>
    <string name="error_invalid_input">Nieprawidłowe dane wejściowe</string>
    <string name="error_overflow">Przepełnienie</string>

    <!-- Mygtukų pritaikymo neįgaliesiems (TalkBack) aprašai -->
    <string name="btn_mc">Wyczyść pamięć</string>
    <string name="btn_mr">Przywołaj z pamięci</string>
    <string name="btn_m_plus">Dodaj do pamięci</string>
    <string name="btn_m_minus">Odejmij od pamięci</string>
    <string name="btn_ms">Zapisz w pamięci</string>
    <string name="btn_mv">Pokaż pamięć</string>
    <string name="btn_percent">Procent</string>
    <string name="btn_ce">Wyczyść wpis</string>
    <string name="btn_c">Wyczyść wszystko</string>
    <string name="btn_backspace">Backspace</string>
    <string name="btn_reciprocal">Odwrotność (1/x)</string>
    <string name="btn_square">Kwadrat (x²)</string>
    <string name="btn_sqrt">Pierwiastek kwadratowy (²√x)</string>
    <string name="btn_divide">Dzielenie</string>
    <string name="btn_multiply">Mnożenie</string>
    <string name="btn_subtract">Minus</string>
    <string name="btn_add">Plus</string>
    <string name="btn_negate">Zmień znak</string>
    <string name="btn_dot">Przecinek dziesiętny</string>
    <string name="btn_equals">Równa się</string>
</resources>
```

### 4 žingsnis: Patikrinimas
1. Perkompiliuokite programėlę.
2. Emuliatoriaus arba telefono nustatymuose (`Settings` $\rightarrow$ `System` $\rightarrow$ `Languages`) pasirinkite naujai pridėtą kalbą.
3. Atidarykite programėlę – visi tekstai, pranešimai bei garsiniai TalkBack aprašai bus automatiškai atvaizduojami nauja kalba.

---

## 🛠 Naudotos technologijos ir bibliotekos

| Technologija / Biblioteka | Versija | Paskirtis |
| :--- | :--- | :--- |
| **Kotlin** | `2.0.0` | Pagrindinė moderni programavimo kalba |
| **Jetpack Compose BOM** | `2024.06.00` | Deklaratyvus vartotojo sąsajos karkasas |
| **Material 3 (`material3`)** | Naujausia (per BOM) | Šiuolaikiniai UI komponentai, Scaffold, ModalBottomSheet |
| **Material Icons Extended** | Naujausia (per BOM) | Piktogramos (`History`, `Delete` ir kt.) |
| **AndroidX Core KTX** | `1.13.1` | Kotlin plėtiniai Android pagrindinėms funkcijoms |
| **Lifecycle & ViewModel** | `2.8.3` | Gyvavimo ciklo valdymas ir būsenos saugojimas ekrano vartymo metu |
| **Activity Compose** | `1.9.0` | Compose integracija į Android Activity |
| **Java BigDecimal** | Įtraukta į JDK | Tikslūs matematiniai skaičiavimai be apvalinimo iškraipymų |
| **Android SharedPreferences** | Android SDK | Istorijos ir atminties duomenų išsaugojimas įrenginyje |
| **JUnit 4 & Kotlin Test** | `4.13.2` / `2.0.0` | Automatiniai vienetų testai verslo logikai patikrinti |
| **Android Gradle Plugin** | `8.5.2` | Projekto kompiliavimas ir surinkimas |

---

## ♿ Pritaikymas neįgaliesiems (Accessibility)

Skaičiuotuvas pilnai pritaikytas naudotojams su regos negalia ar naudojantiems ekrano skaitytuvus (pvz., **Google TalkBack**):
- Visi funkciniai ir operatorių mygtukai turi semantinius `contentDescription` atributus.
- Skaitytuvas balsu perskaito mygtuko reikšmę vartotojo nustatyta kalba (pvz., paspaudus `÷` perskaitoma „Dalyba“ / „Divide“, paspaudus `²√x` – „Kvadratinė šaknis“).
- Mygtukai turi aiškius paspaudimo rėmelius bei kontrastingas spalvas, atitinkančias WCAG gaires.

---

## 📄 Licencija

Projektas skirtas mokymosi ir asmeninio naudojimo tikslams. Sukurta pagal „Windows 11 Calculator“ vartotojo patirties dizainą.
