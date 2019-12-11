plugins {
    kotlin("jvm")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src")
}

sourceSets["main"].resources.srcDir("resources")

dependencies {
    compile(kotlin("stdlib"))
}
