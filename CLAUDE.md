# Upute za Claude: postavljanje i provjera 1. labosa iz Raspodijeljenih sustava

Ovaj repozitorij je predano rješenje 1. laboratorijske vježbe iz Raspodijeljenih sustava (FER, 2026./2027.):
**Prikupljanje i obrada senzorskih podataka**. Tvoj posao na ovom računalu:

1. instalirati samo ono što fali,
2. izgraditi oba projekta,
3. proći provjere A do F niže i javiti korisniku što je prošlo,
4. po želji otvoriti prozore za demonstraciju.

**Pravila:**
- Kod ne mijenjaj. To je točno ono što je predano, a korisnik ga mora obraniti na usmenom ispitu.
- Ako nešto ne radi, popravi okruženje (Java, putanja, portovi), a ne kod. Ako misliš da je greška u kodu, reci korisniku i ništa ne diraj.
- Korisniku odgovaraj na hrvatskom, neformalno („ti”).
- Instalacija Jave traži administratorsku dozvolu. Reci korisniku da potvrdi prozor koji se otvori.

## Što je u repozitoriju

| Mapa | Što je | Pokretanje |
|---|---|---|
| `server/` | Spring Boot 4.0.8 REST poslužitelj, port 8090 (`src/main/resources/application.yml`), baza H2 u memoriji | `.\gradlew bootRun` |
| `sensor/` | Senzor: REST klijent (Retrofit) prema poslužitelju, gRPC poslužitelj i gRPC klijent prema susjedu. Čita `readings.csv`. | `.\gradlew runSensor --args="8080"` (argument je gRPC port senzora) |

Oba projekta imaju svoj Gradle wrapper (Gradle 9.1.0) i traže **Javu 25** (`languageVersion = 25` u `build.gradle`).

## 1. Provjeri što već postoji

Očekuje se Windows i PowerShell. Za macOS i Linux vidi zadnje poglavlje.

```powershell
java -version
Get-ChildItem "C:\Program Files\Microsoft", "C:\Program Files\Eclipse Adoptium", "C:\Program Files\Java" -Directory -Filter "jdk-25*" -ErrorAction SilentlyContinue | ForEach-Object FullName
git --version
curl.exe --version
Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue | Where-Object LocalPort -in 8080,8081,8082,8090 | Select-Object LocalPort, OwningProcess
```

- Ako JDK 25 postoji, preskoči instalaciju.
- Ako je neki od portova 8080, 8081, 8082 ili 8090 zauzet, pitaj korisnika što je to prije nego išta gasiš.

## 2. Instaliraj samo što fali

| Alat | Treba li | Kako |
|---|---|---|
| JDK 25 | Da | `winget install Microsoft.OpenJDK.25`, pa otvori novi terminal |
| Gradle | **Ne** | `gradlew` sam preuzme Gradle 9.1.0 pri prvom pokretanju |
| curl | Ne | `curl.exe` je dio Windowsa 10 i 11 |
| Git | Samo za kloniranje | Ako ga nema, preuzmi ZIP (korak 3) |
| IDE | Ne | Sve ide iz terminala |

Prvi build preuzima Gradle i biblioteke (nekoliko stotina MB), pa treba internet.

**Pazi:** `winget install Microsoft.OpenJDK.25` je na jednom računalu prebacio sistemski `JAVA_HOME` na Javu 25. Labosu to ne smeta. Ako korisnik ima druge projekte sa starijim Gradleom (< 9.1), oni na Javi 25 padaju s `Unsupported class file major version 69`. Tada mu reci da vrati `JAVA_HOME` u postavkama sustava. Labos radi i tako, zbog koraka 4.

## 3. Preuzmi kod

Putanja mora biti **kratka, bez razmaka i bez č, ć, đ**. Inače `gradlew` pada s `Could not find or load main class org.gradle.wrapper.GradleWrapperMain`, jer Java na Windowsu s kodnom stranicom 1252 ne može primiti ta slova.

```powershell
git clone https://github.com/RomanoRandic/rassus-labos-1.git C:\rassus\rassus-labos-1
```

Ako nema gita:

```powershell
New-Item -ItemType Directory -Force C:\rassus | Out-Null
curl.exe -L -o C:\rassus\lab.zip https://github.com/RomanoRandic/rassus-labos-1/archive/refs/heads/main.zip
Expand-Archive C:\rassus\lab.zip C:\rassus
Rename-Item C:\rassus\rassus-labos-1-main rassus-labos-1
```

## 4. Provjeri da Gradle vidi Javu 25

```powershell
cd C:\rassus\rassus-labos-1\sensor
.\gradlew -q javaToolchains
```

Na popisu mora biti JDK s `Language Version: 25`. Ako ga nema, dodaj ovo i ponovi provjeru. Gradle 9.1.0 sam ne pronalazi Microsoftov JDK, osim ako mu je to `JAVA_HOME`.

```powershell
$jdk25 = (Get-ChildItem "C:\Program Files\Microsoft" -Directory -Filter "jdk-25*" | Select-Object -First 1).FullName -replace '\\', '/'
Add-Content "$HOME\.gradle\gradle.properties" "org.gradle.java.installations.paths=$jdk25"
```

Nakon toga mora pisati `Detected by: Gradle property 'org.gradle.java.installations.paths'`.

## 5. Izgradi oba projekta

```powershell
cd C:\rassus\rassus-labos-1\server
.\gradlew build
cd C:\rassus\rassus-labos-1\sensor
.\gradlew build
```

U oba mora pisati `BUILD SUCCESSFUL`. Ovo nisu greške:
- `Deprecated Gradle features were used`
- `WARNING: A terminally deprecated method in sun.misc.Unsafe has been called`
- `WARNING: A restricted method in java.lang.System has been called`

## 6. Provjere

`bootRun` i `runSensor` rade dok ih se ne ugasi. Pokreni ih kao pozadinske naredbe (u Claude Codeu: `run_in_background`) i čitaj njihov izlaz. Senzor gasiš tako da ugasiš proces na njegovom portu:

```powershell
Stop-Process -Id (Get-NetTCPConnection -LocalPort 8081 -State Listen).OwningProcess -Force
```

H2 baza je u memoriji, a odjave senzora nema. Zato prije F ponovo pokreni poslužitelj.

### A. Poslužitelj je ugašen, senzor mora stati

```powershell
cd C:\rassus\rassus-labos-1\sensor
.\gradlew runSensor --args="8080"
```

Očekivano:
```
INFO: gRPC server started on 8080
SEVERE: Server is not reachable, stopping: Failed to connect to localhost/[0:0:0:0:0:0:0:1]:8090
BUILD FAILED
```
`BUILD FAILED` je ovdje ispravno: uputa traži prekid rada, a Gradle tako prijavljuje `System.exit(1)`.

### B. REST poslužitelj curlom

U pozadini pokreni `.\gradlew bootRun` u `server` i pričekaj `Tomcat started on port 8090`. Zatim u PowerShellu, redom (piši `curl.exe`, ne `curl`):

```powershell
curl.exe -i http://localhost:8090/sensors
curl.exe -i -X POST http://localhost:8090/sensors -H "Content-Type: application/json" -d '{\"latitude\":45.75,\"longitude\":15.87,\"ip\":\"127.0.0.1\",\"port\":8080}'
curl.exe -i http://localhost:8090/sensors/1/neighbour
curl.exe -i -X POST http://localhost:8090/sensors -H "Content-Type: application/json" -d '{\"latitude\":45.76,\"longitude\":15.88,\"ip\":\"127.0.0.1\",\"port\":8081}'
curl.exe -i http://localhost:8090/sensors/1/neighbour
curl.exe -i -X POST http://localhost:8090/sensors/1/readings -H "Content-Type: application/json" -d '{\"temperature\":32.0,\"pressure\":1109.0,\"humidity\":40.0,\"co\":547.0,\"so2\":18.0}'
curl.exe -i http://localhost:8090/sensors/1/readings
curl.exe -i -X POST http://localhost:8090/sensors/999/readings -H "Content-Type: application/json" -d '{\"temperature\":32.0}'
curl.exe -i http://localhost:8090/sensors/999/readings
curl.exe -i http://localhost:8090/sensors
```

| # | Očekivano |
|---|---|
| 1 | `200`, `[]` |
| 2 | `201`, `Location: http://localhost:8090/sensors/1` |
| 3 | `204` (jedini senzor nema susjeda) |
| 4 | `201`, `Location: http://localhost:8090/sensors/2` |
| 5 | `200`, `{"latitude":45.76,"longitude":15.88,"ip":"127.0.0.1","port":8081}` |
| 6 | `201`, `Location: http://localhost:8090/readings/1` |
| 7 | `200`, `[{"co":547.0,"humidity":40.0,"id":1,"pressure":1109.0,"so2":18.0,"temperature":32.0}]` |
| 8 | `204` (senzor 999 ne postoji) |
| 9 | `204` |
| 10 | `200`, oba senzora s `id` |

Polja dolaze abecednim redom, i to je u redu.

**Nakon B ugasi poslužitelj i pokreni ga ponovo.** Lažni senzori iz curla inače bi postali susjedi pravima.

### C. Jedan senzor (poslužitelj radi, baza je prazna)

U pozadini: `.\gradlew runSensor --args="8080"` u `sensor`. Svake 2 sekunde:
```
INFO: Registered with id 1 at 45.76..., 15.88...
INFO: Own reading: Reading[temperature=34.0, pressure=1001.0, humidity=35.0, co=174.0, no2=478.0, so2=null]
INFO: No neighbour, readings will not be calibrated
INFO: Saved Reading[...] (HTTP 201)
```

### D. Još dva senzora, kalibracija

U pozadini još `--args="8081"` i `--args="8082"`. U njihovom izlazu traži:
```
INFO: My neighbour is SensorInfo[latitude=..., longitude=..., ip=127.0.0.1, port=8080]
INFO: Neighbour reading: temperature: 30.0 pressure: 505.0 humidity: 50.0 co: 178.0
INFO: Saved Reading[temperature=32.0, ...] (HTTP 201)
INFO: Sensor 2 asked for my reading
```

Spremljena vrijednost je prosjek vlastite i susjedove. Ako je jedna od njih 0 ili je nema, uzima se druga, a ako nema obje, polje je `null` i izostavljeno iz JSON-a. Provjeri jedan redak ručno.

### E. Susjed nestane

Iz retka `My neighbour is ... port=XXXX` senzora 8082 ugasi senzor na portu XXXX. Senzor 8082 mora ispisati sljedeće i nastaviti spremati:
```
WARNING: gRPC connection to neighbour failed: UNAVAILABLE: io exception
INFO: Saved Reading[...] (HTTP 201)
```
Novog susjeda ne traži, to uputa izričito ne traži.

### F. Podaci na poslužitelju

```powershell
curl.exe http://localhost:8090/sensors
curl.exe http://localhost:8090/sensors/1/readings
```
Moraju biti svi registrirani senzori i njihova očitanja (JSON 4 i JSON 5).

### Na kraju

1. Ugasi sve: procese na portovima 8080, 8081, 8082 i 8090, pa `.\gradlew --stop`.
2. Javi korisniku tablicu: A do F, prošlo ili nije, s ključnim retkom iz izlaza.

## 7. Prozori za demonstraciju (ako korisnik to traži)

Korisnik na obrani pokazuje rad u vidljivim prozorima. Ovo otvara četiri PowerShell prozora koja ostaju otvorena:

```powershell
$R = "C:\rassus\rassus-labos-1"
Start-Process powershell -ArgumentList '-NoExit', '-Command', "Set-Location '$R\server'; .\gradlew bootRun"
Start-Sleep -Seconds 25
foreach ($p in 8080, 8081, 8082) {
  Start-Process powershell -ArgumentList '-NoExit', '-Command', "Set-Location '$R\sensor'; .\gradlew runSensor --args=$p"
  Start-Sleep -Seconds 8
}
```

Korisnik gasi pojedini prozor s `Ctrl+C`.

## Zamke

| Što se vidi | Uzrok | Rješenje |
|---|---|---|
| `Could not find or load main class org.gradle.wrapper.GradleWrapperMain` | č, ć ili đ u putanji | Premjesti repozitorij u `C:\rassus\...` |
| `Cannot find a Java installation ... languageVersion=25` | Gradle ne vidi JDK 25 | Korak 4 |
| `Unsupported class file major version 69` u **drugom** projektu | `JAVA_HOME` je sad Java 25 | Korisnik vraća `JAVA_HOME` za taj projekt |
| `protoc-gen-grpc: The system cannot find the path specified` | Predugačka putanja (Windows ograničenje od 260 znakova), npr. `GRADLE_USER_HOME` u dubokoj mapi | Kraći `GRADLE_USER_HOME` i kraća putanja projekta |
| `Port 8090 was already in use` ili `Failed to bind` | Stari proces još radi | Ugasi ga ili pitaj korisnika što je to |
| `Invoke-WebRequest` greška kod `curl` | U PowerShellu je `curl` alias | `curl.exe` |
| `400` kod POST-a curlom | PowerShell je pojeo navodnike | Unutar JSON-a `\"`, kao gore |
| Senzor dobije susjeda koji se ne javlja | Ugašeni senzor ostao je u H2 bazi | Ponovo pokreni poslužitelj, pa senzore |

## Gdje je što u kodu (da možeš pomoći s objašnjavanjem)

| Zahtjev iz upute | Datoteka |
|---|---|
| 3.1 Nasumična lokacija, registracija, prekid rada bez registracije | `sensor/.../SensorApp.java` (`main`, `register`) |
| 3.2 Redak `(brojAktivnihSekundi % 100) + 1` iz `readings.csv` | `sensor/.../ReadingGenerator.java` |
| 3.3 Traženje susjeda, gRPC zahtjev, rad bez susjeda | `SensorApp.java` (petlja, `findNeighbour`), `GrpcClient.java` |
| 3.3 Senzor kao gRPC poslužitelj za više susjeda | `GrpcServer.java`, `ReadingExchangeService.java` |
| gRPC poruke | `sensor/src/main/proto/reading.proto` (generirani kod je u paketu `...sensor.grpc`) |
| 3.4 Kalibracija | `Calibrator.java` |
| 3.5 i 4.1 do 4.3 REST pozivi senzora | `SensorApi.java` (Retrofit) |
| 4.1, 4.2, 4.4 Registracija, najbliži susjed (Haversine, R = 6371, radijani), popis senzora | `server/.../controllers/SensorsController.java` |
| 4.3, 4.5 Spremanje i popis očitanja | `server/.../controllers/ReadingController.java` |
| Entiteti i repozitoriji (JPA, H2, veza 1 : N) | `server/.../beans/`, `server/.../services/` |

## macOS i Linux

Isti koraci, uz ove razlike:
- Bilo koji JDK 25 (npr. preko SDKMAN-a ili upravitelja paketa).
- `./gradlew` umjesto `.\gradlew`. Ako javi `Permission denied`, pokreni `chmod +x gradlew`.
- `curl` umjesto `curl.exe`, a JSON u jednostrukim navodnicima bez `\`.
- Proces na portu gasiš s `kill $(lsof -ti :8081)`.
