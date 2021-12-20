import java.nio.file.Paths
import kotlin.io.path.inputStream

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("com.github.ben-manes.versions") version "0.39.0"
    `maven-publish`
}

group = "cc.ekblad"
version = "0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
}

ktlint {
    version.set("0.43.2")
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

tasks {
    val dependencyUpdateSentinel = register<DependencyUpdateSentinel>("dependencyUpdateSentinel") {
        dependsOn(dependencyUpdates)
    }

    check {
        dependsOn(test)
        dependsOn(ktlintCheck)
        dependsOn(dependencyUpdateSentinel)
    }

    listOf(compileJava, compileTestJava).map { task ->
        task {
            sourceCompatibility = "1.8"
            targetCompatibility = "1.8"
        }
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    build {
        dependsOn(javadocJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("kotline") {
            groupId = "cc.ekblad"
            artifactId = "kotline"
            version = project.version.toString()
            from(components["kotlin"])
            artifact(javadocJar)
            artifact(tasks.kotlinSourcesJar)
            pom {
                name.set("kotline")
                description.set("Minimalist cross-platform readline-like library.")
                url.set("https://github.com/valderman/kotline")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/valderman/kotline/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("valderman")
                        name.set("Anton Ekblad")
                        email.set("anton@ekblad.cc")
                    }
                }
            }
        }
    }
}

abstract class DependencyUpdateSentinel : DefaultTask() {
    @kotlin.io.path.ExperimentalPathApi
    @org.gradle.api.tasks.TaskAction
    fun check() {
        val updateIndicator = "The following dependencies have later milestone versions:"
        Paths.get("build", "dependencyUpdates", "report.txt").inputStream().bufferedReader().use { reader ->
            if (reader.lines().anyMatch { it == updateIndicator }) {
                throw GradleException("Dependency updates are available.")
            }
        }
    }
}
