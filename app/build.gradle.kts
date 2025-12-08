import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
    id("com.gorylenko.gradle-git-properties") version "2.4.2"
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.yugentech.sessions"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yugentech.sessions"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val webClientId = localProperties.getProperty("WEB_CLIENT_ID") ?: ""
        resValue("string", "web_client_id", webClientId)
    }

    sourceSets {
        getByName("main") {
            assets.srcDir(layout.buildDirectory.dir("generated/assets/gitProperties"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        @Suppress("DEPRECATION")
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// This generates the file into the folder registered above
gitProperties {
    gitPropertiesResourceDir = layout.buildDirectory.dir("generated/assets/gitProperties")
    dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ"
    dateFormatTimeZone = "UTC"
}

dependencies {

    // --- 1. LOCAL UNIT TESTS (Logic) ---
    // Runs fast on your computer (JVM). Used for ViewModels and Repositories.
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // --- 2. ANDROID UI TESTS (Visuals) ---
    // Runs on the Emulator/Device. Used for Screens and Buttons.
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.5") // Check your compose version
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    // REQUIRED: This allows the test to "see" the UI tree
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.5")

    // Crashlytics and Analytics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // Timber
    implementation(libs.timber)

    // Material
    implementation(libs.google.material)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.runtime)

    // Firebase BOM
    implementation(platform(libs.firebase.bom))

    // Firebase Auth
    implementation(libs.firebase.auth.ktx)

    // Firestore
    implementation(libs.firebase.firestore.ktx)

    // Realtime DB
    implementation(libs.firebase.database.ktx)

    // Jetpack Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // SplashScreen
    implementation(libs.androidx.core.splashscreen)

    // Extended Icons
    implementation(libs.androidx.material.icons.extended)

    // Coil
    implementation(libs.coil.compose)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Accompanist
    implementation(libs.accompanist.navigation.animation)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.compose.material3.window.size.class1)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

val generateGitProperties by tasks.existing

tasks.named("preBuild") {
    dependsOn(generateGitProperties)
}