import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}
val appScriptUrl: String = localProperties.getProperty("APPS_SCRIPT_URL")
    ?: System.getenv("APPS_SCRIPT_URL")
    ?: ""

android {
    namespace = "com.infrabwx.app"
    compileSdk = 34

    signingConfigs {
        create("release") {
            storeFile = file("../infra-bwx-keystore.jks")
            storePassword = System.getenv("STORE_PASSWORD") ?: "infrabwx"
            keyAlias = "infra-bwx"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "infrabwx"
        }
    }

    defaultConfig {
        applicationId = "com.infrabwx.app"
        minSdk = 31
        targetSdk = 34
        versionCode = 3
        versionName = "1.1.1"
        buildConfigField("String", "APPS_SCRIPT_URL", "\"${appScriptUrl}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
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
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.navigation.compose)
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.datastore.preferences)
    implementation(libs.coil.compose)
    implementation(libs.play.services.location)
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    debugImplementation(libs.compose.ui.tooling)
}
