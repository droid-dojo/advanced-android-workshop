# 🏁 Lab 1 – Finale: Die modulare Offline-First-App

**Geschafft!** Dieser Branch (`lab-1-final`) enthält die **Musterlösung zu Übung 1.3** und damit den vollständigen Endstand von Tag 1.

---

## ✅ Rückblick: Die Lösung zu Übung 1.3

Die App ist jetzt in sechs Gradle-Module zerlegt, **ohne eine einzige Zeile Logik anzufassen**:

```
:app                        → Application, MainActivity (Navigation-Verdrahtung)
:feature:characterlist      → Listen-Screen, ViewModel, Route
:feature:characterdetail    → Detail-Screen, ViewModel, Route (+ eigene String-Ressource)
:core:data                  → Repository, Retrofit, Room, Hilt-Module, Repository-Tests
:core:model                 → Character, Place, Sample-Daten
:core:ui                    → Theme, PreviewContainer
build-logic                 → Included Build mit den Convention Plugins (Schritt 5)
```

Die interessanten Stellen der Lösung:

* **`api` vs. `implementation`:** `:core:data` reicht `:core:model` per `api(...)` weiter, weil `Character` in der öffentlichen Repository-Signatur auftaucht. Alles andere ist `implementation`, Retrofit- und Room-Typen bleiben in `:core:data` eingesperrt. Genau die Grenze, die vorher nur Konvention war, erzwingt jetzt der Compiler.
* **Root-`build.gradle.kts`:** Alle Plugins (auch `android-library`, `ksp`, `hilt`) sind dort mit `apply false` deklariert, sonst beschwert sich Gradle, sobald zwei Module dasselbe Plugin mit Version anfordern.
* **Hilt über Modul-Grenzen:** `NetworkModule` und `DatabaseModule` leben jetzt in `:core:data`. In `:app` steht dazu: nichts. Hilt sammelt die Rezepte beim Kompilieren automatisch aus allen Modulen ein (Handout, Modul 4.4).
* **Features kennen sich nicht:** `:feature:characterlist` weiß nicht, dass es einen Detail-Screen gibt. Die Verbindung (`onCharacterClick` → `backStack.add(CharacterDetailRoute(id))`) zieht `:app` in der `MainActivity`.
* **Ressourcen pro Modul:** Der Titel-String des Detail-Screens ist als `character_detail_title` ins Feature-Modul umgezogen, der `R`-Import zeigt jetzt auf dessen Namespace.
* **Convention Plugins (Schritt 5):** `compileSdk`, `minSdk` und Java-Version stehen nur noch in `AndroidLibraryConventionPlugin`; `AndroidFeatureConventionPlugin` bündelt darauf Compose, Hilt/KSP und die geteilten Feature-Dependencies. Die `build.gradle.kts` der Feature-Module ist dadurch auf `alias(libs.plugins.rickandmorty.android.feature)` + `namespace` + `implementation(projects.core.data)` geschrumpft. Vergleichen Sie sie einmal mit Ihrer handverdrahteten Fassung aus Schritt 3!
* **Die Tests** liegen bei ihrem Testobjekt in `:core:data` und laufen weiterhin mit `./gradlew test`.

Vergleichen Sie gerne mit Ihrer eigenen Lösung: `git diff lab-1-uebung-1.3 lab-1-final`

---

## 🛠 Was wir an Tag 1 gebaut haben

Aus der "fertigen" App des Einführungs-Workshops ist eine Enterprise-taugliche Architektur geworden:

* **Dependency Injection (Übung 1.1):** Der handgestrickte Service Locator (`Dependencies.kt`) ist einem deklarativen Hilt-Setup gewichen: Constructor Injection überall, Assisted Injection für Navigations-Argumente, Verdrahtungsfehler sind jetzt Compile-Fehler.
* **Offline-First mit Room (Übung 1.2):** Die Datenbank ist die Single Source of Truth. Die UI beobachtet nur noch die DB (`Flow` + `combine` + `stateIn`), das Netzwerk aktualisiert sie. Die App startet im Flugzeugmodus mit Inhalt, Favoriten überleben Neustart und Refresh, abgesichert durch Unit-Tests mit Fakes.
* **Modularisierung (Übung 1.3):** Feature- und Core-Module mit compiler-erzwungenen Grenzen, sauberem `api`/`implementation`-Schnitt und Hilt als Bindeglied; dazu Convention Plugins in `build-logic`, damit die Modul-Vielfalt nicht in Build-Boilerplate erstickt.

## 💻 Den Code ausführen

1. Branch auschecken, in Android Studio auf **"Sync Project with Gradle Files"** klicken.
2. Emulator starten oder Gerät anschließen, **"Run"** drücken.
3. Der Lackmustest für Tag 1: App einmal mit Internet starten, killen, **Flugzeugmodus an**, neu starten: die Liste ist da, oben erscheint das Offline-Banner. 🎉
4. Tests: `./gradlew test`

## 🎯 Herausforderungen zum Weiterbauen

Wer das Gelernte vertiefen möchte:

1. **Pull-to-Refresh:** Die `refresh()`-Funktion des ViewModels wartet nur darauf, von einer `PullToRefreshBox` (Material 3) aufgerufen zu werden.
2. **Suche als Flow-Übung:** Ein Suchfeld in der TopBar, dessen Eingabe als dritter Flow in das `combine` des Listen-ViewModels einfließt (Handout, Modul 2.5).
3. **Detail-Refresh absichern:** Schreiben Sie Repository-Tests für `refreshCharacter(id)`-Randfälle (Charakter noch nicht im Cache, API liefert 404).
4. **Ein neues Feature-Modul:** Die API bietet auch Episoden an (`/episode`). Bauen Sie ein `:feature:episodelist` – von der Route bis zum Repository-Ausbau, ganz ohne die bestehenden Module anzufassen. Das Gradle-Setup kostet dank `rickandmorty.android.feature` genau drei Zeilen.

## 📚 Wichtige Ressourcen zum Nachschlagen

* **Unser Workshop-Handout:** [📘 HANDOUT.md](HANDOUT.md)
* **Hilt:** [developer.android.com/training/dependency-injection/hilt-android](https://developer.android.com/training/dependency-injection/hilt-android)
* **Room:** [developer.android.com/training/data-storage/room](https://developer.android.com/training/data-storage/room)
* **Offline-First:** [developer.android.com/topic/architecture/data-layer/offline-first](https://developer.android.com/topic/architecture/data-layer/offline-first)
* **Modularisierung:** [developer.android.com/topic/modularization](https://developer.android.com/topic/modularization)

**Weiter geht's:** Die Aufgabenstellung für **Übung 2.1 (Analytics & Logging)**, und damit der Start von Tag 2, wartet im Branch `lab-2-uebung-2.1`.
