# Sessions - Your Personal Focus Companion

**Sessions** is a precision-engineered productivity application designed to help you master your time and maintain flow state. By combining a distraction-free timer with immersive audio environments and detailed analytics, Sessions provides a reliable ecosystem for deep work.

[![Get it on Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](YOUR_PLAY_STORE_LINK_HERE)

[**Download Latest Release**](https://github.com/MohammadAliUstad/Sessions/releases) | [**Report Bug**](https://github.com/MohammadAliUstad/Sessions/issues) | [**Request Feature**](https://github.com/MohammadAliUstad/Sessions/issues)

---

## Overview

Sessions transforms the concept of a simple timer into a comprehensive focus tool. Whether you are studying, coding, or writing, the application ensures your environment is optimized for concentration. It features a robust background service that prevents the operating system from killing the timer, ensuring your progress is tracked even when your phone is locked.

---

## Key Features

### Intelligent Focus Engine
* **Customizable Cycles:** Define your exact Focus Duration, Break Duration, and Repetition count.
* **Smart Intervals:** The application automatically calculates when to trigger a Long Break based on your completed sets.
* **Task History:** Assign names to specific sessions to recognize and review what you worked on later.

### Immersive Audio Environment
* **Curated Ambience:** Includes 5 high-quality background sounds: Rain, Brown Noise, Fireplace, Library, and Riverside.
* **Adaptive Audio Ducking:** Background volume intelligently lowers during breaks and rises during focus sessions to subconsciously guide your attention.
* **Sensory Feedback:** Integrated haptic feedback and sound effects confirm interactions without needing visual confirmation.

### Reliability & System Integration
* **Persistent Notification:** A live notification on the lock screen allows you to track progress without unlocking the device.
* **Background Stability:** Engineered to resist aggressive battery optimization, ensuring the timer never stops unexpectedly.

### Analytics & Personalization
* **Visual Insights:** A dedicated dashboard featuring heatmaps and metrics (Total Focus Time, Peak Productivity Hours).
* **Deep Theming:** 8 Color Themes (including Dynamic Material You), OLED Black Mode, and 6 Font options.
* **Identity System:** Choose from a variety of avatars and set a custom display name.

---

## User Interface Gallery

<table>
  <tr>
    <td align="center">
      <img src="Screenshots/Timer.png" alt="Timer" width="200"/>
      <br />
      <b>Timer (Locked In)</b>
    </td>
    <td align="center">
      <img src="Screenshots/Insights.png" alt="Insights" width="200"/>
      <br />
      <b>Insights & Heatmap</b>
    </td>
    <td align="center">
      <img src="Screenshots/Dashboard.png" alt="Dashboard" width="200"/>
      <br />
      <b>Daily Dashboard</b>
    </td>
    <td align="center">
      <img src="Screenshots/Settings.png" alt="Settings" width="200"/>
      <br />
      <b>Settings & Preferences</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="Screenshots/Appearance.png" alt="Appearance" width="200"/>
      <br />
      <b>Appearance & Theming</b>
    </td>
    <td align="center">
      <img src="Screenshots/EditProfile.png" alt="Edit Profile" width="200"/>
      <br />
      <b>Edit Profile</b>
    </td>
    <td align="center">
      <img src="Screenshots/Credits.png" alt="Credits" width="200"/>
      <br />
      <b>Credits</b>
    </td>
    <td align="center">
      <img src="Screenshots/About.png" alt="About" width="200"/>
      <br />
      <b>About Screen</b>
    </td>
  </tr>
</table>

---

## Technical Architecture

Sessions is built using modern Android development standards, ensuring a codebase that is scalable, testable, and maintainable.

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3 Design System)
* **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture principles
* **Dependency Injection:** Koin
* **Local Database:** Room Database
* **Backend Services:** Firebase (Auth, Firestore)
* **Concurrency:** Kotlin Coroutines & Flow

---

## Setup & Installation

To run this project locally for development or contribution purposes:

1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/MohammadAliUstad/Sessions.git](https://github.com/MohammadAliUstad/Sessions.git)
    cd sessions
    ```

2.  **Firebase Configuration**
    * Create a project in the Firebase Console.
    * Download the `google-services.json` file.
    * Place the file in the `app/` directory of the project.

3.  **Build**
    * Open the project in Android Studio.
    * Sync Gradle files.
    * Select your target device/emulator and click Run.

---

## Contact & Support

If you encounter any issues or have suggestions for future updates, please open an issue on GitHub or contact the developer directly.

**Email:** Mohammadaliustad@gmail.com

---

**Developed by Yugen Tech**
