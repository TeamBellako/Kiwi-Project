val mobileApiUrl: String = System.getenv("MOBILE_API_URL") ?: "http://10.0.2.2:8080"
val companyEmail: String = System.getenv("MOBILE_COMPANY_EMAIL") ?: "simon@petrikov.com"

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")

    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
}

ktlint {
    version.set("1.7.1")
    android.set(true)
    outputColorName.set("RED")
}

detekt {
    toolVersion = "1.23.8"
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "22" // detekt only supports up to 22
    }
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
                "true",
            )
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        release {
            buildConfigField("String", "MOBILE_API_URL", "\"$mobileApiUrl\"")
            buildConfigField("String", "MOBILE_COMPANY_EMAIL", "\"$companyEmail\"")
            buildConfigField(
                "boolean",
                "LOGGING_ENABLED",
                "false",
            )
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            isDebuggable = false
            isJniDebuggable = false
            isShrinkResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

configurations { implementation.get().exclude(mapOf("group" to "org.jetbrains", "module" to "annotations")) }

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)

    // Compose and Material
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.foundation)
    androidTestImplementation(libs.androidx.foundation)
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.ui)
    implementation(libs.androidx.ui.graphics)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    androidTestImplementation(libs.androidx.navigation.testing)

    // Room and Datastore
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.androidx.datastore.preferences)

    // Lifecycle
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)

    // Hilt/Dagger
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Network and Parsing
    implementation(libs.converter.gson)
    implementation(libs.converter.moshi)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    // Firebase
    implementation(libs.firebase.analytics)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.google.firebase.analytics)

    // Media
    implementation(libs.androidx.media3.exoplayer)

    // Annotations
    compileOnly(libs.annotations)

    // Security
    implementation(libs.tink.android)

    // Testing
    testImplementation(kotlin("test"))
    androidTestImplementation(libs.androidx.core)
    implementation(libs.core.ktx)
    androidTestImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.ui.test.manifest)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.jupiter.api)
    androidTestImplementation(libs.junit.jupiter.engine)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.mockito.android)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    androidTestImplementation(libs.mockito.junit.jupiter)
    androidTestImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.mockwebserver)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
    testImplementation(libs.robolectric)
}
