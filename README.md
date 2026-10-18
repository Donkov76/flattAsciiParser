# flattAsciiParser

## Verwendung
- Kompiliert mit Java 25
- Es ist eine SpringBoot Application daher kann die Anwendung mit `./gradlew bootRun --args <Filename>` gestartet werden.

## Warum ich das so gelöst habe
- Die Input Datei ist ja recht klein. Daher war es in Ordnung, den kompletten Inhalt in den Speicher zu laden. Bei größeren Dateien würde ich eine Zeile für Zeile verarbeiten.
- Spring Boot nimmt einem, auch bei einem so kleinen Projekt, etwas Arbeit ab. (Logging, Konfigurierbarkeit, Starten, Fat Jar / bootJar)
- Eine Bearbeitung der Daten war ja nicht gefordert, daher habe ich keine Konvertierung der Felder nach  Java Objekten durchgeführt sondern alles bei String belassen.
- Da es sich um sehr viele Felder handelt, habe ich nur in eine abstrakte Struktur geparsed. (model.row.*).
  - Reicht aus um es gut debuggen zu können. 
  - Dadurch konnte dann auch die Umwandlung in das Ziel JSON ohne viel Mapping Code erfolgen.
  - Überführung in ein konkreteres Modell hätte für viel mehr Code beim Mapping ins JSON Zielformat geführt.
- Zum testen habe ich eine zusätzliche Datei sample-pos-alc.txt angelegt. Da die erlaubte Variante POS mit LC in den Beispieldaten nicht vorkam.