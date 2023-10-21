import com.google.protobuf.gradle.*

plugins {
    id("idea")
    id("java")
    id("com.google.protobuf") version("0.9.4")
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
    runtimeOnly("io.grpc:grpc-netty-shaded:1.58.0")
    implementation("io.grpc:grpc-protobuf:1.58.0")
    implementation("io.grpc:grpc-stub:1.58.0")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53") // necessary for Java 9+
}

tasks.test {
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.22.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.58.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                 id("grpc") { option("lite") }
            }
        }
    }
}