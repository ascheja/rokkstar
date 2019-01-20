plugins {
    kotlin("jvm")
}

val ktor_version = "1.1.1"

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("io.ktor:ktor-jackson:$ktor_version")
    compile(project(":interpreter"))
    compile(project(":parser"))
    testCompile("junit:junit:4.12")
}
