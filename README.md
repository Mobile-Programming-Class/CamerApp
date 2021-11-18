# CamerApp
 Tugas Camera Kelas PPB A 2021

## SETUP DEPENDENCY

~/build.gradle
```
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    ext.kotlin_version = "1.3.72"
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:7.0.3"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30"
        classpath "com.google.gms:google-services:4.3.10"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```
~/app/build.gradle
```
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-android-extensions'
    id 'kotlin-kapt'
    id 'com.google.gms.google-services'
}

android {
    compileSdk 31

    defaultConfig {
        applicationId "com.camerax.app"
        minSdk 23
        targetSdk 31
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {

    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.5.31"
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.3.1'
    implementation 'com.google.android.material:material:1.4.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.1'

    implementation 'androidx.recyclerview:recyclerview:1.2.1'
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    implementation 'androidx.viewpager:viewpager:1.0.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'

    // firebase
    implementation 'com.google.firebase:firebase-storage-ktx:20.0.0'
    implementation platform('com.google.firebase:firebase-bom:29.0.0')
    implementation 'com.google.firebase:firebase-analytics-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx:24.0.0'

    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
}
```

# Struktur
In our implementation, here is the list of files that we create or change

## PROGRAM

- <a href="/app/src/main/java/com/camerax/app/activity/">activity</a>
    - <a href="/app/src/main/java/com/camerax/app/activity/ImageActivity.kt">ImageActivity</a>
    - <a href="/app/src/main/java/com/camerax/app/activity/ImageActivity.kt">MainActivity</a>
- <a href="/app/src/main/java/com/camerax/app/adapter/">adapter</a>
    - <a href="/app/src/main/java/com/camerax/app/adapter/GalleryImageAdapter.kt">GalleryImageAdapter</a>
    - <a href="/app/src/main/java/com/camerax/app/adapter/GalleryImageClickListener.kt">GalleryImageClickListener</a>
    - <a href="/app/src/main/java/com/camerax/app/adapter/Image.kt">Image</a>
    - <a href="/app/src/main/java/com/camerax/app/adapter/ImageManagerAdapter.kt">ImageManagerAdapter</a>
- <a href="/app/src/main/java/com/camerax/app/datatype/">datatype</a>
    - <a href="/app/src/main/java/com/camerax/app/datatype/UpdateDataType.kt">UpdateDataType</a>
- <a href="/app/src/main/java/com/camerax/app/fragment/">fragment</a>
    - <a href="/app/src/main/java/com/camerax/app/fragment/GalleryFullscreenFragment.kt">GalleryFullscreenFragment</a>
- <a href="/app/src/main/java/com/camerax/app/helper/">helper</a>
    - <a href="/app/src/main/java/com/camerax/app/helper/AppFIrebaseFirestore.kt">AppFIrebaseFirestore</a>
    - <a href="/app/src/main/java/com/camerax/app/helper/AppFIrebaseStorage.kt">AppFIrebaseStorage</a>
    - <a href="/app/src/main/java/com/camerax/app/helper/MyAppGlideModule.kt">MyAppGlideModule</a>
    - <a href="/app/src/main/java/com/camerax/app/helper/SquareLayout.kt">SquareLayout</a>
    - <a href="/app/src/main/java/com/camerax/app/helper/ZoomOutPageTransformer.kt">ZoomOutPageTransformer</a>

## LAYOUT

- drawable
    - btn_bg
    - btn_bg_green
    - btn_bg_red
    - row_bg
    - ic_cam
    - ic_delete
    - ic_gallery
    - ic_refresh
- layout
    - activity_image
    - activity_main
    - fragment_gallery_fullscreen
    - image_fullscreen
    - item_gallery_image
    - item_image
- menu
    - menu_main
- values
    - colors
    - strings
    - themes

## Manifest
<a href="/app/src/main/AndroidManifest.xml">AndroidManifest</a>

## google-services.json Firebase
 file tambahan ~/app/google-services.json digunakan untuk konfigurasi Firebase
