import groovy.lang.Closure
import net.fabricmc.loom.api.remapping.RemapperExtension
import net.fabricmc.loom.api.remapping.RemapperParameters
import net.fabricmc.loom.extension.LoomGradleExtensionImpl
import net.fabricmc.loom.extension.RemapperExtensionHolder
import net.fabricmc.tinyremapper.TinyRemapper
import java.util.*

plugins {
    java
    `maven-publish`
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
    id("me.fallenbreath.yamlang") version "1.3.1"
    kotlin("jvm")
    kotlin("plugin.serialization")
}

val kotlinVersion = fromProperties("kotlinVersion")
val userConfig = Properties()
val cfg = rootProject.file("user.properties")
if (cfg.exists()) userConfig.load(cfg.inputStream())

val modId = fromProperties("mod_id")
val minecraftVersion = stonecutter.current.project.substringBeforeLast('-')
val modPlatform = stonecutter.current.project.substringAfterLast('-')
val license = fromProperties("license")
val modName = fromProperties("mod_name")
val modVersion = fromProperties("mod_version")


loom {
    silentMojangMappingsLicense()
    val awFile = rootProject.file("src/main/resources/$modId.accesswidener")
    if (awFile.exists()) accessWidenerPath = awFile

    mixin.useLegacyMixinAp = true
    mixin.add(sourceSets.main.get(), "$modId.refmap.json")

    when (modPlatform) {
        "forge" -> forge {
            convertAccessWideners = true
            mixinConfig("$modId.mixins.json")
            (this@loom as LoomGradleExtensionImpl).remapperExtensions.add(ForgeFixer)
        }
    }

    runConfigs.all {
        if(environment == "client") programArgs("--username=test")
        runDir("../../run")
    }
}

architectury {
    minecraft = minecraftVersion
    platformSetupLoomIde()
    common(modPlatform)
    when (modPlatform) {
        "forge" -> forge()
    }
}

group = fromProperties("mod_group")
version = modVersion

base {
    archivesName = "$modName-$modPlatform-$minecraftVersion"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://maven.0mods.team/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.parchmentmc.org")
    maven("https://maven.blamejared.com")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://jitpack.io")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.cleanroommc.com")
    maven("https://cursemaven.com")
    flatDir { dirs(rootDir.resolve("libs")) }
}

configurations.configureEach {
    resolutionStrategy {
        force("net.sf.jopt-simple:jopt-simple:5.0.4")
        force("org.ow2.asm:asm-commons:9.5")
    }
}

dependencies {
    setupLoader(modPlatform, minecraftVersion)

    compileOnly("org.spongepowered:mixin:0.8.7")

    // KOTLIN //

    dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    dependency("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    dependency("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    dependency("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
    dependency("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // CONFIG //
    dependency("com.akuleshov7:ktoml-core-jvm:0.5.1")

    // OTHER
    implementation("org.ow2.asm:asm:9.7")
    implementation("org.ow2.asm:asm-tree:9.7")
    implementation("org.anarres:jcpp:1.4.14")
    implementation("io.github.douira:glsl-transformer:2.0.1")

}

afterEvaluate {
    stonecutter {
        val platform = loom.platform.get().id()
        stonecutter.const("forge", platform == "forge")
    }
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(
        tasks.remapJar.get().archiveFile
    )
    into(rootProject.layout.buildDirectory.file("../merged"))

    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }

    rootProject.tasks.register("runActive") {
        group = "project"
        dependsOn(tasks.named("runClient"))
    }
}

stonecutter {
    val j21 = eval(minecraftVersion, ">=1.20.5")
    java {
        withSourcesJar()
        sourceCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
        targetCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17

        toolchain {
            languageVersion = JavaLanguageVersion.of(if (j21) 21 else 17)
        }
    }

    kotlin {
        jvmToolchain(if (j21) 21 else 17)
    }

    arrayOf("gltf", "glb", "bin", "ttf", "so", "dll", "dylib", "ser", "efkefc", "obj", "mtl")
        .forEach { stonecutter.exclude("*.$it") }
}

tasks.processResources {
    from(project.sourceSets.main.get().resources)
    when (modPlatform) {
        "forge" -> exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
    }

    val excl = if (modPlatform == "fabric") "tsrg" else "tiny"

    val resFile = rootProject.file("src/main/resources")
    if (resFile.isDirectory) {
        resFile.listFiles()?.forEach {
            if (it.name.contains("mappings")) {
                val splittedName = it.name.split('/').last()

                // Check current minecraft version
                if (!splittedName.split('-')[1].contains(minecraftVersion)) exclude(splittedName)

                // Check environment
                if (splittedName.endsWith(excl)) exclude(splittedName)
            }
        }
    }

    exclude("architectury.common.marker")

    filesMatching(
        listOf(
            "META-INF/mods.toml",
            "fabric.mod.json",
            "META-INF/neoforge.mods.toml",
            "$modId.mixins.json"
        )
    ) {
        expand(
            mapOf(
                "mod_version" to modVersion,
                "mod_id" to modId,
                "mod_name" to modName,
                "license" to license,
                "mc_version" to minecraftVersion
            )
        )
    }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/${modId}/lang")
}

fun DependencyHandlerScope.includes(vararg libraries: String) {
    for (library in libraries) {
        include(library)
    }
}

fun fromProperties(id: String) = rootProject.properties[id].toString()


class KClosure<T : Any?>(val function: T.() -> Unit) : Closure<T>(null, null) {
    fun doCall(it: T): T {
        function(it)
        return it
    }
}

fun <T : Any> closure(function: T.() -> Unit): Closure<T> {
    return KClosure(function)
}

object ForgeFixer : RemapperExtensionHolder(object : RemapperParameters {}) {
    override fun getRemapperExtensionClass(): Property<Class<out RemapperExtension<*>>> {
        throw UnsupportedOperationException("How did you call this method?")
    }

    override fun apply(
        tinyRemapperBuilder: TinyRemapper.Builder,
        sourceNamespace: String,
        targetNamespace: String,
        objectFactory: ObjectFactory,
    ) {
        // For some strange reason there are errors with source name mapping, but that doesn't stop me from compiling the jar, does it?
        tinyRemapperBuilder.ignoreConflicts(true)
    }
}

fun DependencyHandlerScope.dependency(path: String) {
    val dependency = implementation(path) {
        exclude("org.jetbrains.kotlin")
        exclude("org.ow2.asm")
        exclude("net.sourceforge.jaad.aac")
        exclude("org.slf4j")
        exclude("commons-logging")
    }

    dependency.takeIf { modPlatform == "forge" || modPlatform == "neoforge" }?.let {
        "forgeRuntimeLibrary"(it)
    }
    include(dependency)
}

fun DependencyHandlerScope.minecraft(version: String) = "minecraft"("com.mojang:minecraft:$version")

@Suppress("UnstableApiUsage")
fun setupMappings(version: String): Dependency = loom.layered {
    officialMojangMappings()
    val mappingsVer = when (version) {
        "1.21" -> "2024.07.28"
        "1.20.1" -> "2023.09.03"
        "1.19.2" -> "2022.11.27"
        else -> throw IllegalStateException("Unknown mappings for version $version!")
    }
    parchment("org.parchmentmc.data:parchment-$version:$mappingsVer")
}

fun DependencyHandlerScope.setupLoader(loader: String, version: String) {
    minecraft(version)
    mappings(setupMappings(version))

    when (loader) {

        "forge" -> {
            when (version) {
                "1.21" -> "forge"("net.minecraftforge:forge:$version-51.0.8")
                "1.20.1" -> {
                    "forge"("net.minecraftforge:forge:$version-47.3.22")
                    modImplementation("mods:oculus-mc1.20.1:1.7.0")
                    modImplementation("mods:embeddium:0.3.31+mc1.20.1")
                }
                "1.19.2" -> {
                    dependency("org.joml:joml:1.10.8")
                    "forge"("net.minecraftforge:forge:$version-43.4.12")
                }

                else -> throw IllegalStateException("Unsupported $loader version $version!")
            }

        }

    }
}