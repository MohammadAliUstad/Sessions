<div align="center">
  <img src="app/src/main/ic_launcher-playstore.png" alt="Sessions Logo" width="120" height="120" style="border-radius: 20px;">

  <h1 style="font-size: 30px; margin-bottom: 0;">Sessions — Your Focus, Reinvented</h1>

  <p>
    <strong>A Jetpack Compose Pomodoro timer with cloud-synced history, <br>
    Google Auth, and Crashlytics stability monitoring.</strong>
  </p>

  <div align="center">
    <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Jetpack%20Compose-M3-4285F4?style=for-the-badge&logo=android&logoColor=white" alt="Jetpack Compose" />
    <img src="https://img.shields.io/badge/Firebase-Auth%20%26%20Crashlytics-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase" />
    <img src="https://img.shields.io/badge/Koin-DI-FF6F00?style=for-the-badge&logo=koin&logoColor=white" alt="Koin" />
  </div>
</div>

<br />

## ✨ Features

- **🍅 Pomodoro Timer**: Customizable focus sessions (25m default) with Pause, Stop, Short Break, and Long Break presets.
- **📊 Session Tracking**: Cloud-synced logging of completed/partial sessions via **Cloud Firestore**.
- **🔐 Robust Auth**: Seamless sign-in supporting **Google Sign-In** and Email/Password (Firebase Auth).
- **🛡️ Crash Monitoring**: Integrated **Firebase Crashlytics** and **Timber** logging for real-world stability.
- **🎨 Modern UI**: Fully native Android UI built with **Jetpack Compose** and **Material 3**.
- **🏗️ Clean Architecture**: Built using **MVVM**, Repository Pattern, and **Koin** dependency injection.

---

## 📸 Screenshots

<table align="center" style="border: none;">
  <tr>
    <td align="center" width="33%">
      <img src="Screenshots/screenshot (1).jpg" alt="Home Screen" width="100%" style="border-radius: 10px;" />
      <br />
      <sub><b>Focus Timer</b></sub>
    </td>
    <td align="center" width="33%">
      <img src="Screenshots/screenshot (2).jpg" alt="Session History" width="100%" style="border-radius: 10px;" />
      <br />
      <sub><b>Session History</b></sub>
    </td>
    <td align="center" width="33%">
      <img src="Screenshots/screenshot (3).jpg" alt="Settings" width="100%" style="border-radius: 10px;" />
      <br />
      <sub><b>Settings</b></sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <img src="Screenshots/screenshot (4).jpg" alt="Login" width="100%" style="border-radius: 10px;" />
      <br />
      <sub><b>Login Screen</b></sub>
    </td>
    <td align="center" width="33%">
      <img src="Screenshots/screenshot (5).jpg" alt="Profile" width="100%" style="border-radius: 10px;" />
      <br />
      <sub><b>Profile Stats</b></sub>
    </td>
    <td align="center" width="33%">
      <img src="Screenshots/screenshot (6).jpg" alt="Dark Mode" width="100%" style="border-radius: 10px;" />
      <br />
      <sub><b>Dark Mode</b></sub>
    </td>
  </tr>
</table>

---

## 🚀 Technologies Used

| Category | Tech Stack |
|:--- | :--- |
| **Language** | Kotlin (100%) |
| **UI Toolkit** | Jetpack Compose, Material 3 |
| **Architecture** | MVVM, Clean Architecture, Repository Pattern |
| **DI** | Koin (Lightweight Dependency Injection) |
| **Backend** | Firebase Auth, Cloud Firestore |
| **Local Data** | Room Database, DataStore Preferences |
| **Monitoring** | Firebase Crashlytics, Timber |
| **Build** | Gradle Kotlin DSL (KTS), Version Catalogs (`libs.versions.toml`) |

---

## 🛠️ Setup & Installation

1. **Clone the repository**
   ```bash
   git clone [https://github.com/YOUR_USERNAME/Sessions.git](https://github.com/YOUR_USERNAME/Sessions.git)
