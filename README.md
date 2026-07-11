# 🧪 Lab 2 – Übung 2.1: Entkoppelte Analytics- & Logging-Schnittstellen

**Willkommen zu Tag 2!** Dieser Branch (`lab-2-uebung-2.1`) entspricht inhaltlich dem Endstand von Tag 1 (`lab-1-final`): die modulare Offline-First-App ist unser Ausgangspunkt.

> 📘 Die Theorie zu dieser Übung finden Sie im [HANDOUT.md](HANDOUT.md), **Modul 7** (Architektur-konformes Analytics & Logging). Neue Dependencies brauchen Sie für diese Übung **keine**.

---

## 🔍 Die Ausgangslage

Der Kunde möchte wissen, welche Screens genutzt werden und welche Charaktere favorisiert werden. Außerdem wünscht sich das Ops-Team technisches Logging aus der Datenschicht ("Warum schlagen Refreshes fehl?").

Der naive Weg wäre, `FirebaseAnalytics.logEvent(...)` und `Log.d(...)` überall dort hinzuschreiben, wo etwas passiert, also quer durch die ViewModels. Warum das ein Architektur-Unfall ist, steht im Handout (Modul 7.1). Wir machen es richtig.

## 🎯 Das Ziel

Bauen Sie eine **entkoppelte Observability-Infrastruktur** nach der Zuständigkeits-Landkarte aus Modul 7.2:

* Ein neues Modul-Paar `:core:analytics:api` / `:core:analytics:impl` mit den Schnittstellen `AnalyticsTracker` und `AppLogger` plus Logcat-Implementierungen, api/impl-getrennt nach Modul 3.3.
* **Screen-Tracking und User-Events in der UI-Schicht**: über einen Lifecycle-Nebeneffekt und ein `CompositionLocal`.
* **Technisches Logging in der Datenschicht**: über den injizierten `AppLogger` im Repository.
* **Die ViewModels bleiben unangetastet**: kein einziger Analytics- oder Logging-Aufruf darin!

## 🛠 Die Aufgaben im Detail

### Schritt 1: Das Modul-Paar `:core:analytics:api` / `:core:analytics:impl`

Ein frisches Modul mit klarem Vertrag: der perfekte Kandidat, um die **api/impl-Trennung aus Modul 3.3** von Anfang an richtig zu machen:

1. Legen Sie **zwei** Module an (`settings.gradle.kts` + je eine `build.gradle.kts`); seit Übung 1.3 heißt das: `alias(libs.plugins.rickandmorty.android.library)` statt Copy-Paste aus Anhang A. `:core:analytics:api` braucht zusätzlich `kotlin-compose` (für das Tracking-Composable), `:core:analytics:impl` zusätzlich `ksp` + `hilt` für die Bindings und hängt per `api(projects.core.analytics.api)` am Vertrag.
2. In **`:api`**: die Interfaces `AnalyticsTracker` (`trackScreen`, `trackEvent`) und `AppLogger` (`debug`, `error`), die Signaturen stehen in Modul 7.3.
3. In **`:impl`**: `LogcatAnalyticsTracker` und `LogcatLogger` (einfach `android.util.Log`, jeweils mit `@Inject constructor()`).
4. Ebenfalls in **`:impl`**: das `AnalyticsModule`: hier lernen Sie den neuen Hilt-Baustein **`@Binds`** kennen (Modul 7.3).

### Schritt 2: UI-Schicht – Screen-Tracking

1. Erstellen Sie in `:core:analytics:api` das `LocalAnalyticsTracker`-CompositionLocal (mit No-Op-Default für Previews) und das Composable `TrackScreen(screenName)` mit `LaunchedEffect` (Vorlage: Modul 7.4).
2. Stellen Sie den echten Tracker in der `MainActivity` bereit: `@Inject lateinit var` + `CompositionLocalProvider` um den bestehenden Content.
3. Instrumentieren Sie beide Screens: `TrackScreen("character_list")` bzw. `TrackScreen("character_detail")`.
4. Tracken Sie das User-Event **Favorit umschalten** im Listen-Screen (`trackEvent("toggle_favorite", ...)` mit der Charakter-ID als Parameter), und zwar am Interaktionspunkt, nicht im ViewModel!

### Schritt 3: Datenschicht – Logging

1. Injizieren Sie den `AppLogger` in das `CharacterRepository` (Dependency in `:core:data` nicht vergessen!).
2. Loggen Sie erfolgreiche Refreshes (`debug`, z.B. mit Anzahl der Datensätze) und fehlgeschlagene per **Log-and-Rethrow** (`error` mit Throwable), Vorlage in Modul 7.5. Achtung: `CancellationException` weder loggen noch schlucken (Modul 2.1)!

### Schritt 4: Die Tests reparieren

Das Repository hat jetzt einen dritten Konstruktor-Parameter, damit kompilieren die Tests in `:core:data` nicht mehr. Schreiben Sie einen kleinen `FakeAppLogger` (der Aufrufe in Listen sammelt) und reichen Sie ihn ein. **Bonus:** Ein neuer Test, der verifiziert, dass ein fehlgeschlagener Refresh tatsächlich einen `error`-Eintrag loggt. Logging ist jetzt testbar!

## ✅ Definition of Done

- [ ] Beim Navigieren erscheinen `screen_view`-Logs in Logcat, beim Favorisieren ein `toggle_favorite`-Event.
- [ ] Refresh im Flugzeugmodus erzeugt einen `error`-Log-Eintrag aus dem Repository.
- [ ] `git diff` über die ViewModels ist **leer**.
- [ ] Kein Feature- oder Datenschicht-Code referenziert `android.util.Log` direkt, nur die Logcat-Implementierungen in `:core:analytics:impl`.
- [ ] Außer `:app` hängt kein Modul an `:core:analytics:impl`.
- [ ] `./gradlew test` läuft grün (inklusive des neuen Logging-Tests).

## 💡 Tipps

* `@Binds` braucht ein `abstract class`-Modul und abstrakte Funktionen: Implementierung als Parameter, Interface als Rückgabetyp.
* `staticCompositionLocalOf` statt `compositionLocalOf`: Der Tracker ändert sich zur Laufzeit nie.
* Denken Sie an die Modul-Abhängigkeiten: `:core:data`, beide Features und `:app` brauchen `implementation(projects.core.analytics.api)`, und **nur** `:app` zusätzlich `implementation(projects.core.analytics.impl)`, damit Hilt die Bindings findet (Modul 3.3/4.4).
* Moment, "beide Features brauchen dieselbe Zeile"? Das ist ein Fall für das `AndroidFeatureConventionPlugin`: Screen-Tracking gehört ab jetzt zu "was *jedes* Feature braucht". Eine Zeile in `build-logic` statt zwei (bald: n) Modul-Dateien anfassen.
* In `PlaceDto.kt` versteckt sich noch ein direkter `android.util.Log`-Aufruf. Tipp: Mit `toIntOrNull()` statt `try/catch` wird das Logging dort schlicht überflüssig.

---

**Fertig?** Die Musterlösung, und damit die Aufgabenstellung für **Übung 2.2 (ViewModel-Tests mit virtueller Zeit)**, finden Sie im Branch `lab-2-uebung-2.2`.
