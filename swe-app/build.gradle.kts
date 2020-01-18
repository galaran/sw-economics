import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src")
}

sourceSets["main"].resources.srcDir("resources")

application {
    mainClassName = "me.galaran.swe.SweApplicationKt"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
}

dependencies {
    compile(project(":swe-data"))

    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))

    compile("net.sourceforge.tess4j:tess4j:4.4.1")

    implementation("net.java.dev.jna:jna:5.5.0")
    implementation("net.java.dev.jna:jna-platform:5.5.0")
}
