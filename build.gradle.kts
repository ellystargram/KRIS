plugins {
    kotlin("jvm") version "2.1.21"
}

group = "com.cgos"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.seleniumhq.selenium:selenium-java:4.34.0")
    implementation("org.junit.jupiter:junit-jupiter-engine:5.13.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {attributes["Main-Class"] = "MainKt"}
    from(configurations.runtimeClasspath.get().map { if(it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}