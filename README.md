# Supervision Livraisons

Monorepo for the delivery supervision platform.

## Projects

- `livraisons-backend`: Spring Boot backend with WebSocket support, MongoDB persistence, and JWT security.
- `SupervisionLivraisons`: Android application for couriers and supervisors.

## Requirements

- Java 17 for the backend
- Android Studio / Android SDK 34 for the mobile app
- MongoDB access for the backend

## Backend

The backend lives in `livraisons-backend/` and uses Spring Boot 3.2.5.

Run it from the module directory:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

The backend defaults to port `8082`.

## Android app

The Android app lives in `SupervisionLivraisons/` and uses Gradle.

Build a debug APK from the module directory:

```bash
./gradlew assembleDebug
```

On Windows:

```bat
gradlew.bat assembleDebug
```

To point the app at a local backend, pass the backend host when building or syncing:

```bash
./gradlew assembleDebug -PbackendHost=10.0.2.2
```

For a physical device, use the machine's LAN IP instead of `10.0.2.2`.

## Configuration

- Backend configuration is in `livraisons-backend/src/main/resources/application.properties`.
- The Android app declares its launcher activity in `SupervisionLivraisons/app/src/main/AndroidManifest.xml`.
- The Google Maps API key placeholder in the manifest should be replaced before release.

## Repository layout

```text
livraisons-backend/         Spring Boot backend
SupervisionLivraisons/      Android application
```
