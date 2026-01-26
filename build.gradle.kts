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

xjc {
    xsdDir.set(layout.projectDirectory.dir("src/main/schema"))
    useJakarta.set(false)
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
            url = URI("https://maven.pkg.github.com/infonl/lib-sepa")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

val javaVersion = JavaVersion.VERSION_18

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

// Place the schema file into the JAR
tasks.named<Jar>("jar") {
    from("src/main/schema") {
        include("*.xsd")
        into("schema")
    }
}
