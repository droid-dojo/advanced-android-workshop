# 🧪 Lab 1 – Übung 1.1: Dependency Injection mit Hilt

**Willkommen zum Advanced Android Workshop!**

Ausgangspunkt ist die fertige Rick & Morty App aus dem Einführungs-Workshop. Sie funktioniert, aber sie hat drei Schwachstellen, die wir an Tag 1 beheben: die **manuelle Objekt-Erzeugung** (diese Übung), den **fehlenden Offline-Support** (Übung 1.2) und den **monolithischen Aufbau** (Übung 1.3).

> 📘 Die Theorie zu dieser Übung finden Sie im [HANDOUT.md](HANDOUT.md), **Modul 4** (Dependency Injection mit Hilt). Die benötigten Dependencies stehen in **Anhang A**.

---

## 🔍 Die Ausgangslage

Werfen Sie einen Blick in `Dependencies.kt`:

```kotlin
object Dependencies {
    private val retrofit = Retrofit.Builder()...build()
    val characterRepository = CharacterRepository(rickAndMortyApi)
}
```

Ein globales `object`, aus dem sich die ViewModels ihre Abhängigkeiten selbst holen:

```kotlin
class CharacterListViewModel : ViewModel() {
    private val repository: CharacterRepository = Dependencies.characterRepository
}
```

Das ist ein **Service Locator**: versteckte Abhängigkeiten, keine Austauschbarkeit in Tests, kein Scoping. In einem modularen Enterprise-Projekt wäre dieses zentrale Objekt der Flaschenhals, den jedes Modul kennen müsste.

## 🎯 Das Ziel

Refactoren Sie die App von der manuellen Instanziierung auf eine **deklarative DI-Struktur mit Hilt**:

* Kein `Dependencies.kt` mehr: die Datei wird am Ende **gelöscht**.
* Jede Klasse deklariert ihre Abhängigkeiten **im Konstruktor** (`@Inject`).
* Framework-Objekte (Retrofit, Api) werden in einem **Hilt-Modul** bereitgestellt.
* Die App verhält sich für den User **exakt wie vorher**, reines Refactoring!

## 🛠 Die Aufgaben im Detail

### Schritt 1: Dependencies einbinden

Ergänzen Sie `gradle/libs.versions.toml` und `app/build.gradle.kts` um **KSP**, **Hilt** und **hilt-lifecycle-viewmodel-compose** (die fertigen Einträge stehen im [HANDOUT.md, Anhang A](HANDOUT.md#anhang-a-setup--dependencies-für-tag-1)). Danach: Sync!

### Schritt 2: Hilt aktivieren

1. Erstellen Sie eine Application-Klasse `RickAndMortyApplication` mit der Annotation `@HiltAndroidApp`.
2. Registrieren Sie sie im `AndroidManifest.xml` (`android:name`).
3. Annotieren Sie die `MainActivity` mit `@AndroidEntryPoint`.

### Schritt 3: Das Netzwerk-Modul

Erstellen Sie ein `NetworkModule` (`@Module`, `@InstallIn(SingletonComponent::class)`), das die bisherigen Inhalte von `Dependencies.kt` als `@Provides`-Funktionen bereitstellt:

* `Retrofit` (inkl. `Json`-Konfiguration und Converter) als `@Singleton`
* `RickAndMortyApi` als `@Singleton`

### Schritt 4: Constructor Injection im Repository

Machen Sie `CharacterRepository` per `@Inject constructor` für Hilt erzeugbar und geben Sie ihm den Scope `@Singleton`.

### Schritt 5: Die ViewModels

1. **`CharacterListViewModel`:** Annotieren Sie es mit `@HiltViewModel` und injizieren Sie das Repository über den Konstruktor. Im `CharacterListScreen` ersetzen Sie `viewModel()` durch `hiltViewModel()`.
2. **`CharacterDetailViewModel`:** Hier kommt die `id` zur Laufzeit aus der Navigation. Nutzen Sie **Assisted Injection** (`@HiltViewModel(assistedFactory = ...)`, `@AssistedInject`, `@Assisted`, `@AssistedFactory`). Die handgeschriebene `ViewModelProvider.Factory` können Sie ersatzlos streichen. In der `MainActivity` bauen Sie das ViewModel dann über `hiltViewModel(creationCallback = ...)` (siehe Handout, Modul 4.2, Baustein 5).

### Schritt 6: Aufräumen

Löschen Sie `Dependencies.kt`. Wenn das Projekt danach noch kompiliert, haben Sie nichts vergessen. 🎉

## ✅ Definition of Done

- [ ] `Dependencies.kt` existiert nicht mehr.
- [ ] Kein ViewModel greift mehr auf ein globales Objekt zu, alle Abhängigkeiten stehen im Konstruktor.
- [ ] Retrofit & Api werden über ein Hilt-Modul bereitgestellt (`@Singleton`).
- [ ] Beide Screens funktionieren wie vorher (Liste laden, Detail öffnen, Favoriten togglen).
- [ ] Die App baut ohne Warnungen von Hilt/KSP.

## 💡 Tipps

* Arbeiten Sie sich **von unten nach oben** durch den Graphen: erst Modul (Retrofit/Api), dann Repository, dann ViewModels. So bleibt das Projekt zwischendurch möglichst lange kompilierbar.
* Hilt meldet Verdrahtungsfehler **beim Kompilieren**. Lesen Sie die Fehlermeldung genau, sie benennt fast immer die fehlende Binding-Quelle.
* `hiltViewModel()` kommt aus `androidx.hilt.lifecycle.viewmodel.compose`, nicht mit `viewModel()` aus `lifecycle-viewmodel-compose` verwechseln.

---

**Fertig?** Die Musterlösung, und damit die Aufgabenstellung für **Übung 1.2 (Offline-First mit Room)**, finden Sie im Branch `lab-1-uebung-1.2`.
