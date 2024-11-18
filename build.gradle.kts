plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("maven-publish")
}

group = "com.tripleauth"
version = "0.2.2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.named("jar").get())
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.redisson:redisson:3.38.1")
    implementation("io.github.oshai:kotlin-logging:6.0.8")
    implementation("io.netty:netty-common:4.1.115.Final")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.named<Jar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveBaseName.set("hypriority")
    archiveVersion.set(version.toString())
    archiveClassifier.set("")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
