plugins {

    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "vn.edu.tlu.haiyen.sayo"
    compileSdk = 35

    defaultConfig {
        applicationId = "vn.edu.tlu.haiyen.sayo"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.google.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.material)
    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)
    implementation("com.google.firebase:firebase-storage")
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation("de.hdodenhof:circleimageview:3.1.0")
}