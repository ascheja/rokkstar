plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(project(":ast"))
    compile(project(":typesystem"))
    testCompile("junit:junit:4.12")
}
