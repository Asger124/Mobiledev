plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "dk.itu.moapd.copenhagenbuzz.asjo"
    compileSdk = 35

    defaultConfig {
        applicationId = "dk.itu.moapd.copenhagenbuzz.asjo"
        minSdk = 27
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.javafaker)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.ui.database)
    implementation(libs.dotenv.kotlin)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}