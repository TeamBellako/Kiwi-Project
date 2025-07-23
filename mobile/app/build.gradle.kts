import org.gradle.kotlin.dsl.implementation

val mobileApiUrl: String = System.getenv("MOBILE_API_URL") ?: "http://10.0.2.2:8080"
val companyEmail: String = System.getenv("MOBILE_COMPANY_EMAIL") ?: "simon@petrikov.com"

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services") version "4.4.2" apply false

    kotlin("kapt")
    id("dagger.hilt.android.plugin")

}

android {
    namespace = "com.bellako.kiwi"
    testNamespace = "com.bellako.kiwi.test"
    compileSdk = 35

    packaging {
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE-notice.md")
        resources.excludes.add("META-INF/NOTICE")
    }

    defaultConfig {
        applicationId = "com.bellako.kiwi"
        minSdk = 25
        targetSdk = 35
        android.buildFeatures.buildConfig = true

        versionCode = (project.findProperty("versionCode") as String?)?.toInt() ?: 1
        versionName = project.findProperty("versionName") as String? ?: "0.1-dev"

        buildConfigField("String", "MOBILE_API_URL", "\"$mobileApiUrl\"")
        buildConfigField("String", "MOBILE_COMPANY_EMAIL", "\"$companyEmail\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "MOBILE_API_URL", "\"$mobileApiUrl\"")
            buildConfigField("String", "MOBILE_COMPANY_EMAIL", "\"$companyEmail\"")
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
        }
        release {
            buildConfigField("String", "MOBILE_API_URL", "\"$mobileApiUrl\"")
            buildConfigField("String", "MOBILE_COMPANY_EMAIL", "\"$companyEmail\"")
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
           isDebuggable = false
           isJniDebuggable = false
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

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}


dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(platform(libs.firebase.bom.v3400))
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.tink.android)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Compose UI
    implementation(libs.ui)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.material.icons.extended)

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
    implementation(libs.moshi)
    kapt(libs.moshi.kotlin.codegen)
    implementation(libs.converter.moshi)

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

    implementation(libs.coil)
    implementation(libs.coil.gif)
    implementation(libs.coil.compose)

    implementation(libs.firebase.crashlytics)
    implementation(libs.google.firebase.analytics)
    testImplementation(kotlin("test"))

    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.hilt.android)
    implementation(libs.androidx.foundation.v105)
    ksp(libs.hilt.compiler)
    androidTestImplementation(libs.androidx.foundation)

    testImplementation(libs.kotlinx.coroutines.test.v173)
    testImplementation(libs.mockito.core.v5170)
    testImplementation(libs.mockito.kotlin)

    implementation(libs.androidx.hilt.navigation.compose)

    androidTestImplementation(libs.mockwebserver)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
}
