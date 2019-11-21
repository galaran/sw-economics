plugins {
    base
    kotlin("jvm") version "1.3.60" apply false
}

allprojects {
    group = "me.galaran.swe"
    version = "0.1-SNAPSHOT"

    repositories {
        jcenter()
    }
}

dependencies {
    // Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }
}
