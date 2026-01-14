# FileIO Aufgabe - Durak Persistence

## Ziel
Implementierung von Persistierung (Speichern/Laden) des GameState mit XML und JSON, versteckt hinter einem gemeinsamen Interface, umschaltbar per Dependency Injection.

## Benötigte Libraries

In `build.sbt` hinzufügen:
```scala
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.3.0"
libraryDependencies += "org.playframework" %% "play-json" % "3.0.4"
```

## Architektur

### Paketstruktur
```
src/main/scala/de/htwg/DurakApp/util/
├── FileIOInterface.scala          // Trait mit save/load
└── impl/
    ├── FileIOXml.scala            // XML-Implementierung
    └── FileIOJson.scala           // JSON-Implementierung
```

### Interface Design
```scala
trait FileIOInterface:
  def save(gameState: GameState): Try[Unit]
  def load(): Try[GameState]
```

**Return-Type: `Try[Unit]`**
- `Success(())` bei Erfolg
- `Failure(exception)` bei Fehler
- Gut testbar und Scala-idiomatisch

## Implementierungsschritte

### 1. Dependencies hinzufügen ✅
- [x] `build.sbt` ergänzen
- [x] `sbt update` ausführen

### 2. FileIOInterface erstellen
- [x] Trait `FileIOInterface` in `util/FileIOInterface.scala`
- [x] Methoden `save` und `load` definieren

### 3. XML-Implementierung (FileIOXml)
- [x] `toXml` Methoden für Model-Klassen:
  - [x] `Card.toXml` → `<card><suit>...</suit><rank>...</rank><isTrump>...</isTrump></card>`
  - [x] `Player.toXml` → `<player><name>...</name><hand>...</hand><isDone>...</isDone></player>`
  - [x] `GameState.toXml` → komplette XML-Struktur
- [x] `fromXml` Methoden:
  - [x] `Card.fromXml(node: scala.xml.Node): Card`
  - [x] `Player.fromXml(node: scala.xml.Node): Player`
  - [x] `GameState.fromXml(node: scala.xml.Node): GameState`
- [x] `FileIOXml` Klasse implementieren:
  - [x] `save()` - mit `java.io.PrintWriter` und `scala.xml.XML.save()`
  - [x] `load()` - mit `scala.xml.XML.loadFile()`

### 4. JSON-Implementierung (FileIOJson)
- [x] Implicit Formats für Model-Klassen:
  - [x] `Suit` - implicit reads/writes
  - [x] `Rank` - implicit reads/writes
  - [x] `Card` - `Json.format[Card]` Macro
  - [x] `Player` - `Json.format[Player]` Macro
  - [x] `GamePhase` - implicit reads/writes für Enums
  - [x] `GameEvent` - implicit reads/writes
  - [x] `GameState` - `Json.format[GameState]` Macro
- [x] `FileIOJson` Klasse implementieren:
  - [x] `save()` - mit `java.io.PrintWriter` und `Json.toJson()`
  - [x] `load()` - mit `scala.io.Source` und `Json.parse()`

### 5. Dependency Injection Setup
- [x] In `DurakModule.scala`:
  ```scala
  bind[FileIOInterface].to[FileIOXml]
  // Wechsel zu JSON möglich mit:
  // bind[FileIOInterface].to[FileIOJson]
  ```

### 6. Tests schreiben
- [x] `FileIOXmlSpec.scala`:
  - [x] Test: GameState speichern und laden
  - [x] Test: Gespeicherte Datei ist gültiges XML
  - [x] Test: Fehlerbehandlung bei ungültiger Datei
- [x] `FileIOJsonSpec.scala`:
  - [x] Test: GameState speichern und laden
  - [x] Test: Gespeicherte Datei ist gültiges JSON
  - [x] Test: Fehlerbehandlung bei ungültiger Datei

**Status: ✅ Alle 454 Tests bestanden!**

### 7. Integration im Controller (optional)
- [ ] Save-Command erstellen
- [ ] Load-Command erstellen
- [ ] In TUI/GUI einbinden

## Technische Details

### XML-Beispiel
```xml
<gameState>
  <players>
    <player>
      <name>Alice</name>
      <hand>
        <card>
          <suit>Hearts</suit>
          <rank>Ace</rank>
          <isTrump>true</isTrump>
        </card>
      </hand>
      <isDone>false</isDone>
    </player>
  </players>
  <trumpCard>...</trumpCard>
  <deck>...</deck>
  ...
</gameState>
```

### JSON-Beispiel
```json
{
  "players": [
    {
      "name": "Alice",
      "hand": [
        {
          "suit": "Hearts",
          "rank": "Ace",
          "isTrump": true
        }
      ],
      "isDone": false
    }
  ],
  "trumpCard": {...},
  "deck": [...],
  ...
}
```

### File-Handling in Scala
```scala
// Schreiben
import java.io._
val pw = new PrintWriter(new File("gamestate.xml"))
pw.write(xmlString)
pw.close()

// Lesen
val source = scala.io.Source.fromFile("gamestate.json")
val content = source.getLines.mkString
source.close()
```

## Wichtige Hinweise

1. **Enums/Sealed Traits**: Für `Suit`, `Rank`, `GamePhase`, `GameEvent` müssen eigene Reads/Writes definiert werden
2. **Option-Types**: `Option[Int]`, `Option[Card]` werden automatisch unterstützt
3. **Maps**: `Map[Card, Option[Card]]` benötigt spezielle Behandlung (Keys als String serialisieren)
4. **Try-Handling**: Alle IO-Operationen in `Try { ... }` wrappen
5. **File-Paths**: Hardcoded oder als Parameter? → Besser als Konstruktor-Parameter für Testbarkeit

## Entscheidungen

- [x] Return-Type: `Try[Unit]` für bessere Testbarkeit
- [x] Package: `util/` statt `model/`
- [x] Default-Dateinamen: `gamestate.xml` / `gamestate.json`
- [x] File-Path als Parameter im Konstruktor mit @Named Annotation

## ✅ Implementierung abgeschlossen

### Erstelle Dateien:
- `src/main/scala/de/htwg/DurakApp/util/FileIOInterface.scala`
- `src/main/scala/de/htwg/DurakApp/util/impl/FileIOXml.scala`
- `src/main/scala/de/htwg/DurakApp/util/impl/FileIOJson.scala`
- `src/test/scala/de/htwg/DurakApp/util/impl/FileIOXmlSpec.scala`
- `src/test/scala/de/htwg/DurakApp/util/impl/FileIOJsonSpec.scala`

### Verwendung:

```scala
// Im DurakModule zwischen XML und JSON wechseln:
bind[FileIOInterface].to[FileIOXml]  // Aktuell aktiv
// bind[FileIOInterface].to[FileIOJson]  // Für JSON

// Verwendung im Code:
@Inject() fileIO: FileIOInterface

fileIO.save(gameState) match
  case Success(_) => println("Gespeichert!")
  case Failure(e) => println(s"Fehler: ${e.getMessage}")

fileIO.load() match
  case Success(state) => // GameState verwenden
  case Failure(e) => println(s"Laden fehlgeschlagen: ${e.getMessage}")
```
