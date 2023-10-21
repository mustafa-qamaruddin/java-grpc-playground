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
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":proto"))

    runtimeOnly("io.grpc:grpc-netty-shaded:1.58.0")
    implementation("io.grpc:grpc-protobuf:1.58.0")
    implementation("io.grpc:grpc-stub:1.58.0")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53") // necessary for Java 9+
}

tasks.test {
    useJUnitPlatform()
}
