plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.blogapp"
    compileSdk = 35


    buildFeatures {
        buildConfig = true
        dataBinding = true
    }
    defaultConfig {
        applicationId = "com.example.blogapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        buildConfigField(
//            "String",
//            "CLOUDINARY_CLOUD_NAME",
//            "\"${project.property("CLOUDINARY_CLOUD_NAME")}\""
//        )
//        buildConfigField(
//            "String",
//            "CLOUDINARY_API_KEY", // <-- ADDED THIS
//            "\"${project.property("CLOUDINARY_API_KEY")}\""
//        )
//        buildConfigField(
//            "String",
//            "CLOUDINARY_API_SECRET",
//            "\"${project.property("CLOUDINARY_API_SECRET")}\""
//        )
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.cast.tv)
    implementation(libs.androidx.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.cloudinary:cloudinary-android:2.4.0")
}