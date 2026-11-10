# Raspodijeljeni sustavi, 1. laboratorijska vježba

**Prikupljanje i obrada senzorskih podataka**, FER, ak. god. 2026./2027.

Dva odvojena projekta:

- **`server/`** je Spring Boot REST poslužitelj na portu 8090. Pamti senzore i njihova očitanja, a na zahtjev računa najbližeg susjeda Haversineovom formulom.
- **`sensor/`** je senzor koji se pokreće u više primjeraka. Čita očitanja iz `readings.csv` i od najbližeg susjeda preko gRPC-a traži njegovo očitanje. Zatim računa prosjek (kalibracija) i sprema ga na poslužitelj.

## Što treba

- **JDK 25.** Na Windowsu: `winget install Microsoft.OpenJDK.25`.
- **Gradle ne treba.** Oba projekta imaju `gradlew`, koji preuzme Gradle 9.1.0.

## Pokretanje (Windows, PowerShell)

Svaki od ova četiri pokreni u **svom** terminalu:

```powershell
cd server
.\gradlew bootRun
```

```powershell
cd sensor
.\gradlew runSensor --args="8080"
```

```powershell
cd sensor
.\gradlew runSensor --args="8081"
```

```powershell
cd sensor
.\gradlew runSensor --args="8082"
```

Argument je gRPC port senzora.

Detaljno postavljanje, provjere i poznate zamke opisani su u [CLAUDE.md](CLAUDE.md). Taj dokument je pisan kao upute za Claude Code, ali ga može pratiti i čovjek.

## Za Claude Code na drugom računalu

Zalijepi ovo u Claude Code:

> Na ovom računalu treba pokrenuti moj labos iz Raspodijeljenih sustava. Kod je na https://github.com/RomanoRandic/rassus-labos-1. Kloniraj ga u `C:\rassus\rassus-labos-1`. Ako nema gita, preuzmi ZIP s https://github.com/RomanoRandic/rassus-labos-1/archive/refs/heads/main.zip. Zatim pročitaj `CLAUDE.md` u korijenu repozitorija i napravi sve redom: instaliraj samo ono što fali, izgradi oba projekta i prođi provjere A do F. Kod ne mijenjaj. Na kraju mi javi što je prošlo.
