plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.me.payment"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.me.payment"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation( "com.stripe:stripe-android:20.35.0")
    implementation ("com.android.volley:volley:1.2.0")
    //20.35.0->'stripe_version' => '2022-08-01',
    //17.2.0->'stripe_version' => '2020-08-27',
    //20.48.6->'stripe_version' => '2024-06-20',not work
    //20.48.0>'stripe_version' => '22024-05-20',
}