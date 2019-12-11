plugins {
    application
    kotlin("jvm")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src")
}

application {
    mainClassName = "me.galaran.swe.data.downloader.ItemDbDownloaderKt"
}

dependencies {
    compile(kotlin("stdlib"))
}
