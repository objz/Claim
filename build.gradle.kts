plugins {
    java
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
    compileOnly("dev.jorel:commandapi-paper-core:11.1.0")
    implementation("net.kyori:adventure-text-minimessage:4.25.0")
}

