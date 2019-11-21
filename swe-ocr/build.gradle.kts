plugins {
    kotlin("jvm")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src")
}

dependencies {
    compile(kotlin("stdlib"))
    compile("net.sourceforge.tess4j:tess4j:4.4.1")
}
