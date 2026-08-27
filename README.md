# 🏆 Das Finale: Die Enterprise-Ready Rick & Morty App

**Herzlichen Glückwunsch!** Sie haben das Ende des Advanced-Android-Workshops erreicht. Dieser Branch (`lab-3-final`) enthält die Musterlösung zu Übung 3.3 und damit die vollständige Referenz-Implementierung aller drei Workshop-Tage.

---

## ✅ Rückblick: Die Lösung zu Übung 3.3

Das ist neu bzw. anders gegenüber dem Branch `lab-3-uebung-3.3`:

* **`:core:settings:api` / `:core:settings:impl`** – das neue Modul-Paar (Modul 3.3) für sensible Gerätekonfiguration:
    * Im **api**-Modul der schmale Vertrag: `TerminalConfig` (`@Serializable`) und `SecureSettings`. Im **impl**-Modul alles andere, inklusive `SettingsCipher`, der als interner Baustein bewusst **nicht** Teil des Vertrags ist.
    * `KeystoreSettingsCipher`: AES/GCM mit einem Schlüssel aus dem **Android Keystore**, einmalig erzeugt, danach nur noch benutzt, niemals exportierbar. Der frische IV wandert vor den Ciphertext.
    * `EncryptedSettingsRepository`: Konfiguration → JSON → `encrypt` → Base64 → **transaktionaler** Write in den Preferences DataStore; gelesen wird reaktiv als `Flow`.
    * `SettingsModule`: DataStore-Provider plus zwei `@Binds` (Cipher & Repository), wie an Tag 2 gelernt.
* **`EncryptedSettingsRepositoryTest`**: drei JVM-Tests mit `FakeSettingsCipher` und Temp-DataStore. Roundtrip, `clear()`, und der wichtigste: **auf der Platte liegt kein Klartext**. Der Keystore selbst bleibt bewusst ungetestet auf der JVM: Er existiert nur auf echter Hardware, genau dafür ist der Cipher ein Interface.

Vergleichen Sie gerne mit Ihrer eigenen Lösung: `git diff lab-3-uebung-3.3 lab-3-final`

---

## 🛠 Was wir in drei Tagen gebaut haben

Aus der Einsteiger-App des Einführungs-Workshops ist eine **Enterprise-Architektur** geworden:

**Tag 1 – Fundament:**
* Dependency Injection mit **Hilt** (Constructor Injection, Module, Assisted Injection) statt Service Locator.
* **Offline-First mit Room** nach dem SSOT-Prinzip: Die UI beobachtet die Datenbank, das Netz aktualisiert sie. Favoriten überleben Neustart und Refresh.
* **Modularisierung**: `:app`, zwei Feature-Module und eine Core-Landschaft mit compiler-erzwungenen Grenzen, bis Tag 3 gewachsen zu **api/impl-Paaren** (`:core:data`, `:core:analytics`, `:core:settings`), bei denen Features nur Verträge sehen und allein `:app` die Implementierungen verdrahtet. Die Build-Konventionen dazu stehen genau einmal: als **Convention Plugins** in `build-logic`; jedes neue Modul startet mit einer Zeile.

**Tag 2 – Betriebsreife:**
* Fehler als **Domain-Zustände** statt unkontrollierter Exceptions.
* **Entkoppelte Observability**: Screen-Tracking als Lifecycle-Nebeneffekt in der UI, Logging hinter injizierten Interfaces in der Datenschicht; ViewModels bleiben frei.
* **ViewModel-Tests mit virtueller Zeit**: Interface an der Nahtstelle zum Repository, Fakes, `MainDispatcherRule`, Turbine.

**Tag 3 – Qualität & Sicherheit:**
* **Compose-UI-Tests auf der JVM** (Robolectric): Semantics Tree, Finder/Assertions/Actions, in Sekunden statt Emulator-Minuten.
* **Screenshot-Tests** in beiden Varianten: Roborazzi (Golden Images für jeden Zustand inkl. Dark Mode) und Googles Compose-Preview-Tool, mit Record/Verify-Workflows für die CI.
* **Enterprise Security**: Hardware-verschlüsselte, transaktional gespeicherte Gerätekonfiguration; Certificate Pinning als dokumentiertes Muster.

Die Test-Bilanz: **Vier Test-Suites** (Repository, Settings, ViewModel, UI) plus 13 Referenzbilder aus zwei Screenshot-Werkzeugen, alles auf der JVM, alles CI-tauglich: `./gradlew test`, `./gradlew verifyRoborazziDebug` und `./gradlew validateDebugScreenshotTest` genügen.

## 💻 Den Code ausführen

1. Branch auschecken, **Sync**, **Run**. Der Flugzeugmodus-Test aus Tag 1 funktioniert natürlich immer noch. 🎉
2. Alle Tests: `./gradlew test`
3. Screenshot-Verifikation: `./gradlew verifyRoborazziDebug` (Roborazzi) bzw. `./gradlew validateDebugScreenshotTest` (Compose-Preview-Tool)

## 🎯 Wie geht es jetzt weiter? (Herausforderungen)

1. **Der große Bogen:** Bauen Sie das Repository auf den typisierten `DataResult`-Wrapper um (Modul 6) und ziehen Sie die Änderung durch alle Test-Suites. Sie werden merken: Die Architektur trägt.
2. **Screenshot-Abdeckung ausbauen:** Erweitern Sie die Preview-Screenshot-Suite um `@PreviewFontScale` (große Schriften!) und den Detail-Screen, und entscheiden Sie im Team, welches der beiden Werkzeuge Ihr Standard wird.
3. **Onboarding-Screen:** Ein `:feature:setup`-Modul, das beim ersten Start eine `TerminalConfig` erfasst und über `SecureSettings` speichert. Damit bekommt Übung 3.3 ihre UI. Das Gradle-Setup: `alias(libs.plugins.rickandmorty.android.feature)`, fertig.
4. **CI-Pipeline:** Setzen Sie den kompletten Workflow aus **Modul 12** um: `.github/workflows/ci.yml` mit Build-, Test- und Screenshot-Stage plus Report-Artefakten. Dank JVM-only-Tests reicht ein simpler Linux-Runner ohne Emulator; danach: CI-Check in den Branch-Protection-Regeln zur Pflicht machen.

## 📚 Wichtige Ressourcen zum Nachschlagen

* **Unser Workshop-Handout:** [📘 HANDOUT.md](HANDOUT.md) (Module 1–12 + Anhänge)
* **Compose Testing:** [developer.android.com/develop/ui/compose/testing](https://developer.android.com/develop/ui/compose/testing)
* **Screenshot-Testing:** [developer.android.com/training/testing/ui-tests/screenshot](https://developer.android.com/training/testing/ui-tests/screenshot)
* **Android Keystore:** [developer.android.com/privacy-and-security/keystore](https://developer.android.com/privacy-and-security/keystore)
* **Now in Android** (Googles Referenz-App mit genau dieser Architektur): [github.com/android/nowinandroid](https://github.com/android/nowinandroid)

**Vielen Dank für drei großartige Workshop-Tage und viel Erfolg in Ihren eigenen Projekten!**
