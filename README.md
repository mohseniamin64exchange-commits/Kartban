# Kartban

Kartban is a native Android application for storing and managing bank card information and associated persons.

## Main Technologies

- **Kotlin**
- **Jetpack Compose**
- **Room Database**
- **ViewModel + StateFlow**
- **Repository Pattern**
- **Coroutines**

## Project Structure

The main Android source code is located under:

`app/src/main/java/com/example/`

Key directories and files:

### data/
- `AppDatabase.kt`
- `BankCardEntity.kt`
- `PersonEntity.kt`
- `PersonWithCards.kt`
- `KartYarDao.kt`
- `KartYarRepository.kt`
- `IranianBankHelper.kt`

### ui/
- `KartYarViewModel.kt`
- `MainScreen.kt`
- `components/`
- `screens/`

## Current Features

- Add persons, stores, and companies
- Store multiple bank cards for each person
- Store card number, account number, and IBAN
- Store additional information for personal cards
- Iranian bank detection/display
- Search persons and banking information
- Copy card information
- Share card information
- RTL/Persian UI support
- Light and dark mode
- Local storage using Room

## Running the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/mohseniamin64exchange-commits/Kartban.git
   cd Kartban
   ```
2. Open it with **Android Studio**.
3. Let Gradle sync.
4. Select an Android device or emulator.
5. Run the `app` module.

## Android Configuration

- **minSdk**: 24
- **targetSdk**: 36
- **compileSdk**: 36

## Database

Room is used with `PersonEntity` and `BankCardEntity`, with a one-to-many relation where each person can have multiple cards.

## Project Status

The project is still under development and is not yet ready for production release.

The following areas still need review before release:
- secure storage of sensitive banking information
- Room migrations
- backup configuration
- release configuration
- removal of sample/debug data

## Google AI Studio

- This project is **NOT** Flutter.
- The main application is native Android using **Kotlin** and **Jetpack Compose**.
- Any Google AI Studio related files are only for design/prototyping/development assistance.
- The Android `app` module is the source of truth.
