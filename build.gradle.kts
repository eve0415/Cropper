import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"

    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"

    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "net.eve0415"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    paperweight {
        paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    }

    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.20.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.20.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            apiVersion.set(KotlinVersion.KOTLIN_2_1)
            languageVersion.set(KotlinVersion.KOTLIN_2_1)
        }
    }

    compileTestKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            apiVersion.set(KotlinVersion.KOTLIN_2_1)
            languageVersion.set(KotlinVersion.KOTLIN_2_1)
        }
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    processResources {
        inputs.property("version", project.version)
        from(sourceSets.main.get().resources.srcDirs) {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            include("plugin.yml")
            expand("version" to project.version)
        }
    }

    build {
        dependsOn(reobfJar)
    }

    runServer {
        minecraftVersion("1.21")
    }
}
