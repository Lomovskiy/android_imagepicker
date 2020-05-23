buildscript {

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.5.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
        classpath("com.novoda:bintray-release:0.9.2")
    }

}

allprojects {

    repositories {
        google()
        jcenter()
    }

}
