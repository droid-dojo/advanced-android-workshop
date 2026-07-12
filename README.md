# 🧪 Lab 3 – Übung 3.3: Verschlüsselte POS-Konfiguration

Dieser Branch (`lab-3-uebung-3.3`) enthält die **Musterlösung zu Übung 3.2** und ist gleichzeitig der Startpunkt für die letzte Übung des Workshops.

> 📘 Die Theorie zu dieser Übung finden Sie im [HANDOUT.md](HANDOUT.md), **Modul 11** (Enterprise Security). Die Dependency (`datastore-preferences`) steht im Setup-Abschnitt von Tag 3.

---

## ✅ Rückblick: Die Lösung zu Übung 3.2

Die Musterlösung enthält **beide Varianten**; vergleichen Sie Ihre Wahl mit dem jeweiligen Teil:

**Variante A – Roborazzi:**

* **Setup**: Plugin (Root + Feature-Modul) und die beiden `testImplementation`-Dependencies.
* **`ScreenshotContainer`**: der Determinismus-Helfer in den Test-Sourcen. Erzwungener `LocalInspectionMode` + `PreviewContainer` → Coil rendert Farbflächen statt Netzwerk-Bilder.
* **`CharacterListScreenshotTest`**: sieben Golden Images. Success (Light **und** Dark), Offline-Banner, Loading, Error sowie das `CharacterItem` als Favorit und als Nicht-Favorit. Die PNGs liegen unter `feature/characterlist/src/test/screenshots/` **im Git**.
* Workflow: `./gradlew recordRoborazziDebug` zum Aufnehmen, `./gradlew verifyRoborazziDebug` zum Prüfen. Probieren Sie den Kaputtmach-Test aus Schritt 4 der Aufgabe ruhig gegen diese Goldens!

**Variante B – Compose Preview Screenshot Testing (Alpha):**

* **Setup**: Plugin `com.android.compose.screenshot`, das Flag in der `gradle.properties`, `experimentalProperties` im Feature-Modul und die `screenshotTestImplementation`-Dependencies.
* **`CharacterListPreviewScreenshots`**: im eigenen Source Set `src/screenshotTest/` mit drei `@PreviewTest`-Previews (Success, Offline-Banner, Favoriten-Item), dank `@PreviewLightDark` automatisch je Light **und** Dark = sechs Referenzbilder unter `src/screenshotTestDebug/reference/`.
* Workflow: `./gradlew updateDebugScreenshotTest` zum Aufnehmen, `./gradlew validateDebugScreenshotTest` zum Prüfen (HTML-Report unter `build/reports/screenshotTest/preview/`).
* Auffällig: **kein** `ScreenshotContainer` nötig, Previews laufen von Haus aus im Inspection Mode. Dafür fehlen (noch) Interaktions-Screenshots und Toleranz-Konfiguration, genau der Trade-off aus Modul 10.4.

Vergleichen Sie gerne mit Ihrer eigenen Lösung: `git diff lab-3-uebung-3.2 lab-3-uebung-3.3`

---

## 🔍 Die Ausgangslage

Szenario-Wechsel: Unsere App soll als **POS-Begleiter** auf Firmengeräten laufen. Beim Einrichten erhält jedes Gerät eine Terminal-Konfiguration: Terminal-ID und API-Schlüssel. Solche Werte in `SharedPreferences` zu legen hieße: Klartext-XML im App-Verzeichnis. Fällt das Gerät in falsche Hände, ist der Händler-Zugang kompromittiert.

## 🎯 Das Ziel

Ein neues Modul **`:core:settings`**, das sensible Konfiguration **hardware-verschlüsselt und transaktional** speichert:

* Schlüsselmaterial im **Android Keystore** (AES/GCM), es verlässt die Hardware nie.
* Ablage als Ciphertext im **Preferences DataStore**: atomare Writes, reaktives Lesen per `Flow`.
* Saubere Schnittstellen (`SecureSettings`, `SettingsCipher`) mit Hilt-`@Binds`, Sie kennen das Muster aus Tag 2.
* Auf der Platte liegt **kein Klartext**, bewiesen durch einen Test.

## 🛠 Die Aufgaben im Detail

### Schritt 1: Das Modul-Paar `:core:settings:api` / `:core:settings:impl`

Wie bei `:core:analytics` (Übung 2.1) wird das Modul von Anfang an als api/impl-Paar angelegt (Modul 3.3). Beide Build-Dateien starten mit `alias(libs.plugins.rickandmorty.android.library)`: `:core:settings:api` ergänzt `kotlin-serialization` für den Vertrag; `:core:settings:impl` ergänzt `ksp`/`hilt`, `datastore-preferences` und die Test-Dependencies (`junit` + `kotlinx-coroutines-test`).

### Schritt 2: Die Schnittstellen (Modul 11.4)

1. In **`:api`**: `TerminalConfig`, ein `@Serializable data class` mit `terminalId` und `apiKey`, und `SecureSettings` (`val terminalConfig: Flow<TerminalConfig?>`, `suspend fun save(config)`, `suspend fun clear()`).
2. In **`:impl`**: `SettingsCipher` mit `encrypt(ByteArray): ByteArray` / `decrypt(ByteArray): ByteArray`. Warum wohnt dieses Interface **nicht** im api-Modul? Weil es kein Vertrag nach außen ist, sondern ein interner Baustein der Implementierung: kein Konsument außer dem Repository (und dessen Tests) braucht es je zu sehen.

### Schritt 3: Der Keystore-Cipher

Implementieren Sie `KeystoreSettingsCipher` (Modul 11.2): AES/GCM-Schlüssel im `AndroidKeyStore` (einmalig erzeugen, dann wiederverwenden), beim Verschlüsseln den frischen IV **vor** den Ciphertext hängen, beim Entschlüsseln wieder abtrennen.

### Schritt 4: Das Repository

`EncryptedSettingsRepository` implementiert `SecureSettings`: Konfiguration → JSON (`kotlinx.serialization`) → `cipher.encrypt` → Base64-String → `dataStore.edit { }`. Lesen ist der Rückweg als `map` auf `dataStore.data`. Dazu ein `SettingsModule` (Hilt): DataStore bereitstellen (`PreferenceDataStoreFactory` mit `@ApplicationContext`), Cipher und Repository per `@Binds`. Die komplette Verdrahtung steht, Zeile für Zeile erklärt, in **Modul 11.5**.

### Schritt 5: Tests – der Klartext-Beweis

Der Keystore existiert nur auf echter Hardware, genau dafür ist `SettingsCipher` ein Interface! Testen Sie das Repository auf der JVM mit einem `FakeSettingsCipher` (z.B. simples Byte-XOR) und einem DataStore im Temp-Verzeichnis (`TemporaryFolder`-Rule + `PreferenceDataStoreFactory`):

- [ ] Roundtrip: `save(config)` → `terminalConfig.first()` liefert die Konfiguration zurück.
- [ ] `clear()` → `terminalConfig.first()` ist `null`.
- [ ] **Der wichtigste Test:** Nach `save(...)` enthält der rohe DataStore-Inhalt weder `terminalId` noch `apiKey` im Klartext.

### Alternative: Certificate Pinning

Wer statt der Verschlüsselung lieber die **Transportsicherheit** vertieft (Modul 11.6): Bauen Sie den `OkHttpClient` mit `CertificatePinner` in das `NetworkModule` ein. Beachten Sie: Gegen die öffentliche Rick&Morty-API ist Pinning konzeptionell falsch (wir kontrollieren deren Zertifikate nicht; Modul 11.6 erklärt, warum das wichtig ist). Es bleibt also eine Trockenübung mit Dummy-Pins, deren Effekt Sie am fehlschlagenden Request beobachten können. Die Musterlösung implementiert die Verschlüsselungs-Variante.

## ✅ Definition of Done

- [ ] Das Modul-Paar baut; keine Schicht außerhalb kennt DataStore- oder Keystore-Typen (alles hinter `SecureSettings` in `:api`).
- [ ] Außer `:app` hängt kein Modul an `:core:settings:impl`.
- [ ] Die drei Repository-Tests laufen grün auf der JVM, inklusive des Klartext-Beweises.
- [ ] `./gradlew test` bleibt insgesamt grün.

## 💡 Tipps

* `Base64` gibt es in `android.util` **und** `java.util`: nehmen Sie `java.util.Base64`, dann bleibt das Repository JVM-testbar.
* Der IV eines GCM-Ciphers ist 12 Bytes, beim Entschlüsseln `copyOfRange` statt Stringzauber.
* DataStore im Test braucht einen `CoroutineScope`: geben Sie ihm den Scope des Tests (`CoroutineScope(UnconfinedTestDispatcher())`) und ein frisches File pro Test (`TemporaryFolder`).

---

**Fertig?** Die Musterlösung finden Sie im Branch `lab-3-final`, dem großen Finale des Workshops.
