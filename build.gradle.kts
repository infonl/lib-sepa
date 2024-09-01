/*
 * SPDX-FileCopyrightText: 2024 INFO
 * SPDX-License-Identifier: EUPL-1.2+
*/
import java.net.URI

plugins {
    java
    `maven-publish`
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

group = "nl.amsterdam.stadsbank"
description = "SEPA client library for Stadsbank van Lening Amsterdam"

publishing {
    publications {
        create<MavenPublication>("Jar") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHub"
            url = URI("https://maven.pkg.github.com/infonl/stadsbank")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

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
