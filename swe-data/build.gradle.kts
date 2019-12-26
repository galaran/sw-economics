plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src")
}

sourceSets["main"].resources.srcDir("resources")

dependencies {
    compile(kotlin("stdlib"))

    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
}
