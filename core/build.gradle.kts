plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "space.pixelsg.teapot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}

publishing {
    repositories {
        maven {
            name = "pixelsgRepositorySnapshots"
            url = uri("https://repo.pixelsg.space/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "space.pixelsg"
            artifactId = "teapot-core"
            version = "0.0.1"
            from(components["java"])
        }
    }
}