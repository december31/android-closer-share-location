@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.appdistribution")
    id("com.google.firebase.firebase-perf")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.harian.software.closer.share.location"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.harian.software.closer.share.location"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        archivesName =
            "CloserShareLocation_v${versionName}(${versionCode})_${
                SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.US
                ).format(Date())
            }"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        flavorDimensions += versionName!!
    }

    signingConfigs {
        create("release") {
            storeFile = file("key/harian_release.jks")
            storePassword = "An30122002"
            keyPassword = "An30122002"
            keyAlias = "release"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true
            manifestPlaceholders["firebasePerformanceLogcatEnabled"] = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
            manifestPlaceholders["firebasePerformanceLogcatEnabled"] = true
        }
    }

    productFlavors {
        create("appDev") {
            buildConfigField("String", "API_BASE_URL", "\"https://solely-pleased-wallaby.ngrok-free.app/closer/\"")
            buildConfigField("String", "WEB_SOCKET_END_POINT", "\"wss://solely-pleased-wallaby.ngrok-free.app/closer/websocket/v1\"")
        }

        create("appProduct") {
            buildConfigField("String", "API_BASE_URL", "\"https://solely-pleased-wallaby.ngrok-free.app/closer/\"")
            buildConfigField("String", "WEB_SOCKET_END_POINT", "\"wss://solely-pleased-wallaby.ngrok-free.app/closer/websocket/v1\"")
        }
    }

    secrets {
        propertiesFileName = "secrets.properties"
        defaultPropertiesFileName = "local.defaults.properties"
        ignoreList.add("keyToIgnore")
        ignoreList.add("sdk.*")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")


    // firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-perf")

    implementation("com.airbnb.android:lottie:6.4.0")

    implementation("androidx.datastore:datastore-preferences-core:1.0.0")

    // dagger hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    kapt("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.0")

    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // http
    val retrofitVersion = "2.9.0"
    val okhttpLoggingVersion = "4.12.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpLoggingVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    implementation("com.facebook.shimmer:shimmer:0.5.0")

    runtimeOnly("com.google.android.material:material:1.11.0")

    // stomp client using krossbow (flow)
    implementation("org.hildan.krossbow:krossbow-stomp-core:5.12.0")
    implementation("org.hildan.krossbow:krossbow-websocket-ktor:5.12.0")
    implementation("org.hildan.krossbow:krossbow-websocket-builtin:5.12.0")
    implementation("org.hildan.krossbow:krossbow-websocket-okhttp:5.12.0")

    // stomp client using StompProtocolAndroid (rxAndroid)
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
}
