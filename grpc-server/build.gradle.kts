plugins {
    id("idea")
    id("java")
}

group = "org.qubits"

repositories {
    mavenLocal() // For testing new releases of gRPC Kotlin
    mavenCentral()
    google()
}

dependencies {
    implementation("junit:junit:4.13.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.grpc:grpc-testing:1.59.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:junit-jupiter:2.20.0")

    implementation(project(":proto"))

    implementation("io.grpc:grpc-protobuf:1.58.0")
    implementation("io.grpc:grpc-stub:1.58.0")
    implementation("io.grpc:grpc-alts:1.59.0")

    compileOnly("org.apache.tomcat:annotations-api:6.0.53") // necessary for Java 9+
    runtimeOnly("io.grpc:grpc-netty-shaded:1.58.0")

}

tasks.test {
    useJUnitPlatform()
}
