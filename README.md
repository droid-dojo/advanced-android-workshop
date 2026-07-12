# 🧪 Lab 3 – Übung 3.2: Visuelle Regressionstests (Screenshot-Testing)

Dieser Branch (`lab-3-uebung-3.2`) enthält die **Musterlösung zu Übung 3.1** und ist gleichzeitig der Startpunkt für Übung 3.2.

> 📘 Die Theorie zu dieser Übung finden Sie im [HANDOUT.md](HANDOUT.md), **Modul 10.2–10.4**. Die Dependencies stehen im Setup-Abschnitt von Tag 3.

---

## ✅ Rückblick: Die Lösung zu Übung 3.1

Das ist neu bzw. anders gegenüber dem Branch `lab-3-uebung-3.1`:

* **`feature/characterlist/build.gradle.kts`**: Robolectric + `ui-test-junit4`/`ui-test-manifest` als `testImplementation`, dazu `isIncludeAndroidResources = true` in den `testOptions`.
* **`CharacterListContent`**: von `private` auf `internal` gelockert. Tests im selben Modul können jetzt feste Zustände hineingeben; nach außen bleibt die Modul-Grenze dicht.
* **`testTag("loading_indicator")`**: der Spinner ist als einziges Element über ein Tag auffindbar; alles andere finden die Tests über echte Semantik (`Text`, `contentDescription`).
* **`CharacterListScreenTest`**: acht Robolectric-Tests in `src/test/`: alle vier UI-Zustände, Banner positiv **und** negativ, Klick-Events für Charakter und Favorit, und ein Test, der beweist, dass die Favoriten-Semantik den Zustand spiegelt (was TalkBack vorliest, stimmt!).

Vergleichen Sie gerne mit Ihrer eigenen Lösung: `git diff lab-3-uebung-3.1 lab-3-uebung-3.2`

---

## 🔍 Die Ausgangslage

Die funktionalen Tests prüfen, *dass* Rick angezeigt wird, nicht *wie*. Ein verrutschtes Padding, ein unlesbarer Dark Mode, ein Banner, das plötzlich den halben Screen füllt: alles grün. Für Layout-Wahrheiten brauchen wir **Golden Images**.

## 🎯 Das Ziel

Eine Screenshot-Test-Suite für das Listen-Feature, die **pixelgenaue Regressionen** auf der JVM erkennt:

* Ein Golden Image pro visuell eigenständigem Zustand, jeweils in **Light und Dark Mode**.
* Die Goldens liegen **im Git** und werden per Gradle-Task verifiziert.
* Die Bilder sind **deterministisch** (keine echten Netzwerk-Bilder!).

**Sie haben die Wahl des Werkzeugs:**

* **Variante A – Roborazzi** (Modul 10.3): der etablierte Community-Standard, baut direkt auf Ihrem Robolectric-Setup aus Übung 3.1 auf.
* **Variante B – Compose Preview Screenshot Testing** (Modul 10.4): Googles offizielles Tool, noch Alpha: Ihre `@Preview`s werden zu Tests (Setup im Ausblick-Kasten).

*Die Musterlösung enthält **beide** Varianten nebeneinander; Sie können Ihre Wahl also in jedem Fall vergleichen.*

## 🛠 Die Aufgaben im Detail (Variante A)

### Schritt 1: Setup

1. `roborazzi`-Version, die beiden Libraries und das Plugin in die `libs.versions.toml` (Setup-Abschnitt Tag 3), Plugin mit `apply false` in die Root-`build.gradle.kts`.
2. Plugin + die zwei `testImplementation`-Dependencies in `feature/characterlist/build.gradle.kts`.

### Schritt 2: Determinismus sicherstellen

Schreiben Sie den kleinen `ScreenshotContainer`-Helfer (Modul 10.3) in die Test-Sourcen: `LocalInspectionMode` auf `true` zwingen + `PreviewContainer` + ein eigener `AsyncImagePreviewHandler`, der ein **echtes Bild aus den Test-Ressourcen** lädt (`src/test/res/drawable-nodpi/`): deterministisch wie eine Farbfläche, aber der Screenshot deckt echtes Bild-Decoding und die Skalierung im Layout mit ab.

### Schritt 3: Die Screenshot-Suite

Erstellen Sie `CharacterListScreenshotTest` (wieder nur `@RunWith(AndroidJUnit4::class)`; die `robolectric.properties` aus 3.1 gilt für das ganze Modul) und halten Sie mit `captureRoboImage()` fest:

- [ ] `Success` mit den Sample-Charakteren, Light **und** Dark (Tipp: `RickAndMortyTheme(darkTheme = true)` innerhalb des Containers erzwingen oder zwei Tests schreiben).
- [ ] `Success` mit sichtbarem Offline-Banner.
- [ ] `Loading` und `Error`.
- [ ] Ein einzelnes `CharacterItem` als Favorit (rotes Herz!).

### Schritt 4: Record, Verify, Kaputtmachen

1. Goldens aufnehmen: `./gradlew recordRoborazziDebug`, die PNGs committen!
2. Verifizieren: `./gradlew verifyRoborazziDebug` → grün.
3. **Der Aha-Moment:** Ändern Sie testweise ein Padding im `CharacterItem`, laufen Sie `verifyRoborazziDebug` erneut, und schauen Sie sich das erzeugte Diff-Bild unter `build/outputs/roborazzi/` an. Danach die Änderung zurücknehmen!

## ✅ Definition of Done

- [ ] Für jeden visuellen Zustand existiert ein eingechecktes Golden Image (inkl. Dark Mode).
- [ ] `./gradlew verifyRoborazziDebug` läuft grün; nach einer absichtlichen Layout-Änderung schlägt er fehl und erzeugt ein Diff-Bild.
- [ ] Auf keinem Screenshot ist ein "echtes" (netzwerkgeladenes) Bild zu sehen, nur die deterministischen Platzhalter.
- [ ] `./gradlew test` bleibt grün (Screenshot-Tests laufen nur über die Roborazzi-Tasks im Verify-Modus).

## 💡 Tipps

* `captureRoboImage()` auf `onRoot()` nimmt den ganzen Baum auf; einzelne Knoten gehen genauso (`onNodeWithText(...).captureRoboImage()`).
* Dark Mode: `RickAndMortyTheme(darkTheme = true)`, der Parameter existiert seit dem Einführungs-Workshop genau für so etwas.
* Benennen Sie die Bilder sprechend (`captureRoboImage("character_list_success_dark.png")`): die Dateinamen sind Ihre Test-Dokumentation.

---

**Fertig?** Die Musterlösung, und damit die Aufgabenstellung für **Übung 3.3 (Verschlüsselte POS-Konfiguration)**, finden Sie im Branch `lab-3-uebung-3.3`.
