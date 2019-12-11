plugins {
    application
    kotlin("jvm")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src")
}

application {
    mainClassName = "me.galaran.swe.data.parser.ItemDbParserKt"
}

dependencies {
    compile(project(":swe-data"))

    compile(kotlin("stdlib"))
}
