plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(project(":ast"))
    testCompile("junit:junit:4.12")
}
