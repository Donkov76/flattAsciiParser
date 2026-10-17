# flattAsciiParser

## Verwendung
- Kompiliert mit Java 25
- Es ist eine SpringBoot Application daher kann ein JAR mit `./gradlew bootJar` erzeugt werden.
- Ausführung dann `java -jar flatAsciiParser-0.0.1-SNAPSHOT.jar 4311560000000_IV_4260043070002_20200527133435`

## Warum ich das so gelöst habe
- Spring Boot nimmt einem auch bei einem so kleinen Projekt etwas Arbeit ab. (Logging, Konfigurierbarkeit)
- Eine Bearbeitung der Daten war ja nicht gefordert, daher habe ich keine Konvertierung der Felder nach  Java Objekten durchgeführt sondern alles bei String belassen.
- Da es sich um sehr viele Felder handelt, habe cih nur in eine recht abstrakte Struktur geparsed. (model.row.*). Dadurch konnte dann auch die Umwandlung in das Ziel JSON ohne viel Mapping Code erfolgen.