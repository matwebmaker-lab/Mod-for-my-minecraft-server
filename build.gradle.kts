import java.net.URL

plugins {
    java
}

group = "no.lager"
version = "1.0.0"

val runDir = file("run")

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to version))
    }
}

tasks.jar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

// Kopier plugin til run/plugins før start
tasks.register<Copy>("copyPluginToServer") {
    dependsOn(tasks.jar)
    from(tasks.jar.get().archiveFile)
    into(file("run/plugins"))
    doFirst {
        file("run/plugins").mkdirs()
    }
}

// Last ned Paper-server (kjør én gang: gradlew setupServer)
tasks.register("setupServer") {
    doLast {
        val runDir = file("run")
        runDir.mkdirs()
        val version = "1.21.11"
        val build = "69"
        val jarName = "paper-$version-$build.jar"
        val paperJar = runDir.resolve(jarName)
        if (!paperJar.exists()) {
            println("Laster ned Paper $version build $build...")
            URL("https://api.papermc.io/v2/projects/paper/versions/$version/builds/$build/downloads/paper-$version-$build.jar")
                .openStream().use { input ->
                    paperJar.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            println("Lastet ned: $paperJar")
        }
        file("run/eula.txt").writeText("eula=true\n")
        println("EULA akseptert. Kjør: gradlew runServer")
    }
}

// Start Paper-serveren (bygger plugin og kopierer til run/plugins)
tasks.register<Exec>("runServer") {
    dependsOn("build", "copyPluginToServer")
    group = "application"
    description = "Starter Paper-serveren med Lager-plugin"
    doFirst {
        runDir.mkdirs()
        if (!runDir.resolve("eula.txt").exists()) {
            runDir.resolve("eula.txt").writeText("eula=true\n")
        }
        val jars = runDir.listFiles()?.filter { it.name.startsWith("paper-") && it.name.endsWith(".jar") } ?: emptyList()
        val jar = jars.firstOrNull { it.name.contains("1.21.11") } ?: jars.firstOrNull()
            ?: throw GradleException("Mangler Paper JAR. Kjør: gradlew setupServer (for 1.21.11)")
        commandLine(
            "java", "-Xms1G", "-Xmx2G", "-jar", jar.name
        )
    }
    workingDir(runDir)
    standardInput = System.`in`
    isIgnoreExitValue = true
}
