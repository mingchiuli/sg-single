
repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":sg-single-common"))
    implementation(project(":sg-single-blog"))
}

tasks.bootJar{
    enabled = false
}

tasks.jar{
    enabled = true
}

