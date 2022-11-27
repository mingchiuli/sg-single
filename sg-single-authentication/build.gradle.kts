
repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":sg-single-common"))
}

tasks.bootJar{
    enabled = false
}

tasks.jar{
    enabled = true
}

