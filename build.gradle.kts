plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.xjc)
}

repositories {
    mavenLocal()
    mavenCentral()
}
sourceSets {
    create("generated") {
        java {
            srcDir("build/generated/sources/xjc/java/main")
        }
    }
}

group = "nl.info.lib-sepa"
description = "SEPA clien library"

val javaVersion = JavaVersion.VERSION_21

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.jaxb.api)
    implementation(libs.jaxb.core)
    implementation(libs.jaxb.runtime)
    implementation(libs.oss.iban)
}

detekt {
    toolVersion = libs.versions.detekt.get()
}

tasks.named<JavaCompile>("compileGeneratedJava") {
    dependsOn("xjcGenerate")
    source = sourceSets["generated"].java
    classpath = sourceSets["main"].compileClasspath
    destinationDirectory.set(file("build/classes/generated"))
}


tasks.named("compileKotlin") {
    dependsOn(tasks.named("compileGeneratedJava"))
}

// Place the schema file into the JAR
tasks.named<Jar>("jar") {
    from("src/main/schema") {
        include("*.xsd")
        into("schema")
    }
}
