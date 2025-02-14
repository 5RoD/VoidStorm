plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"


    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://reposilite.worldseed.online/public")

    }


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("net.minestom:minestom-snapshots:32735340d7")
    implementation("ca.atlasengine:atlas-projectiles:2.1.1")
    implementation("net.worldseed.particleemitter:ParticleEmitter:1.4.1")



}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23)) // Minestom has a minimum Java version of 21
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "org.rod.Main" // Change this to your main class
        }
    }

    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix on the shadowjar file.
    }
}