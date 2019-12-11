plugins {
    base
    kotlin("jvm") version "1.3.61" apply false
}

allprojects {
    group = "me.galaran.swe"
    version = "0.1-SNAPSHOT"

    repositories {
        jcenter()
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "12"
        }
    }
}

dependencies {
    // Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }
}
