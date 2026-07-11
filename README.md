# 🧪 Lab 2 – Übung 2.2: ViewModel-Unit-Tests mit virtueller Zeit

Dieser Branch (`lab-2-uebung-2.2`) enthält die **Musterlösung zu Übung 2.1** und ist gleichzeitig der Startpunkt für Übung 2.2.

> 📘 Die Theorie zu dieser Übung finden Sie im [HANDOUT.md](HANDOUT.md), **Modul 8** (Testbarkeit & moderne Testwerkzeuge). Die neue Dependency (**Turbine**) steht im Setup-Abschnitt von Tag 2.

---

## ✅ Rückblick: Die Lösung zu Übung 2.1

Das ist neu bzw. anders gegenüber dem Branch `lab-2-uebung-2.1`:

* **`:core:analytics:api` / `:core:analytics:impl`**: das neue Modul-Paar nach dem api/impl-Muster aus Modul 3.3. Die Schnittstellen `AnalyticsTracker` und `AppLogger` plus `TrackScreen` im **api**-Modul; Logcat-Implementierungen und das `AnalyticsModule` (unser erster Einsatz von **`@Binds`**) im **impl**-Modul. Konsumenten kennen nur `:api`; allein `:app` hebt `:impl` per `implementation` in den Hilt-Graphen.
* **`TrackScreen.kt`**: das `LocalAnalyticsTracker`-CompositionLocal (No-Op-Default für Previews) und der `TrackScreen`-Nebeneffekt via `LaunchedEffect`.
* **`MainActivity`**: injiziert den echten Tracker (`@Inject lateinit var`) und stellt ihn per `CompositionLocalProvider` bereit.
* **Die Screens**: eine Zeile `TrackScreen(...)` pro Screen; der Listen-Screen trackt zusätzlich `toggle_favorite` **am Interaktionspunkt**. Die ViewModels: unverändert.
* **`CharacterRepository`**: bekommt den `AppLogger` injiziert; erfolgreiche Refreshes werden per `debug` protokolliert, fehlgeschlagene per **Log-and-Rethrow** (`CancellationException` ausgenommen!).
* **`PlaceDto`**: der letzte direkte `android.util.Log`-Aufruf ist Geschichte, `toIntOrNull()` statt `try/catch`.
* **Tests**: der neue `FakeAppLogger` sammelt Log-Aufrufe in Listen; der Test `failed refresh is logged as error` beweist, dass der Fehlerpfad protokolliert wird.

Vergleichen Sie gerne mit Ihrer eigenen Lösung: `git diff lab-2-uebung-2.1 lab-2-uebung-2.2`

---

## 🔍 Die Ausgangslage

Unsere Repository-Schicht ist getestet, aber das Herzstück der UI-Logik, das `CharacterListViewModel`, ist es nicht. Dabei steckt dort inzwischen einiges drin: `combine` aus zwei Quellen, `stateIn` mit `WhileSubscribed`, die Loading/Success/Error-Weiche, das `isRefreshFailed`-Flag.

Und es gibt ein Hindernis: Der Konstruktor verlangt die **konkrete Klasse** `CharacterRepository`, mitsamt Retrofit und Room. So lässt sich auf der JVM kein Test schreiben.

## 🎯 Das Ziel

Machen Sie das ViewModel testbar und **verifizieren Sie seine reaktiven Zustandsübergänge** mit virtueller Zeitsteuerung:

* `CharacterRepository` wird ein **Interface**; die bisherige Implementierung heißt `OfflineFirstCharacterRepository` und wird per `@Binds` verdrahtet.
* Das `CharacterListViewModel` wird auf der **lokalen JVM** getestet: ohne Emulator, ohne Netzwerk, ohne echte Zeit.
* Die Tests decken alle Zustandsübergänge ab: Loading → Success, Refresh-Fehler mit/ohne Cache, Favoriten-Toggle.

## 🛠 Die Aufgaben im Detail

### Schritt 1: Die Nahtstelle auftrennen (Interface-Extraktion)

1. Extrahieren Sie in `:core:data` das Interface `CharacterRepository` mit den fünf bekannten Funktionen (Signaturen: Handout 8.1).
2. Benennen Sie die Implementierung in `OfflineFirstCharacterRepository` um (`: CharacterRepository`, `override` nicht vergessen).
3. Verdrahten Sie beides in einem neuen `DataModule` per `@Binds`, wie in Übung 2.1 gelernt.
4. **Das Modul-Upgrade (Modul 3.3):** Jetzt, wo der Vertrag existiert, trennen Sie `:core:data` in `:core:data:api` (nur das Interface, mit `api(projects.core.model)`) und `:core:data:impl` (alles andere, inklusive der Tests). Beide Build-Dateien starten dank Convention Plugin mit einer Zeile `alias(libs.plugins.rickandmorty.android.library)`. Die Features hängen danach nur noch an `:core:data:api`; **nur `:app`** bekommt `implementation(projects.core.data.impl)`.
5. **Kontrollfrage:** Warum müssen die ViewModels für diesen Umbau nicht angefasst werden? (Handout 8.1)
6. Die Repository-Tests (jetzt in `:core:data:impl`) instanziieren `OfflineFirstCharacterRepository`.

### Schritt 2: Test-Infrastruktur im Feature-Modul

1. Ergänzen Sie `turbine` und `kotlin-test` in der `libs.versions.toml` und die vier Test-Dependencies in `feature/characterlist/build.gradle.kts` (Setup-Abschnitt Tag 2 im Handout).
2. Lösen Sie das Main-Dispatcher-Problem zuerst **von Hand**: `Dispatchers.setMain(StandardTestDispatcher())` in `@Before`, `Dispatchers.resetMain()` in `@After` (Handout 8.3), und lassen Sie einen ersten leeren Test damit grün werden. Refactoren Sie das Setup **danach** in die wiederverwendbare `MainDispatcherRule` (der Bonus aus 8.3): gleicher Effekt, eine Zeile pro Testklasse.
3. Schreiben Sie einen `FakeCharacterRepository` (Vorlage: Handout 8.2): eine `MutableStateFlow`-basierte Implementierung mit steuerbarem Fehlverhalten (`shouldFailRefresh`) und steuerbarer "Netzwerk-Antwort".

### Schritt 3: Die Zustandsübergänge testen

Schreiben Sie `CharacterListViewModelTest` mit mindestens diesen Fällen. Schreiben Sie den **Success-Fall ohne Turbine**: leerer Collector im `backgroundScope` + Assert auf `.value` (Handout 8.6, inklusive der beiden Stolperfallen!). Für die übrigen Fälle nutzen Sie Turbine (Handout 8.7); die Uhr bewegt in beiden Welten `advanceUntilIdle` (Handout 8.4):

- [ ] Vor dem ersten Refresh ist der Zustand `Loading`.
- [ ] Nach erfolgreichem Refresh ist der Zustand `Success` mit den Daten des Repositories.
- [ ] Schlägt der Refresh fehl und der Cache hat Daten: `Success` mit `isRefreshFailed = true`, **kein** Error-Screen.
- [ ] Schlägt der Refresh fehl und der Cache ist leer: `Error`.
- [ ] Ein erneuter `refresh()` nach einem Fehler setzt das Flag zurück.
- [ ] `toggleFavorite` schlägt im exponierten Zustand durch (der Favoriten-Stern ist im nächsten `Success` gesetzt).

## ✅ Definition of Done

- [ ] `CharacterRepository` ist ein Interface; kein ViewModel-Code wurde geändert.
- [ ] Kein Feature-Modul hängt an `:core:data:impl`, nur `:app`. Probieren Sie testweise, `OfflineFirstCharacterRepository` in einem Feature zu importieren: Der Compiler kennt die Klasse gar nicht mehr.
- [ ] Die ViewModel-Tests laufen auf der JVM in Millisekunden: kein `Thread.sleep`, kein Emulator.
- [ ] Jeder Test, der Coroutinen anstößt, steuert die Zeit explizit (`advanceUntilIdle`).
- [ ] `./gradlew test` läuft grün (Repository-Tests **und** ViewModel-Tests).

## 💡 Tipps

* Das neue `:core:data:api`-Modul braucht `kotlinx-coroutines-core` als eigene Dependency, denn `Flow` in den Signaturen kommt nicht von allein auf den Klassenpfad.
* Ohne Subscriber startet `stateIn(WhileSubscribed)` den Upstream gar nicht erst (Stolperfalle 1 aus Handout 8.6): der leere `backgroundScope`-Collector ist der Subscriber, bei Turbine übernimmt das der `test { }`-Block.
* An die Felder von `Success` kommen Sie über `assertIs<CharacterListUiState.Success>(...)`: prüft und typisiert in einem Schritt, statt eines rohen `as`-Casts mit kryptischer `ClassCastException` im Fehlerfall (Handout 8.7).
* Ein `StateFlow` konfliert und dedupliziert: Zwischenwerte können verschwinden, zwei identische Zustände erzeugen nur **eine** Emission (Stolperfalle 2). Deshalb: Asserts auf `.value` bzw. `expectMostRecentItem()`, wenn Zwischenzustände uninteressant sind.
* Denken Sie daran: Das `init { refresh() }` läuft beim Erzeugen des ViewModels, aber erst `advanceUntilIdle()` lässt es wirklich arbeiten (`StandardTestDispatcher`).

---

**Fertig?** Die Musterlösung finden Sie im Branch `lab-2-final`, dem Abschluss von Tag 2.
