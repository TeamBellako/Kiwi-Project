import org.gradle.kotlin.dsl.implementation

val mobileApiUrl: String = System.getenv("MOBILE_API_URL") ?: "http://10.0.2.2:8080"

fun getVersionCodeFromCI(): Int {
    val runNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1
    return runNumber
}

fun getVersionNameFromCI(): String {
    val sha = System.getenv("GITHUB_SHA")?.take(7) ?: "dev"
    return "0.0.$sha"
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services") version "4.4.2" apply false
}

android {
    namespace = "com.kiwi"
    compileSdk = 35

    packaging {
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE-notice.md")
        resources.excludes.add("META-INF/NOTICE")
    }

    defaultConfig {
        applicationId = "com.kiwi"
        minSdk = 24
        targetSdk = 35
        android.buildFeatures.buildConfig = true

        versionCode = getVersionCodeFromCI()
        versionName = getVersionNameFromCI()

        buildConfigField("String", "MOBILE_API_URL", "\"$mobileApiUrl\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }



    buildTypes {
        debug {
            buildConfigField("String", "MOBILE_API_URL", "\"$mobileApiUrl\"")
            buildConfigField(
                "boolean",
                "LOGGING_ENABLED",
                "true"
            )
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val ciVersionCode: String? by project
            val ciVersionName: String? by project

            defaultConfig {
                versionCode = ciVersionCode?.toIntOrNull() ?: 1
                versionName = ciVersionName ?: "0.1.0"
            }

        }
        release {
            buildConfigField("String", "MOBILE_API_URL", "\"$mobileApiUrl\"")
            buildConfigField(
                "boolean",
                "LOGGING_ENABLED",
                "false"
            )
            isMinifyEnabled = true
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
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

configurations { implementation.get().exclude(mapOf("group" to "org.jetbrains", "module" to "annotations"))}

dependencies {
    implementation(libs.firebase.bom)
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Compose UI
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx.v261)
    implementation(libs.androidx.activity.compose.v181)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    androidTestImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.mockito.junit.jupiter)
    androidTestImplementation(libs.junit.jupiter.api)
    androidTestImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.mockito.core.v380)
    androidTestImplementation(libs.mockito.android)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing.v210)

    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    implementation(libs.firebase.crashlytics)
    implementation(libs.google.firebase.analytics)
}