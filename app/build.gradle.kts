plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.activmitsu_can"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.example.activmitsu_can"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        debug {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-rules-debug.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

val compose_ui_version: String by project
val coroutineVer = "1.6.4"
val navigationVer = "2.5.3"
val lifecycleVer = "2.5.1"
val daggerVer = "2.45"

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.ui:ui:$compose_ui_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_ui_version")
    implementation("androidx.compose.material:material:1.3.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.github.mik3y:usb-serial-for-android:3.4.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVer")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVer")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVer")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVer")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVer")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVer")
    implementation("io.github.unitbean:androidcore:2.7.0")
    implementation("com.google.dagger:dagger:$daggerVer")
    implementation("com.google.dagger:dagger-android:$daggerVer")
    implementation("androidx.core:core-ktx:1.9.0")
    kapt("com.google.dagger:dagger-compiler:$daggerVer")
    kapt("com.google.dagger:dagger-android-processor:$daggerVer")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_ui_version")
}