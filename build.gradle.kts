plugins {
    kotlin("jvm") version "1.3.72"
    application
}

application {
    mainClassName = "cc.ekblad.kotline.KotlineKt"
}

group = "cc.ekblad"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
