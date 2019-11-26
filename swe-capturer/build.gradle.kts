plugins {
    application
    kotlin("jvm")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src")
}

sourceSets["main"].resources.srcDir("resources")

application {
    mainClassName = "me.galaran.swe.capture.ScreenCapturer"
}

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))
    compile(project(":swe-ocr"))
    compile(project(":swe-overlay"))
}
