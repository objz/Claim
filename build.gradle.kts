plugins {
    java
    id("com.gradleup.shadow") version "9.3.0"
}

group = "dev.objz"
version = "1.0.0"

java {
    toolchain.languageVersion. set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.bluecolored.de/releases")
}

dependencies {
    
    compileOnly("dev.folia:folia-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    compileOnly("de.bluecolored:bluemap-api:2.7.7")
    
    implementation("dev.jorel:commandapi-paper-shade:11.0.0")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("dev.jorel.commandapi", "dev.objz.libs.commandapi")
    }
    
    assemble {
        dependsOn(shadowJar)
    }
    
}
