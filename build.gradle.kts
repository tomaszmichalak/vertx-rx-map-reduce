import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java-library")
}

repositories {
    jcenter()
}

dependencies {
    // This dependency is found on compile classpath of this component and consumers.

    implementation("io.reactivex.rxjava2:rxjava:2.2.6")
    implementation("io.vertx:vertx-core:3.6.3")
    implementation("com.google.guava:guava:26.0-jre")
    implementation("io.vertx:vertx-circuit-breaker:3.6.3")


    // Use JUnit test framework
    testImplementation("io.vertx:vertx-junit5:3.6.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.3.2")
}

tasks.withType<Test>().configureEach {
    failFast = true
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.SHORT
    }
}