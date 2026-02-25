# 🎵 Musicality

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=flat-square&logo=mysql&logoColor=white)

> *Una piattaforma desktop completa per lo streaming e la valutazione multidimensionale di musica e podcast.*

## 🌟 Punti chiave

- **Gestione Ruoli:** Accesso differenziato e dashboard dedicate per Ascoltatori, Autori e Amministratori.
- **Rating:** Valutazioni specifiche degli utenti su voce, testo e base musicale con classifiche generate dinamicamente.
- **Architettura Solida:** Sviluppata in Java con pattern MVC e pattern DAO per la persistenza dei dati.
- **Sicurezza Integrata:** Password degli account protette tramite hashing con algoritmo BCrypt.

## ℹ️ Panoramica

**Musicality** è un'applicazione desktop per lo streaming di contenuti multimediali. Nata con l'obiettivo di applicare solide logiche di ingegneria del software, la piattaforma gestisce un database relazionale complesso e offre agli utenti la possibilità di creare playlist, caricare brani o podcast e interagire tramite un sistema di recensioni dettagliato. L'intero sistema è supportato da query SQL native e ottimizzate per l'analisi dei dati (utilizzando funzioni di aggregazione, GROUP BY e HAVING).

## 🚀 Utilizzo

Trattandosi di un'applicazione con interfaccia grafica sviluppata in JavaFX, l'interazione avviene interamente tramite UI. Dopo aver avviato l'applicazione, potrai:

1. Registrarti scegliendo il ruolo di **Ascoltatore** o **Autore**.
2. Esplorare i contenuti tramite il sistema di Tag e sottoscrivere piani di abbonamento.
3. Consultare le classifiche in tempo reale (Top 50 Artisti, Top 50 Canzoni, ecc.).
4. Se sei un Amministratore, accedere al pannello di moderazione per gestire segnalazioni e approvare i testi.

<img src="https://github.com/user-attachments/assets/1db61559-fb35-4955-88aa-bd80ac19d585" width="400"/>

## ⬇️ Installazione e Utilizzo

### 📋 Prerequisiti
Prima di procedere, assicurati di avere configurato sul tuo sistema:
- **Java JDK** (versione 17 o superiore).
- **Maven** installato e configurato.
- **MySQL** in esecuzione (il database deve essere prima inizializzato importando lo script `schema.sql` fornito nel progetto).

### 🚀 Compilazione ed Esecuzione
Segui questi semplici passaggi per avviare l'applicazione in locale:

1. **Configura il Database:** Apri il file `Database.java`, inserisci le tue credenziali locali (username e password) per l'accesso a MySQL e salva il file.
2. **Apri il Terminale:** Posizionati nella directory principale del progetto, ovvero dove è contenuto il file `pom.xml`.
3. **Build del Progetto:** Per scaricare le dipendenze e compilare il progetto, esegui:
   ```bash
   mvn clean install
