# 🧪 Lab 3 – Übung 3.1: Funktionale UI-Tests auf der JVM

**Willkommen zu Tag 3!** Dieser Branch (`lab-3-uebung-3.1`) entspricht inhaltlich dem Endstand von Tag 2 (`lab-2-final`).

> 📘 Die Theorie zu dieser Übung finden Sie im [HANDOUT.md](HANDOUT.md), **Modul 9** (Der Semantics Tree) und **Modul 10.1** (Robolectric). Die Dependencies stehen im Setup-Abschnitt von Tag 3.

---

## 🔍 Die Ausgangslage

Repository und ViewModel sind getestet, aber niemand beweist bisher, dass die **UI** den richtigen Zustand zeichnet und Klicks die richtigen Events auslösen. Ein kaputter `when`-Zweig im `CharacterListContent` würde durch alle bisherigen Tests rutschen.

Der klassische Weg wären Instrumented Tests auf dem Emulator: langsam, flaky, teuer in der CI. Wir gehen den modernen Weg: **Compose-UI-Tests auf der lokalen JVM** mit Robolectric.

## 🎯 Das Ziel

Eine funktionale UI-Test-Suite für das Listen-Feature, die in `src/test/` lebt und mit `./gradlew test` in Sekunden läuft:

* Jeder UI-Zustand (`Loading`, `Success`, `Error`, Offline-Banner) wird über den **Semantics Tree** verifiziert.
* Interaktionen (Charakter-Klick, Favoriten-Klick) werden als **Events** geprüft, nicht als Navigation.
* Nebenbei wird die App **barrierefreier**: sinnvolle `contentDescription`s statt stummer Icons.

## 🛠 Die Aufgaben im Detail

### Schritt 1: Dependencies & Test-Setup

1. Ergänzen Sie `robolectric`, `ui-test-junit4`, `ui-test-manifest` und `androidx-test-ext-junit` (Setup-Abschnitt Tag 3 im Handout; die Screenshot-Teile brauchen Sie erst in Übung 3.2).
2. Aktivieren Sie in `feature/characterlist/build.gradle.kts` die `testOptions`-Einstellung `isIncludeAndroidResources = true`.
3. Legen Sie `src/test/resources/robolectric.properties` an, mit `sdk=36` und `graphicsMode=NATIVE` (warum, steht in Modul 10.1). Ihre Testklassen bleiben damit frei von Robolectric-Annotationen.

### Schritt 2: Die UI testbar (und barrierefrei) machen

1. **Sichtbarkeit:** `CharacterListContent` ist `private`. Lockern Sie es auf `internal`, damit die Tests im selben Modul es mit festen Zuständen aufrufen können (Modul 9.4). Die Modul-Grenze nach außen bleibt intakt!
2. **Semantik:** Geben Sie dem Lade-Indikator ein `testTag` (z.B. `"loading_indicator"`), denn eine sinnvolle `contentDescription` gibt es für einen Spinner nicht. Das Favoriten-Icon dagegen bringt bereits eine **zustandsabhängige** `contentDescription` mit (`"Add to favorites"` / `"Remove from favorites"`) – genau darüber werden Ihre Tests es finden. Werfen Sie einen Blick in `CharacterItem.kt` und überlegen Sie: Was liest TalkBack hier vor?

### Schritt 3: Die Test-Suite

Erstellen Sie `CharacterListScreenTest` in `src/test/`, annotiert nur mit `@RunWith(AndroidJUnit4::class)` (der Runner delegiert auf der JVM automatisch an Robolectric, Modul 10.1) und mit einer `createComposeRule()`. Testen Sie mindestens:

- [ ] `Loading` zeigt den Lade-Indikator (`onNodeWithTag`).
- [ ] `Success` zeigt die Namen der Charaktere (`onNodeWithText`).
- [ ] `Error` zeigt die Fehlermeldung.
- [ ] Das Offline-Banner erscheint **nur**, wenn `isRefreshFailed = true` (positiv **und** negativ testen, `assertDoesNotExist`!).
- [ ] Ein Klick auf einen Charakter liefert dessen `id` über `onCharacterClick`.
- [ ] Ein Klick auf das Favoriten-Icon liefert die `id` über `onFavoriteClick` (Finder: `onNodeWithContentDescription`).

## ✅ Definition of Done

- [ ] Alle UI-Tests liegen in `src/test/` (nicht `androidTest/`) und laufen ohne Emulator.
- [ ] `./gradlew test` läuft grün: ViewModel-, Repository- **und** UI-Tests zusammen.
- [ ] Die Tests finden das Favoriten-Icon über seine `contentDescription`, nicht über ein `testTag`.
- [ ] Kein Test wartet real (`Thread.sleep` verboten).

## 💡 Tipps

* `composeTestRule.onRoot().printToLog("TREE")` zeigt den Semantics Tree, wenn ein Finder ins Leere greift.
* Bei mehreren Treffern (`onAllNodesWith...`): mit `[0]` indizieren oder den Finder präzisieren.
* Die `LazyColumn` rendert nur Sichtbares. Für tiefer liegende Items `performScrollTo()` verwenden.

---

**Fertig?** Die Musterlösung, und damit die Aufgabenstellung für **Übung 3.2 (Screenshot-Tests)**, finden Sie im Branch `lab-3-uebung-3.2`.
