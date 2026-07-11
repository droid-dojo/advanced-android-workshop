# 🏁 Lab 2 – Finale: Observability & getestete reaktive Logik

**Geschafft!** Dieser Branch (`lab-2-final`) enthält die **Musterlösung zu Übung 2.2** und damit den vollständigen Endstand von Tag 2.

---

## ✅ Rückblick: Die Lösung zu Übung 2.2

Das ist neu bzw. anders gegenüber dem Branch `lab-2-uebung-2.2`:

* **`CharacterRepository` ist jetzt ein Interface**: die Implementierung heißt `OfflineFirstCharacterRepository` und wird im neuen `DataModule` per `@Binds` verdrahtet. Die ViewModels blieben unangetastet: Sie verlangten schon immer nur den Vertrag, Hilt liefert jetzt eben die Implementierung dahinter. Das ist Dependency Inversion in Aktion.
* **`:core:data` ist jetzt ein api/impl-Paar** (Modul 3.3): Das Interface wohnt in `:core:data:api`, alles andere (Retrofit, Room, Hilt-Module, Tests) in `:core:data:impl`. Die Features kompilieren nur noch gegen den Vertrag, `OfflineFirstCharacterRepository` ist für sie physisch unsichtbar. Allein `:app` hebt `:impl` in den Graphen. Und dank Convention Plugin war der Gradle-Anteil des Umbaus trivial: Beide neuen Build-Dateien starten mit einer Zeile `alias(libs.plugins.rickandmorty.android.library)`.
* **`feature/characterlist/src/test/`** – die neue Test-Suite:
    * `MainDispatcherRule`: das `setMain`/`resetMain`-Muster aus Handout 8.3, einmal gebündelt statt als `@Before`/`@After`-Copy-Paste in jeder Testklasse. `viewModelScope` läuft damit auf der virtuellen Test-Uhr.
    * `FakeCharacterRepository`: eine reaktive In-Memory-Implementierung mit steuerbarer "Netzwerk-Antwort" und steuerbarem Fehlverhalten.
    * `CharacterListViewModelTest`: sechs Tests, die **jeden Zustandsübergang** des ViewModels verifizieren: Loading vor dem ersten Refresh, Success nach Refresh, Fehler mit Cache (`isRefreshFailed`-Flag statt Error-Screen!), Fehler ohne Cache (Error), Flag-Reset beim Retry, Favoriten-Toggle.
* **Der Success-Fall steht bewusst ohne Turbine da:** leerer Collector im `backgroundScope` (sonst startet `stateIn(WhileSubscribed)` nie, Stolperfalle 1 aus Handout 8.6) plus Assert auf `.value` (konflationssicher, Stolperfalle 2).
* **Turbine** verkürzt die übrigen Fälle (`test { }`, `awaitItem()`, `expectMostRecentItem()`), und `assertIs` aus `kotlin-test` prüft und typisiert die Sealed-States in einem Schritt, statt roher `as`-Casts. `advanceUntilIdle()` bewegt in beiden Welten die virtuelle Uhr. Kein `Thread.sleep`, kein Emulator: die komplette Suite läuft in Millisekunden auf der JVM.

Vergleichen Sie gerne mit Ihrer eigenen Lösung: `git diff lab-2-uebung-2.2 lab-2-final`

---

## 🛠 Was wir an Tag 2 gebaut haben

* **Fehlerbehandlung als Architektur-Thema (Modul 6):** Fehler sind Domain-Zustände, keine Überraschungen: vom `isRefreshFailed`-Flag bis zum typisierten `DataResult`-Wrapper mit Exception-Mapping an der Schicht-Grenze.
* **Entkoppelte Observability (Übung 2.1):** Screen-Tracking und User-Events leben als Lifecycle-Nebeneffekte in der UI-Schicht, technisches Logging hinter dem injizierten `AppLogger` in der Datenschicht, und die ViewModels wissen von alledem nichts. Anbieterwechsel = ein `@Binds`-Modul tauschen.
* **Getestete reaktive Logik (Übung 2.2):** Interface an der Nahtstelle zum Repository (physisch besiegelt durch das api/impl-Paar `:core:data:api`/`:core:data:impl`), Fakes statt Mocks, virtuelle Zeit statt echter: Die `combine`/`stateIn`-Maschinerie aus Tag 1 ist jetzt beweisbar korrekt.

## 💻 Den Code ausführen

1. Branch auschecken, **Sync**, **Run**.
2. Logcat beobachten (Filter: `Analytics` bzw. `OfflineFirstCharacterRepository`): Beim Navigieren erscheinen `screen_view`-Einträge, beim Favorisieren `toggle_favorite`, beim Refresh im Flugzeugmodus ein Error-Log aus der Datenschicht.
3. Tests: `./gradlew test` (Repository- und ViewModel-Suite zusammen).

## 🎯 Herausforderungen zum Weiterbauen

1. **Typisierte Fehler:** Bauen Sie das Repository auf den `DataResult`-Wrapper aus Modul 6.3/6.4 um, inklusive `safeCall` und eigenem Error-Mapping. Wie verändert sich das ViewModel, wie die Tests?
2. **Detail-ViewModel testen:** Das `CharacterDetailViewModel` hat dieselbe reaktive Struktur. Schreiben Sie die Test-Suite dafür (der `FakeCharacterRepository` lässt sich wiederverwenden, wenn Sie ihn in ein geteiltes Test-Modul oder per `testFixtures` verschieben).
3. **Analytics verifizieren:** Ein `FakeAnalyticsTracker` plus ein Test, der prüft, dass `toggle_favorite` mit der richtigen Charakter-ID getrackt wird. Dank CompositionLocal geht das sogar als reiner Composable-Test (Vorgeschmack auf Tag 3!).
4. **Debug vs. Release:** Binden Sie den `LogcatLogger` nur im Debug-Build und einen No-Op-Logger im Release-Build (Stichwort: Hilt-Module pro Build-Variante über Source Sets).

## 📚 Wichtige Ressourcen zum Nachschlagen

* **Unser Workshop-Handout:** [📘 HANDOUT.md](HANDOUT.md)
* **Coroutines testen:** [developer.android.com/kotlin/coroutines/test](https://developer.android.com/kotlin/coroutines/test)
* **Flows testen (Hot vs. Cold):** [developer.android.com/kotlin/flow/test](https://developer.android.com/kotlin/flow/test)
* **Turbine:** [github.com/cashapp/turbine](https://github.com/cashapp/turbine)
* **Hilt Testing Guide:** [developer.android.com/training/dependency-injection/hilt-testing](https://developer.android.com/training/dependency-injection/hilt-testing)

**Weiter geht's:** Die Aufgabenstellung für **Übung 3.1 (UI-Tests auf der JVM)**, und damit der Start von Tag 3, wartet im Branch `lab-3-uebung-3.1`.
