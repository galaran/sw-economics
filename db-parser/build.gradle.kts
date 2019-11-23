plugins {
    application
    kotlin("jvm")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src")
}

application {
    mainClassName = "me.galaran.swe.data.ItemDbDownloaderKt"
}

dependencies {
    compile(kotlin("stdlib"))
}
