/*
 * Copyright (C) 2019 Tomasz Michalak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
