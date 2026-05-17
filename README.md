# ⚡ Grama-Urja — Crowdsourced Power Monitor for Rural Areas

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-blue?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Database-Firebase-yellow?style=for-the-badge&logo=firebase" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-purple?style=for-the-badge" />
</p>

<p align="center">
  <b>Power in Your Hand, Bright Future for Our Land.</b>
</p>

---

## 📋 Table of Contents

- [Problem Statement](#problem-statement)
- [Solution](#solution)
- [App Screenshots Flow](#app-screenshots-flow)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Firebase Setup](#firebase-setup)
- [How to Run](#how-to-run)
- [Success Criteria](#success-criteria)
- [Future Improvements](#future-improvements)
- [Built For](#built-for)

---

## 🚨 Problem Statement

In rural India, **power cuts are frequent and unpredictable**. Farmers depend on electricity to run their irrigation pumps. Without knowing if power is available, they walk 1–2 km to their fields — only to find the electricity is still off. This wastes:

- ⏰ Hours of productive time every day
- ⛽ Fuel for vehicles to reach the field
- 💧 Irrigation opportunities when power is briefly available

There is no simple, reliable way for rural farmers to know the **real-time power status** of their transformer zone.

---

## ✅ Solution

**Grama-Urja** is a crowdsourced Android app where farmers in the same transformer zone help each other by reporting power status with a single tap.

```
Farmer sees power is ON
        ↓
Taps "POWER ON" in the app
        ↓
Firebase updates instantly
        ↓
ALL farmers in that zone see the update within 2 seconds
        ↓
Farmer at home knows power is ON — starts pump without going to field
```

It is a simple, **human-powered Smart Grid** for rural communities.

---

## 📱 App Screenshots Flow

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Splash    │    │    Name     │    │    Zone     │    │    Home     │    │Confirmation │
│   Screen    │ →  │   Entry     │ →  │  Selection  │ →  │   Screen    │ →  │   Screen    │
│             │    │             │    │             │    │  (ON/OFF)   │    │             │
│  Grama-Urja │    │ Enter your  │    │ Hunsur Zone │    │ ⚡ POWER ON │    │ ✅ Updated! │
│    Logo     │    │   name...   │    │ Gundlupet   │    │ 🔴 POWER OFF│    │             │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
    Screen 1           Screen 2           Screen 3           Screen 4           Screen 5
```

---

## ✨ Features

### Core Features
| Feature | Description |
|---------|-------------|
| 📍 **Zone Selection** | Dynamic zones loaded from Firebase — no app update needed |
| ⚡ **Real-time Power Status** | ON/OFF status syncs across all devices in under 2 seconds |
| 👆 **One-tap Updates** | Single tap to report POWER ON or POWER OFF |
| 🕐 **Last Seen Timestamp** | Shows exactly when status was last updated (e.g., "3 mins ago") |
| 👤 **Updated By** | Shows which farmer made the last update — builds community trust |
| 🟢 **Freshness Indicator** | Fresh / Recent / Old / Stale — shows data reliability |
| 🔔 **Push Notifications** | FCM notifications when power status changes in selected zone |
| 👨‍🌾 **Farmer Registration** | Simple name entry on first launch — no password needed |

### Bonus Feature
| Feature | Description |
|---------|-------------|
| ⏱️ **Pump Timer Calculator** | Calculate exact pump runtime based on crop type and land size |

---

## 🛠️ Tech Stack

```
┌─────────────────────────────────────────────────────────────┐
│                      GRAMA-URJA APP                         │
├─────────────────────────────────────────────────────────────┤
│  UI Layer          │  Jetpack Compose + Material 3          │
│  Language          │  Kotlin                                │
│  Architecture      │  MVVM (Model-View-ViewModel)           │
│  State Management  │  Kotlin StateFlow + collectAsState     │
│  Navigation        │  Custom sealed class navigation        │
│  Animations        │  Compose Animation APIs                │
├─────────────────────────────────────────────────────────────┤
│  Real-time Sync    │  Firebase Realtime Database            │
│  Notifications     │  Firebase Cloud Messaging (FCM)        │
│  Local Storage     │  SharedPreferences                     │
├─────────────────────────────────────────────────────────────┤
│  IDE               │  Android Studio                        │
│  Build System      │  Gradle with Version Catalog (TOML)    │
│  Min SDK           │  API 24 (Android 7.0)                  │
│  Target SDK        │  API 35 (Android 15)                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🏗️ Architecture

This app follows **MVVM (Model-View-ViewModel)** architecture:

```
┌─────────────────────────────────────────────┐
│                   VIEW                      │
│  HomeScreen  ZoneSelection  PumpTimer  etc  │
│         (Jetpack Compose Screens)           │
└──────────────────┬──────────────────────────┘
                   │ observes StateFlow
┌──────────────────▼──────────────────────────┐
│               VIEWMODEL                     │
│          GramaUrjaViewModel                 │
│   Holds state, handles user actions         │
└──────────────────┬──────────────────────────┘
                   │ calls functions
┌──────────────────▼──────────────────────────┐
│                 MODEL                       │
│  PowerRepository   UserPreferences          │
│  Firebase reads/writes  SharedPreferences   │
└─────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
app/src/main/java/com/gramaurja/
│
├── MainActivity.kt                    ← App entry + screen navigation
│
├── data/
│   ├── PowerRepository.kt             ← Firebase DB reads/writes + StateFlow
│   ├── UserPreferences.kt             ← SharedPreferences (farmer name)
│   ├── NotificationHelper.kt          ← FCM topic subscription + local notifications
│   └── GramaUrjaMessagingService.kt   ← FCM background message handler
│
├── viewmodel/
│   └── GramaUrjaViewModel.kt          ← State management + business logic
│
└── ui/
    ├── theme/
    │   └── Theme.kt                   ← Colors: GramaGreen, GramaRed, GramaYellow
    │
    └── screens/
        ├── SplashScreen.kt            ← Animated splash with logo
        ├── NameEntryScreen.kt         ← Farmer name registration
        ├── ZoneSelectionScreen.kt     ← Dropdown + list of zones
        ├── HomeScreen.kt              ← Power status card + ON/OFF buttons
        ├── UpdateConfirmationScreen.kt← Success screen after update
        └── PumpTimerSheet.kt          ← Irrigation pump calculator
```

---

## 🔥 Firebase Setup

### Database Structure
```json
{
  "zones_list": {
    "zone_a": {
      "name": "Hanumanthapura",
      "transformer": "Zone A – Hanumanthapura"
    },
    "zone_b": {
      "name": "Angondahalli",
      "transformer": "Zone B – Angondahalli"
    }
  },
  "zones": {
    "zone_a": {
      "status": {
        "isOn": true,
        "timestamp": 1234567890000,
        "updatedBy": "Ravi Kumar"
      }
    }
  }
}
```

### Database Rules (Testing)
```json
{
  "rules": {
    "zones": {
      ".read": true,
      ".write": true
    },
    "zones_list": {
      ".read": true,
      ".write": false
    }
  }
}
```

---

## 🚀 How to Run

### Prerequisites
- Android Studio (latest version)
- Android device or emulator (API 24+)
- Firebase account (free)

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/YourUsername/GramaUrja.git
cd GramaUrja
```

**2. Set up Firebase**
- Go to [Firebase Console](https://console.firebase.google.com/)
- Create a new project named `GramaUrja`
- Add an Android app with package name `com.gramaurja`
- Download `google-services.json`
- Place it in the `app/` folder

**3. Add your database URL**

Open `app/src/main/java/com/gramaurja/data/PowerRepository.kt` and replace:
```kotlin
FirebaseDatabase.getInstance("YOUR_DATABASE_URL")
```
with your actual Firebase database URL.

**4. Add zones to Firebase**

In Firebase Console → Realtime Database → Import this JSON:
```json
{
  "zones_list": {
    "zone_a": { "name": "Your Village", "transformer": "Zone A" }
  }
}
```

**5. Build and run**
```bash
# Open in Android Studio → Click Run ▶
```

---

## ✅ Success Criteria

| Criteria | Status |
|----------|--------|
| Status updates visible to all users within 2 seconds | ✅ Done |
| "Last Seen" freshness shown (e.g., "2 mins ago (10:30 AM)") | ✅ Done |
| High-contrast UI, readable outdoors | ✅ Done |
| One-tap Power ON / OFF updates | ✅ Done |
| Zone selection with dropdown + list | ✅ Done |
| Confirmation screen after every update | ✅ Done |
| Pump Timer tool based on crop type | ✅ Done |
| Push notifications when power changes | ✅ Done |
| Farmer name shown on every update | ✅ Done |
| Dynamic zones from Firebase (no app update needed) | ✅ Done |

---

## 🚀 Future Improvements

1. **Multi-language Support** — Kannada, Hindi, Tamil, Telugu for rural farmers
2. **Firebase Phone OTP Authentication** — verify farmer identity with mobile number
3. **Power History Graph** — 24-hour chart of when power was ON/OFF in a zone
4. **Cloud Functions** — server-side notification triggers using Firebase Cloud Functions
5. **Gemini AI Assistant** — farming chatbot to answer crop and irrigation questions
6. **Offline Mode** — show cached status when no internet, sync when connected
7. **Electricity Board API** — integrate official planned outage schedules
8. **SMS Fallback** — send SMS notifications for farmers with basic phones
9. **Upvote/Downvote** — community verification of power status reports

---

## 🏆 Impact

| Benefit | Description |
|---------|-------------|
| ⏰ **Saves Time** | Farmers skip the 1-2 km walk to check power status |
| ⛽ **Saves Fuel** | No vehicle trips to check field power status |
| 💧 **Efficient Irrigation** | Pump timer prevents over/under watering |
| 🤝 **Community Intelligence** | Farmers help each other through shared data |
| 📱 **Simple to Use** | Large buttons, high contrast, works on basic Android phones |

---

## 👨‍💻 Built For

**MindMatrix VTU Internship Program — Project #60**

> *Android App Development using GenAI — Grama-Urja (Energy)*

---

## 📄 License

This project is built for educational purposes as part of the MindMatrix VTU Internship Program.

---

<p align="center">
  Made with ❤️ for Indian Farmers
</p>
