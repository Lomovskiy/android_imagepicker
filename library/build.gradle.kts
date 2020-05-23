import com.novoda.gradle.release.PublishExtension
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.novoda.bintray-release")
}

configure<PublishExtension> {

    val properties: Properties = Properties()

    val propertiesFile: File = project.rootProject.file("local.properties")

    if (propertiesFile.exists()) {
        properties.load(propertiesFile.inputStream())
    }

    val bintray_user: String = properties.getProperty("bintray_user")
    val bintray_key: String = properties.getProperty("bintray_key")

    bintrayUser = bintray_user
    bintrayKey = bintray_key
    userOrg = bintray_user
    repoName = "android_libs"
    groupId = "com.lomovskiy.android.library"
    artifactId = "image-picker"
    publishVersion = "1.0.0"
    desc = "Oh hi, this is a nice description for a project, right?"
    website = "https://github.com/Lomovskiy/android_imagepicker"
    dryRun = false

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
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")
    implementation("androidx.exifinterface:exifinterface:1.1.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.2.0")
}
