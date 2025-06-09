import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android") version "2.46"
    id("kotlin-kapt")
}

android {
    namespace = "com.testcityapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.testcityapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn"
        )
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7" // Compatible with Kotlin 1.8.21
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    configurations.all {
        resolutionStrategy {
            // Force specific versions to resolve conflicts
            force("com.squareup:javapoet:1.13.0")
            force("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:1.8.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.21")
        }
    }

}

// Configure kapt for Hilt and Room
kapt {
    correctErrorTypes = true
    javacOptions {
        option("-Xmaxerrs", 500)
        option("-source", "17")
        option("-target", "17")
    }
    arguments {
        arg("kapt.verbose", "true")
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
    }
}

dependencies {
    // AndroidX Core Libraries
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    
    // Room - use direct version references
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")
    
    // Hilt - use fixed versions known to work together
    implementation("com.google.dagger:hilt-android:2.46")
    kapt("com.google.dagger:hilt-android-compiler:2.46")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    
    // Compose - use fixed version
    implementation(platform("androidx.compose:compose-bom:2023.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.5.3")
    
    // Maps
    implementation("com.google.maps.android:maps-compose:2.8.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    
    // Lifecycle - use versions compatible with the rest of the stack
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    // lifecycle-runtime-compose not available in 2.5.1, use 2.6.0-alpha01 or remove
    // implementation("androidx.lifecycle:lifecycle-runtime-compose:2.5.1")
    implementation("androidx.lifecycle:lifecycle-process:2.5.1")
    
    // WorkManager - use version compatible with Hilt 2.44
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    
    // Fix for JavaPoet issue
    implementation("com.squareup:javapoet:1.13.0")
    
    // Fix for kotlinx-metadata compatibility - use version compatible with Kotlin 1.8.21
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.0")
    
    // Retrofit
    implementation(libs.square.retrofit)

    // Gson
    implementation(libs.google.code.gson)

    // Retrofit with Gson Converter
    implementation(libs.square.retrofit.converter.gson)
    
    // Google Maps
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Testing
    testImplementation(libs.junit)
    testImplementation("org.robolectric:robolectric:4.10.2")
    testImplementation("io.mockk:mockk:1.12.5")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

