

plugins {
    id ("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin") // Use the appropriate version
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
}


android {
    namespace = "com.example.commerce"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.commerce"
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

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }





    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore-ktx:24.7.1")
    implementation("com.google.firebase:firebase-firestore:24.7.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Navigation component
    val nav_version = "2.7.1"
    //noinspection GradleDependency
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    //noinspection GradleDependency
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // Loading button
    implementation("br.com.simplepass:loading-button-android:2.2.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Circular image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // ViewPager2 indicator
    implementation("io.github.vejei.viewpagerindicator:viewpagerindicator:1.0.0-alpha.1")

    // StepView
    implementation("com.shuhart.stepview:stepview:1.5.1")

    // Android Ktx
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.2")

    // Dagger Hilt
//    implementation("com.google.dagger:hilt-android:2.48")
//    ksp("com.google.dagger:hilt-android-compiler:2.48")
    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")



    // Firebase
    implementation("com.google.firebase:firebase-auth:22.1.1")

    // Coroutines with Firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Firebase
    implementation("com.google.firebase:firebase-auth-ktx:22.1.1")
}

kapt {
    correctErrorTypes = true
}

