plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")
    implementation("androidx.exifinterface:exifinterface:1.1.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.2.0")
}

//publishing {
//
//}

//bintray {
//    user = 'lomovskiy'
//    key = 'cd4a4fbc927e57d1251a993927ba176dc8e96215'
//    configurations = ['archives']
//    pkg {
//        repo = 'android'
//        name = 'image-picker'
//        licenses = ['Apache-2.0']
//        vcsUrl = 'https://github.com/Lomovskiy/android_imagepicker'
//        userOrg = 'lomovskiy'
//        version {
//            name = '1.0.0'
//            released  = new Date()
//        }
//    }
//
//}

//bintray {
//
//    Properties properties = new Properties()
//    def propertiesFile = project.rootProject.file('local.properties')
//    if (propertiesFile.exists()) {
//        properties.load(propertiesFile.newDataInputStream())
//    }
//
//    user = properties.getProperty("bintray_user")
//    key = properties.getProperty("bintray_key")
//    configurations = ['archives']
//    pkg {
//        repo = 'android'
//        name = 'image-picker'
//        userOrg = properties.getProperty("bintray_user")
//        vcsUrl = 'https://github.com/Lomovskiy/android_imagepicker'
//        licenses = ["Apache-2.0"]
//
//        version {
//            name = "0.2"
//            released = new Date()
//        }
//    }
//
//}

//publish {
//
//    Properties properties = new Properties()
//    def propertiesFile = project.rootProject.file('local.properties')
//    if (propertiesFile.exists()) {
//        properties.load(propertiesFile.newDataInputStream())
//    }
//
//    def bintray_user = properties.getProperty("bintray_user")
//    bintrayUser = "lomovskiy"
//    bintrayKey = "cd4a4fbc927e57d1251a993927ba176dc8e96215"
//    userOrg = "lomovskiy"
//    repoName = "android"
//    groupId = "com.lomovskiy.android.library"
//    artifactId = 'image-picker'
//    publishVersion = '0.2'
//    uploadName = "image-picker"
//    desc = "Image picker for Android"
//    autoPublish = true
//    website = "https://github.com/Lomovskiy/android_imagepicker"
//    repository = "${website}.git"
//    licences = ["Apache-2.0"]
//    issueTracker = "${website}/issues"
//
//}
