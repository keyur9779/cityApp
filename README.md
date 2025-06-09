# Random City App

A Jetpack Compose Android application that generates random city-color combinations every 5 seconds while the app is in the foreground.

## Features

- **Splash Screen**: Displays for 1 second before navigating to the main screen.
- **Master-Detail View**: Shows a sorted list of emitted cities with their corresponding emission timestamps.
- **Responsive Layout**: Supports side-by-side view in tablet landscape mode.
- **City Details**: Features a map centered on the selected city.
- **Welcome Toast**: Shows "Welcome to [city name]" 5 seconds after selecting a city using WorkManager.
- **Offline Support**: Stores all emissions in a Room database for persistence.

## Architecture

The app follows Clean Architecture principles and MVVM pattern:

- **Data Layer**: Contains Room database, entities, and repositories
- **Domain Layer**: Contains business logic, use cases, and repository interfaces
- **Presentation Layer**: Contains UI components, ViewModels, and state management

## Technologies

- Jetpack Compose for UI
- Hilt for dependency injection
- Room for database storage
- WorkManager for background tasks
- Google Maps for location display
- Kotlin Coroutines and Flow for asynchronous operations

## Getting Started

1. Clone the repository
2. Add your Google Maps API key in the AndroidManifest.xml
3. Build and run the project using Android Studio

## Testing

The project includes unit tests for:
- CityEmissionProducer
- CityRepository
- MainViewModel

Run the tests using:
```
./gradlew test
```
