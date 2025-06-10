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

## Prerequisites

- **JDK 17** (required)
- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 35
- Gradle 8.5

## Required Software Versions

```
Java Development Kit (JDK):         17
Kotlin:                             1.9.22
Compose Compiler:                   1.5.8
Android Gradle Plugin:              8.2.2
Gradle:                             8.5
Room:                               2.6.1
Hilt:                               2.48.1
Jetpack Compose:                    2023.10.01 (BOM)
Android Minimum SDK:                26
Android Target SDK:                 35
```

## Getting Started

1. Clone the repository:
   ```shell
   git clone https://github.com/yourusername/cityApp.git
   cd cityApp
   ```

2. Configure Google Maps API key:
   - Add the following to your `local.properties` file:
     ```
     MAPS_API_KEY=your_google_maps_api_key_here
     ```

3. Build and run:
   ```shell
   ./gradlew build
   ```
   
   Or open the project in Android Studio and run from there.

## Project Structure

- `app/src/main/java/com/testcityapp/`
  - `data/`: Repository implementations, local database, entities
  - `domain/`: Business logic, models, repository interfaces
  - `presentation/`: UI components, ViewModels
  - `di/`: Dependency injection modules

## Testing

The project includes unit tests for:
- CityEmissionProducer
- CityRepository
- MainViewModel

Run the tests using:
```shell
./gradlew test
```

Run a specific test:
```shell
./gradlew testDebugUnitTest --tests "com.testcityapp.data.repository.CityRepositoryImplTest"
```

## Troubleshooting

If you encounter build errors:

1. Make sure JDK 17 is installed and configured in Android Studio
2. Verify Gradle settings match the required versions
3. Run with `--stacktrace` flag for detailed error information
   ```shell
   ./gradlew build --stacktrace
   ```

## License

[Add your license information here]
