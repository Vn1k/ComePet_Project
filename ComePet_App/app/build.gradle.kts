plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}
val placesApiKey = if (project.hasProperty("PLACES_API_KEY")) project.property("PLACES_API_KEY") as String else ""

android {
    namespace = "com.example.comepet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.comepet"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "PLACES_API_KEY", "\"${placesApiKey}\"")
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
        buildConfig = true
    }
    secrets {
        // To add your Maps API key to this project:
        // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
        // 2. Add this line, where YOUR_API_KEY is your API key:
        //        MAPS_API_KEY=YOUR_API_KEY
        propertiesFileName = "secrets.properties"

        // A properties file containing default secret values. This file can be
        // checked in version control.
        defaultPropertiesFileName = "local.defaults.properties"

        // Configure which keys should be ignored by the plugin by providing regular expressions.
        // "sdk.dir" is ignored by default.
        ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
        ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx.v190)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx.v262)
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v262)
    implementation(libs.androidx.navigation.fragment.ktx.v275)
    implementation(libs.androidx.navigation.ui.ktx.v275)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.core.splashscreen)

    //google
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.firestore)
    implementation(platform(libs.firebase.bom.v3351))
    implementation (libs.com.google.firebase.firebase.analytics3)
    implementation (libs.play.services.auth)
    implementation (libs.com.google.firebase.firebase.firestore)
    implementation(libs.com.google.firebase.firebase.analytics2)
    implementation (libs.com.google.firebase.firebase.auth)
    implementation("com.google.firebase:firebase-storage-ktx:20.2.0")

    // glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    //map
    implementation(libs.play.services.maps)
    implementation (libs.play.services.maps.v1810)
    implementation (libs.play.services.location)
    implementation (libs.places)

    // image cropper
    implementation("com.vanniktech:android-image-cropper:4.6.0")

    implementation ("androidx.appcompat:appcompat:1.6.1")

    implementation (libs.play.services.maps.v1700)
    implementation(platform(libs.kotlin.bom))
    implementation(libs.places.v350)


}