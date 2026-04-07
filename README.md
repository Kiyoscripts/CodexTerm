# CodexTerm

CodexTerm is a Kotlin-only Android app that combines a mock AI code assistant with a terminal experience designed for Termux interoperability.

## Features

- Jetpack Compose UI with Material 3 dark theme
- MVVM + clean-ish layering with repositories and use cases
- Mock `CodexRepository` that streams generated code fragments via `Flow`
- Terminal backend selector:
  - Primary: Termux inter-app bridge via `com.termux.api.RUN_COMMAND`
  - Fallback: in-app sandboxed shell using `ProcessBuilder("sh", "-c", ...)`
- Local prompt history persisted with `SharedPreferences`
- Compose navigation across Home, Prompt, and Terminal screens

## Build Requirements

- Android Studio Koala or newer
- Android SDK 34
- JDK 17

## Build

```bash
cd /root/CodexTerm
./gradlew assembleDebug
```

The debug APK will be generated at `/root/CodexTerm/app/build/outputs/apk/debug/`.

## Signing

For a release build, add signing config values in `/root/CodexTerm/app/build.gradle.kts` and run:

```bash
./gradlew assembleRelease
```

The release build reads these environment variables:

- `CODEXTERM_UPLOAD_STORE_FILE`
- `CODEXTERM_UPLOAD_STORE_PASSWORD`
- `CODEXTERM_UPLOAD_KEY_ALIAS`
- `CODEXTERM_UPLOAD_KEY_PASSWORD`

## GitHub Actions

The repository includes a workflow at [`android-apk.yml`](/root/CodexTerm/.github/workflows/android-apk.yml).

- On every push to `main` or `master`, pull request, or manual dispatch, it builds `assembleDebug`
- It uploads the debug APK as the `codexterm-debug-apk` artifact
- If signing secrets are configured, it also builds `assembleRelease` and uploads `codexterm-release-apk`

To enable signed release builds in GitHub Actions, add these repository secrets:

- `CODEXTERM_UPLOAD_KEYSTORE_BASE64`
- `CODEXTERM_UPLOAD_STORE_PASSWORD`
- `CODEXTERM_UPLOAD_KEY_ALIAS`
- `CODEXTERM_UPLOAD_KEY_PASSWORD`

Example to create the base64 keystore value locally:

```bash
base64 -w 0 your-upload-keystore.jks
```

## Notes

- No root access is used or assumed.
- The fallback shell runs inside the app sandbox with a minimized environment.
- Termux command result capture varies by installed Termux/Termux:API plugin support; the bridge is structured so a real callback contract can replace the mock handoff messaging.
