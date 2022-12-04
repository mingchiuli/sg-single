plugins {
    java
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.0"
//    id("org.graalvm.buildtools.native") version "0.9.18"
}
group = "com.chiu"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":sg-single-common"))
    implementation(project(":sg-single-blog"))
    implementation(project(":sg-single-blog-admin"))
    implementation(project(":sg-single-search"))
    implementation(project(":sg-single-websocket"))
    implementation(project(":sg-single-authentication"))
    implementation(project(":sg-single-captcha"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
//    apply(plugin = "org.graalvm.buildtools.native")

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        implementation("com.github.axet:kaptcha:0.0.9")
        implementation("org.springframework.boot:spring-boot-starter-aop")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
        implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
        implementation("io.jsonwebtoken:jjwt-api:0.11.5")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-amqp")
        implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
        implementation("org.springframework.boot:spring-boot-starter-websocket")
        runtimeOnly("org.springframework.boot:spring-boot-devtools")
        runtimeOnly("mysql:mysql-connector-java:8.0.30")
    }

}
