# 🧪 Lab 1 – Übung 1.2: Lokaler Daten-Cache & Offline-First Repository (Room)

Dieser Branch (`lab-1-uebung-1.2`) enthält die **Musterlösung zu Übung 1.1** und ist gleichzeitig der Startpunkt für Übung 1.2.

> 📘 Die Theorie zu dieser Übung finden Sie im [HANDOUT.md](HANDOUT.md), **Modul 5** (Offline-First-Strategien) sowie **Modul 2.4–2.6** (Flow, `combine` & `stateIn`). Die benötigten Dependencies stehen in **Anhang A**.

---

## ✅ Rückblick: Die Lösung zu Übung 1.1

Das ist neu bzw. anders gegenüber dem `main`-Branch:

* **`RickAndMortyApplication.kt`**: mit `@HiltAndroidApp` annotiert und im Manifest registriert. Hier entsteht der DI-Graph.
* **`di/NetworkModule.kt`**: die "Rezepte" für `Retrofit` und `RickAndMortyApi` als `@Provides`-Funktionen im `SingletonComponent`.
* **`CharacterRepository`**: bekommt die Api jetzt per `@Inject constructor` und ist `@Singleton`.
* **`CharacterListViewModel`**: `@HiltViewModel`, Repository kommt über den Konstruktor. Im Screen: `hiltViewModel()` statt `viewModel()`.
* **`CharacterDetailViewModel`**: nutzt Assisted Injection. Das Repository liefert Hilt, die `id` kommt zur Laufzeit über die `@AssistedFactory`. Die handgeschriebene `ViewModelProvider.Factory` ist Geschichte.
* **`Dependencies.kt`**: gelöscht. 🪦

Vergleichen Sie gerne mit Ihrer eigenen Lösung: `git diff main lab-1-uebung-1.2`

---

## 🔍 Die Ausgangslage

Machen Sie das Experiment: Starten Sie die App einmal mit Internet, schließen Sie sie, aktivieren Sie den **Flugzeugmodus** und starten Sie sie erneut.

Ergebnis: eine Fehlermeldung. Alle Daten, die wir eben noch hatten, sind weg. Und noch etwas fällt auf: Markieren Sie einen Favoriten und starten Sie die App neu, auch der ist weg, denn er lebt nur im `StateFlow`.

## 🎯 Das Ziel

Bauen Sie die Datenschicht auf **Offline-First mit Room** um, nach dem **SSOT-Prinzip** (Single Source of Truth):

* Die UI beobachtet **ausschließlich die Datenbank** (per `Flow`).
* Das Netzwerk **schreibt nur noch in die Datenbank**, nie in die UI.
* Die App startet auch im Flugzeugmodus mit Inhalt (sofern einmal geladen wurde).
* Favoriten werden persistent gespeichert und überleben App-Neustarts, auch einen Daten-Refresh!
* Das Offline-Verhalten des Repositories ist mit **Unit-Tests** abgesichert.

## 🛠 Die Aufgaben im Detail

### Schritt 1: Dependencies einbinden

Ergänzen Sie **Room** (runtime, ktx, compiler via `ksp`) sowie **JUnit** und **kotlinx-coroutines-test** (die fertigen Einträge stehen im [HANDOUT.md, Anhang A](HANDOUT.md#anhang-a-setup--dependencies-für-tag-1)). Danach: Sync!

### Schritt 2: Die Datenbank-Schicht

Erstellen Sie im Package `character/data/db`:

1. **`CharacterEntity`**: die Tabelle (`@Entity`, `@PrimaryKey val id`). Flachen Sie `origin`/`location` bewusst zu Spalten ab (`originId`, `originName`, ...) und vergessen Sie die Spalte `isFavorite` nicht. Schreiben Sie Mapper: `CharacterEntity.toDomain()` und `CharacterDto.toEntity(isFavorite: Boolean)`.
2. **`CharacterDao`** mit:
   * `observeAll(): Flow<List<CharacterEntity>>` und `observeById(id): Flow<CharacterEntity?>` (Room feuert automatisch bei jeder Änderung neu!)
   * `upsertAll(...)` / `upsert(...)` für den Refresh-Pfad
   * einer Update-Möglichkeit für `isFavorite` und einer Query für die aktuellen Favoriten-IDs
3. **`RickAndMortyDatabase`**: die `@Database`-Klammer.
4. **`di/DatabaseModule`**: stellt Datenbank (via `Room.databaseBuilder`, braucht `@ApplicationContext context: Context`) und DAO als `@Singleton` bereit.

### Schritt 3: Das Repository umbauen (SSOT!)

Trennen Sie Lese- und Schreibpfad strikt:

```kotlin
fun observeCharacters(): Flow<List<Character>>   // READ:  DB beobachten
fun observeCharacter(id: Int): Flow<Character?>  // READ:  DB beobachten
suspend fun refreshCharacters()                  // WRITE: API -> DB
suspend fun refreshCharacter(id: Int)            // WRITE: API -> DB
suspend fun toggleFavorite(id: Int)              // WRITE: nur DB
```

**⚠️ Die Denksportaufgabe dabei:** Die API kennt keine Favoriten. Ein naiver `upsert` der API-Antwort würde also bei jedem Refresh alle `isFavorite`-Flags auf `false` zurücksetzen. Sorgen Sie dafür, dass der Refresh die lokalen Favoriten **erhält**.

### Schritt 4: Die ViewModels reaktiv machen

1. **`CharacterListViewModel`:** Der `uiState` entsteht jetzt aus dem Datenbank-Flow (`repository.observeCharacters()`), kombiniert mit dem Refresh-Status per `combine` und in die UI gehoben per `stateIn` (siehe Handout 2.5, 2.6 und 5.5) statt aus einem manuell gepflegten `MutableStateFlow`. Der Refresh wird im `init` angestoßen; schlägt er fehl, fliegt **kein** Fehler-Screen rein, solange gecachte Daten da sind.
2. **UI-State erweitern:** `Success` bekommt ein Flag `isRefreshFailed` (siehe Handout 5.5). Zeigen Sie in dem Fall einen dezenten Hinweis über der Liste (z.B. ein Banner "Offline — Daten evtl. nicht aktuell"). Nur wenn die Datenbank leer ist **und** der Refresh fehlschlägt, zeigen wir den Fehler-Zustand.
3. **`toggleFavorite`** delegiert nur noch ans Repository, die UI aktualisiert sich von selbst über den Flow. Genau das ist SSOT!
4. **`CharacterDetailViewModel`:** gleiche Kur, Detail-Daten aus `observeCharacter(id)`, Refresh separat.

### Schritt 5: Tests schreiben

Legen Sie unter `app/src/test/` Unit-Tests für das Repository an (mit `runTest`, siehe Handout 5.6). Schreiben Sie **Fakes** für Api und DAO (kein Mock-Framework nötig) und testen Sie mindestens:

- [ ] `refreshCharacters()` schreibt die API-Daten in die Datenbank.
- [ ] Schlägt der Refresh fehl (Api wirft `IOException`), bleiben die gecachten Daten über `observeCharacters()` verfügbar.
- [ ] `toggleFavorite()` persistiert den Favoriten-Status.
- [ ] Ein Refresh überschreibt vorhandene Favoriten **nicht**.

## ✅ Definition of Done

- [ ] Flugzeugmodus-Test: App neu starten → die zuletzt geladenen Charaktere erscheinen (plus Offline-Hinweis).
- [ ] Favorit markieren, App killen, neu starten → Favorit ist noch da.
- [ ] Favorit markieren, Refresh ausführen → Favorit ist noch da.
- [ ] Die UI liest nirgendwo mehr direkt aus der API.
- [ ] `./gradlew test` läuft grün.

## 💡 Tipps

* Room-DAOs mit `suspend`/`Flow` sind automatisch main-safe, Sie brauchen nirgendwo `Dispatchers.IO`.
* Für den Refresh-Pfad bietet sich `@Upsert` an.
* Für den Fake-DAO im Test eignet sich eine `MutableStateFlow<Map<Int, CharacterEntity>>` als In-Memory-"Tabelle".
* Denken Sie an `CancellationException` beim Fehlerbehandeln im ViewModel (Handout 2.1)!

---

**Fertig?** Die Musterlösung, und damit die Aufgabenstellung für **Übung 1.3 (Modularisierung)**, finden Sie im Branch `lab-1-uebung-1.3`.
