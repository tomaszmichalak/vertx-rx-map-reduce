plugins {
    id("java-library")
}

repositories {
    jcenter()
}

dependencies {
    // This dependency is found on compile classpath of this component and consumers.

    implementation("io.reactivex.rxjava2:rxjava:2.2.6")
    implementation("io.vertx:vertx-core:3.6.2")
    implementation("com.google.guava:guava:26.0-jre")

    // Use JUnit test framework
    testImplementation("io.vertx:vertx-junit5:3.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.1.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.1.0")
}
