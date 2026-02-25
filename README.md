# UniBo-Musicality
Musicality è una piattaforma desktop per lo streaming di musica e podcast. L'applicazione permette agli utenti di esplorare contenuti multimediali, creare playlist personalizzate e interagire con la community attraverso un sistema di valutazione.

1) Ruoli Utente
  - Il sistema gestisce l'accesso tramite un sistema di ruoli (Role-Based Access Control):
      Ascoltatore: Può esplorare il catalogo, sottoscrivere piani di abbonamento, creare playlist e valutare i contenuti.
      Autore: Oltre ai privilegi dell'ascoltatore, possiede una dashboard dedicata per la pubblicazione e la gestione di propri contenuti multimediali (brani e podcast), l'aggiunta di tag e il caricamento dei testi.
      Amministratore: Responsabile della moderazione della piattaforma, gestisce le segnalazioni, approva i testi e vigila sul rispetto delle linee guida.

2) Funzionalità Principali
  - Autenticazione e Gestione Account
      Registrazione e Login: Creazione dell'account (Nome, Cognome, Email, Username, Password) con selezione del ruolo (Ascoltatore o Autore).
      Gestione Abbonamenti: Possibilità di scegliere e sottoscrivere diversi piani di abbonamento.

  - Gestione Contenuti e Interazione
      Upload Contenuti: Aggiunta, modifica e rimozione di brani musicali e podcast (riservato agli Autori).
      Sistema di Tagging: Creazione e assegnazione di tag per ottimizzare l'indicizzazione e la ricerca dei contenuti.
      Gestione Playlist: Creazione di raccolte personalizzate di brani e podcast, complete di nome e descrizione, con la possibilità di renderle pubbliche o mantenerle private.
      Testi Sincronizzati: Caricamento dei testi delle canzoni, resi visibili agli utenti durante l'ascolto (previa approvazione dell'Admin).

  - Sistema di Rating
      Valutazione Dettagliata: Gli utenti possono recensire metriche specifiche dei brani (es. qualità della voce, profondità del testo, base musicale).

3) Analisi Dati e Classifiche
  - La piattaforma elabora i dati generati dagli utenti per offrire insight e classifiche aggiornate:
      Top Charts Generali: Top 50 degli artisti più apprezzati.
      Classifiche Contenuti: Top 50 dei brani e Top 50 dei podcast con i rating complessivi più alti.
      Classifiche Specifiche: Graduatorie mirate basate sui singoli aspetti valutati dagli utenti (es. "Miglior Testo", "Miglior Base Musicale").
      Trend di Piattaforma: Statistiche globali, come l'individuazione del genere musicale più ascoltato.

4) Pannello di Amministrazione (Admin)
  - Strumenti dedicati al mantenimento di un ambiente sicuro:
      Moderazione Account: Possibilità di bloccare preventivamente gli account degli autori che violano le policy e di sbloccarli in seguito a revisione.
      Moderazione Contenuti: Rimozione di tag inappropriati e approvazione manuale dei testi delle canzoni prima della loro pubblicazione definitiva.

5) Tecnologie e Architettura
  - Di seguito lo stack tecnologico e le scelte architetturali impiegate per lo sviluppo della piattaforma:
      Linguaggio Principale: Java.
      Interfaccia Grafica: Sviluppo UI tramite JavaFX.
      Database: MySQL per la progettazione di un database relazionale complesso, ottimizzato per gestire gerarchie di utenti e relazioni molti-a-molti.
      Interazione Database: Connessione tramite JDBC con implementazione di query SQL native avanzate per l'analisi dei dati e la generazione delle classifiche (utilizzo di funzioni di aggregazione, GROUP BY, HAVING).
      Design Pattern: Strutturazione del codice secondo il pattern architetturale MVC (Model-View-Controller) e utilizzo del pattern DAO (Data Access Object) per isolare e gestire la persistenza dei dati.
      Sicurezza: Gestione sicura degli account con hashing delle password tramite l'algoritmo BCrypt.
      Build System: Gestione delle dipendenze e del ciclo di build tramite Maven.
