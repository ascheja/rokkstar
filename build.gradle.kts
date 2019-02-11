import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.20"
    application
}

allprojects {
    group = "net.ascheja.rokkstar"
    version = "1.0-SNAPSHOT"

    repositories {
        jcenter()
    }
}

application {
    mainClassName = "net.ascheja.rokkstar.RokkstarKt"
}

val startScripts: CreateStartScripts by tasks
startScripts.applicationName = "rokkstar"

val ktorVersion = "1.1.1"

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("io.ktor:ktor-server-netty:$ktorVersion")
    compile("io.ktor:ktor-jackson:$ktorVersion")
    compile("ch.qos.logback:logback-classic:1.2.3")
    compile(project(":interpreter"))
    compile(project(":parser"))
    testCompile("junit:junit:4.12")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
