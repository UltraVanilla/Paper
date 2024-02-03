plugins {
    java
    `maven-publish`

    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    // keep in sync with upstream (https://github.com/PaperMC/Paper/blob/master/build.gradle.kts)
    id("io.papermc.paperweight.patcher") version "1.5.11"
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

repositories {
    mavenCentral()
    maven(paperMavenPublicUrl) {
        content { onlyForConfigurations(configurations.paperclip.name) }
    }
}

dependencies {
    // keep in sync with upstream (https://github.com/PaperMC/Paper/blob/master/build.gradle.kts)
    remapper("net.fabricmc:tiny-remapper:0.8.10:fat")
    // keep in sync with upstream (https://github.com/PaperMC/Paper/blob/master/build.gradle.kts)
    decompiler("net.minecraftforge:forgeflower:2.0.627.2")
    // keep in sync with upstream (https://github.com/PaperMC/Paper/blob/master/build.gradle.kts)
    paperclip("io.papermc:paperclip:3.0.3")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 17
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }
}

paperweight {
    serverProject = project(":josiepaper-server")

    remapRepo = paperMavenPublicUrl
    decompileRepo = paperMavenPublicUrl

    usePaperUpstream(providers.gradleProperty("paperRef")) {
        withPaperPatcher {
            apiPatchDir = layout.projectDirectory.dir("patches/api")
            apiOutputDir = layout.projectDirectory.dir("josiepaper-api")

            serverPatchDir = layout.projectDirectory.dir("patches/server")
            serverOutputDir = layout.projectDirectory.dir("josiepaper-server")
        }
    }
}
