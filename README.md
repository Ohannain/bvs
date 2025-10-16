# BVS – Bibliotheksverwaltungssystem für die Kommandozeile

A Library Management System (BVS) for the CLI.  

## Technologie und Architektur

- Java (Standardbibliotheken)
- Gradle als Build-Tool
- JSON-Dateien für Persistenz (GSON oder Jackson)
- JUnit für Tests
- Textbasierte Konsolenanwendung (kein GUI)

## Features

### Feature 1: Nutzerverwaltung
- Nutzer anlegen, suchen, anzeigen, bearbeiten, löschen, sperren und Ausleihberechtigungen prüfen.

### Feature 2: Medienverwaltung
- Medien erfassen, ändern, löschen, bearbeiten
- Zustand verwalten (aktiv, inaktiv, beschädigt, verloren, ...)
- Suchfunktionen/Filteroptionen

### Feature 3: Ausleih- und Rückgabe-System
- Medien ausleihen und verlängern
- Rückgaben erfassen und Fristen überwachen
- Verfügbarkeitsprüfung – automatisches Prüfen, ob ein Medium ausgeliehen, reserviert oder defekt ist
- Dynamische Leihdauer – Leihzeit hängt vom Medientyp oder Nutzerstatus ab (z. B. DVD = 7 Tage, Buch = 30 Tage)

### Feature 4: Reservierungen
- Reservierungen erstellen und verwalten

### Feature 5: Mahnungen / Strafen
- Fristüberschreitungen erkennen
- Mahnungen erstellen/verwalten (automatisch anhand des Zustandes)
- Gebühren berechnen
- Nutzer mit offenen Mahnungen oder zu vielen Medien werden automatisch gesperrt.

### Feature 6: Benutzeroberfläche (CLI)
- Menüs anzeigen, Eingaben verarbeiten und Systemfunktionen ausführen.

### Feature 7: Statistiken & Reports
- Mahnstatistik – Analyse, wie oft Mahnungen pro Monat oder pro Nutzergruppe auftreten
- Auslastungsbericht (optional) – Berichte zur Bibliotheksauslastung generieren
- Beliebtheitsanalyse – Ranking der meist ausgeliehenen Medien oder Genres
- Trendermittlung – welche Genres, Medien oder Autoren sind aktuell „im Trend“
- Jahresabschluss / Berichtsmodul – Automatische Erstellung eines Jahresberichts mit Kennzahlen zu Ausleihen, Reservierungen, Mahnungen und Medienbestand

### Feature 8 (optional): Verlängerungs-Management
- Laufende Ausleihen prüfen und automatisch verlängern, sofern keine Reservierungen oder Limits bestehen

### Feature 9 (optional): Empfehlungssystem
- Medienvorschläge für Nutzer basierend auf deren bisherigen Ausleihen oder bevorzugten Genres generieren

