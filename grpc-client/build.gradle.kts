plugins {
    id("java")
}

group = "org.qubits"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":proto"))

    runtimeOnly("io.grpc:grpc-netty-shaded:1.58.0")
    implementation("io.grpc:grpc-protobuf:1.58.0")
    implementation("io.grpc:grpc-stub:1.58.0")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53") // necessary for Java 9+
    implementation("io.grpc:grpc-alts:1.59.0")

}

tasks.test {
    useJUnitPlatform()
}