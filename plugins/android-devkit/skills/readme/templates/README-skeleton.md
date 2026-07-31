<!--
  README skeleton — house style.
  Replace every ALL-CAPS placeholder, then DELETE every section the project
  does not actually have. A section with placeholder text left in it is worse
  than no section. Delete this comment before committing.

  Accent colour: pick one hex and use it for every non-vendor badge below.
  Search-and-replace ACCENT with it (e.g. 8c7bff).
-->

<div align="center">

<img src="media/logo.png" alt="PROJECT logo" width="150">

# PROJECT

**ONE LINE: WHAT IT DOES, FOR WHOM. NO ADJECTIVES.**

*A SECOND LINE WITH THE MECHANISM — WHAT IT READS → WHAT IT PRODUCES.*

<br>

[![CI](https://img.shields.io/github/actions/workflow/status/OWNER/REPO/ci.yml?branch=main&style=flat-square&label=CI&color=ACCENT)](https://github.com/OWNER/REPO/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/OWNER/REPO?style=flat-square&color=ACCENT)](https://github.com/OWNER/REPO/releases/latest)
[![Last commit](https://img.shields.io/github/last-commit/OWNER/REPO?style=flat-square&color=ACCENT)](https://github.com/OWNER/REPO/commits)
[![License](https://img.shields.io/badge/license-LICENSE-ACCENT?style=flat-square)](LICENSE)
[![Stars](https://img.shields.io/github/stars/OWNER/REPO?style=flat-square&color=ACCENT)](https://github.com/OWNER/REPO/stargazers)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/platform-Android%20MINSDK%2B-3DDC84?style=flat-square&logo=android&logoColor=white)](#-install)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=flat-square)](https://developer.android.com/jetpack/compose)

[⚡ Features](#-features) • [📥 Install](#-install) • [📸 Screenshots](#-screenshots) • [🧩 How it works](#-how-it-works) • [🛠 Build](#-build) • [📜 License](#-license)

<sub>OPTIONAL CONTEXT LINE — org, affiliation, thesis, funding.</sub>

</div>

---

> [!NOTE]
> STATUS IN ONE SENTENCE — prototype / shipped app / library — and the scope
> limit that stops the wrong expectations.

## ⚡ Features

<!-- Emoji-labelled left column, one dense sentence right. Scannable by the left column alone. -->

| | |
| --- | --- |
| **📥 FEATURE** | WHAT IT ACCEPTS AND WHAT IT DOES WITH IT. |
| **🧠 FEATURE** | THE INTERESTING MECHANISM, NOT THE MARKETING. |
| **♿ FEATURE** | ACCESSIBILITY, IF IT HAS A STORY — IT BELONGS HERE, NOT IN A FOOTNOTE. |

## 📥 Install

<!-- Android: store links first, then the sideload path. Delete what does not exist. -->

- 📦 **[Download the latest APK](https://github.com/OWNER/REPO/releases/latest/download/APP.apk)** — sideload on Android MINSDK+
- ▶️ **[Watch the demo](https://github.com/OWNER/REPO/releases/latest/download/demo.mp4)**

## 📸 Screenshots

<!-- Three real device captures: main screen, the distinguishing feature, settings/detail. -->

|  |  |  |
|:--:|:--:|:--:|
| ![DESCRIBE WHAT IS ON SCREEN](media/screenshots/01.png) | ![DESCRIBE WHAT IS ON SCREEN](media/screenshots/02.png) | ![DESCRIBE WHAT IS ON SCREEN](media/screenshots/03.png) |
| CAPTION | CAPTION | CAPTION |

## 🧩 How it works

<!-- One real user action, traced through the system. Theme the classDefs to the accent colour. -->

```mermaid
flowchart TD
    A["👆 USER ACTION"] --> B["⚙️ THE COMPONENT THAT HANDLES IT"]
    B --> C[("🗄️ WHERE STATE LIVES")]
    B --> D["✨ WHAT THE USER GETS BACK"]

    classDef step fill:#10151e,stroke:#ACCENT,stroke-width:1.5px,color:#e8edf4;
    classDef data fill:#0a1722,stroke:#4fb8e8,stroke-width:1.5px,color:#dff1ff;
    class A,B,D step;
    class C data;
```

<details>
<summary><b>Module layout</b></summary>

```
MODULE/     WHAT LIVES HERE
MODULE/     WHAT LIVES HERE
```

</details>

## 🛠 Build

> **Prerequisites:** LIST THEM — JDK, Android Studio version, SDK level.

```sh
./gradlew assembleDebug
./gradlew installDebug
```

<details>
<summary><b>Running the checks</b></summary>

```sh
./gradlew check          # format, static analysis, lint, unit tests
```

</details>

## 🧭 Roadmap

<!-- Only if there is a real one, tracked somewhere. Delete otherwise — an
     invented roadmap is a promise the repo did not make. -->

| Milestone | Theme | Status |
| --- | --- | --- |
| **M1** | THEME | ✅ `v0.1.0` |
| **M2** | THEME | 🚧 in progress |

## 📜 License

[![License](https://img.shields.io/badge/license-LICENSE-ACCENT?style=flat-square)](LICENSE)

LICENSE NAME — ONE LINE ON WHY, IF THE CHOICE WAS DELIBERATE.

<div align="center">
<br/>
<sub>AUTHOR · built with <a href="https://claude.com/claude-code">Claude Code</a>.</sub>
</div>
