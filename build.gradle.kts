val ktlint: Configuration by configurations.creating

plugins {
    kotlin("jvm") version "1.7.20"
}

group = "net.eve0415"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.6.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    ktlint("com.pinterest:ktlint:0.47.1")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        from(sourceSets.main.get().resources.srcDirs) {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            include("plugin.yml")
            expand("version" to project.version)
        }
    }

    check {
        dependsOn("ktlintCheck")
    }

    register<JavaExec>("ktlintCheck") {
        inputs.files(project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt")))
        outputs.dir("${project.buildDir}/reports/ktlint")

        group = "verification"
        description = "Check Kotlin code style."
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        args = listOf(
            "src/**/*.kt",
            "--reporter=plain",
            "--reporter=checkstyle,output=$buildDir/reports/ktlint/checkstyle-report.xml"
        )
    }

    register<JavaExec>("ktlintFormat") {
        inputs.files(project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt")))
        outputs.dir("${project.buildDir}/reports/ktlint")

        group = "formatting"
        description = "Fix Kotlin code style deviations."
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        args = listOf("-F", "src/**/*.kt")
        jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    }
}
