import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
    java
    `maven-publish`
    id("architectury-plugin")
    id("dev.architectury.loom")
    id("me.fallenbreath.yamlang")
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
}

val hollowcore: String by properties
val modId: String by properties
val modName: String by properties
val modVersion: String by properties
val license: String by properties

val container = ModProject(
    modId = modId,
    modName = modName,
    modVersion = modVersion,
    license = license,

    entryPoints = mapOf(),
    dependencies = mapOf(),

    username = "TheHollowHorizon"
)

val koolVersion: String by rootProject.properties
val kotlinVersion: String by properties

setupEnviroment(container, kotlinVersion, includeKotlin = false)

repositories {
    maven("https://jitpack.io")
    maven("https://maven.blamejared.com/")

    flatDir { dirs(rootProject.file("libs")) }
}

dependencies {

    install("ru.hollowhorizon:HollowCore-${stonecutter.modPlatform}-${stonecutter.minecraftVersion}:$hollowcore:dev", includeInJar = false, isMod = stonecutter.modPlatform == "forge")
    include("ru.hollowhorizon:HollowCore-${stonecutter.modPlatform}-${stonecutter.minecraftVersion}:$hollowcore")

    // CONFIG //
    install("com.akuleshov7:ktoml-core-jvm:0.5.1", false)

    // GRAPHICS //
    install("de.fabmax.kool:kool-core:$koolVersion", false)
    include("com.github.weisj:jsvg:2.0.0")
    install("com.facebook:ktfmt:0.54")
}

