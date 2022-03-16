import com.google.protobuf.gradle.*
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.springframework.boot.gradle.plugin.SpringBootPlugin

/*
 * This file was generated by the Gradle 'init' task.
 */



group = "com.sms.courier"
version = "1.3"
description = "courier-backend"

val grpcVersion by extra("1.42.1")
val protocVersion by extra("3.19.1")
val mapstructSpiGrpcVersion by extra("1.19")
val versionLombok by extra("1.18.20")
val versionMapstruct by extra("1.4.2.Final")
val versionJjwt by extra("0.11.2")
val caffeine by extra("3.0.0")
val versionMustache by extra("compiler:0.9.10")
val versionJsonPath by extra("2.4.0")
val versionEasyPoiBase by extra("4.4.0")

plugins {
    val springVersion = "2.5.10"
    java
    checkstyle
    jacoco
    idea
    id("com.github.spotbugs") version "4.7.1"
    id("org.springframework.boot") version springVersion
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.google.protobuf") version "0.8.18"
    id ("org.cyclonedx.bom") version "1.5.0"

}

spotbugs {
    ignoreFailures.set(false)
    toolVersion.set("4.3.0")
    showProgress.set(true)
    effort.set(com.github.spotbugs.snom.Effort.MAX)
    reportLevel.set(com.github.spotbugs.snom.Confidence.MEDIUM)
    omitVisitors.addAll(listOf("FindReturnRef", "RuntimeExceptionCapture"))
    maxHeapSize.set("1g")
    excludeFilter.set(file("excludeFilter.xml"))
    sourceSets.add(sourceSets.main.get())
}

allprojects {
    java.sourceCompatibility = JavaVersion.VERSION_11
    java.targetCompatibility = JavaVersion.VERSION_11
}
checkstyle {
    group = "verification"
    toolVersion = "8.42"
    config = resources.text.fromFile("checkstyle.xml", "UTF-8")
    isShowViolations = true
    isIgnoreFailures = false
    maxWarnings = 0


}

jacoco {
    toolVersion = "0.8.6"


}

repositories {
//    mavenLocal()
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    mavenCentral()
}

springBoot {
    buildInfo()
}



dependencies {

    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation(platform("software.amazon.awssdk:bom:2.17.149"))
    implementation("com.google.protobuf:protobuf-java-util:$protocVersion")
    annotationProcessor("no.entur.mapstruct.spi:protobuf-spi-impl:$mapstructSpiGrpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-services:${grpcVersion}")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.thymeleaf:thymeleaf-spring5")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeine")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-websocket") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    // docker api
    implementation("com.github.docker-java:docker-java:3.2.12") {
        exclude(module = "docker-java-transport-jersey")
        exclude(module = "bcpkix-jdk15on")
    }
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.2.12")
    compileOnly("org.projectlombok:lombok:$versionLombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:$versionMapstruct")
    annotationProcessor("org.projectlombok:lombok:$versionLombok")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    compileOnly("org.mapstruct:mapstruct:$versionMapstruct")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("io.swagger.parser.v3:swagger-parser:2.0.27")
    implementation("org.springdoc:springdoc-openapi-ui:1.5.10")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.8.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("io.vavr:vavr:0.10.4")
    implementation("org.codehaus.groovy:groovy:3.0.8")
    implementation("com.google.guava:guava:31.0.1-jre")
    compileOnly("com.github.spotbugs:spotbugs-annotations:${spotbugs.toolVersion.get()}")
    spotbugs("com.github.spotbugs:spotbugs:${spotbugs.toolVersion.get()}")
    implementation("io.jsonwebtoken:jjwt-api:$versionJjwt")
    implementation("io.jsonwebtoken:jjwt-impl:$versionJjwt")
    implementation("io.jsonwebtoken:jjwt-jackson:$versionJjwt")
    implementation("net.logstash.logback:logstash-logback-encoder:6.6")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito:mockito-inline:3.6.28")
    implementation("com.github.spullara.mustache.java:$versionMustache")
    implementation("com.jayway.jsonpath:json-path:$versionJsonPath")
    implementation("cn.afterturn:easypoi-base:$versionEasyPoiBase")
    implementation ("net.sourceforge.jexcelapi:jxl:2.6.12"){
        exclude(module = "log4j")
    }
    implementation("software.amazon.awssdk:s3:2.16.60")
    implementation("net.minidev:json-smart:2.4.8")

}

// Temporarily cancel writing to the list through packaging.
//tasks.withType<BootJar> {
//    manifest {
//        attributes("Product-Version" to project.version.toString())
//    }
//}


tasks.checkstyleTest {
    group = "verification"
    enabled = false
}

tasks.checkstyleMain {
    group = "verification"
    sourceSets.add(sourceSets.main.get())


}

tasks.spotbugsTest {
    enabled = false
}

tasks.spotbugsMain {
    group = "verification"
    showStackTraces = true
    reports.register("html")
}



tasks.jacocoTestReport {
    classDirectories.setFrom(sourceSets.main.get().output.asFileTree.matching {
        exclude(
                "com/sms/courier/security/SecurityConfig.class",
                "com/sms/courier/utils/**",
                "com/sms/courier/engine/**",
                "com/sms/courier/parser/converter/**.class",
                "com/sms/courier/common/**",
                "**/entity/**/**.class",
                "**/courierApplication.class",
                "com/sms/courier/infrastructure/**",
                "com/sms/courier/websocket/**.class",
                "com/sms/courier/config/**.class",
                "com/sms/courier/controller/**",
                "com/sms/courier/dto/**",
                "com/sms/courier/engine/grpc/api/v1/**"
        )
    })
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(true)
    }


}



tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                // 暂时修改 后期补单元测试
                minimum = "0.4".toBigDecimal()
            }
        }

    }
}



tasks.test {
    finalizedBy(tasks.jacocoTestReport)
    useJUnitPlatform()
    testLogging {
        lifecycle {
            events = mutableSetOf(FAILED, PASSED, TestLogEvent.SKIPPED)
            exceptionFormat = FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true

        }
        info.events = lifecycle.events
        info.exceptionFormat = lifecycle.exceptionFormat
    }
    val failedTests = mutableListOf<TestDescriptor>()
    val skippedTests = mutableListOf<TestDescriptor>()
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
            when (result.resultType) {
                TestResult.ResultType.FAILURE -> failedTests.add(testDescriptor)
                TestResult.ResultType.SKIPPED -> skippedTests.add(testDescriptor)
                else -> Unit
            }
        }

        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) { // root suite
                logger.lifecycle("----")
                logger.lifecycle("Test result: ${result.resultType}")
                logger.lifecycle(
                        "Test summary: ${result.testCount} tests, " +
                                "${result.successfulTestCount} succeeded, " +
                                "${result.failedTestCount} failed, " +
                                "${result.skippedTestCount} skipped"
                )
                failedTests.takeIf { it.isNotEmpty() }?.prefixedSummary("\tFailed Tests")
                skippedTests.takeIf { it.isNotEmpty() }?.prefixedSummary("\tSkipped Tests:")
            }
        }

        private infix fun List<TestDescriptor>.prefixedSummary(subject: String) {
            logger.lifecycle(subject)
            forEach { test -> logger.lifecycle("\t\t${test.displayName()}") }
        }

        private fun TestDescriptor.displayName() = parent?.let { "${it.name} - $name" } ?: name
    })
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:-unchecked", "-Xlint:none", "-nowarn", "-Xlint:-deprecation"))
    options.isWarnings = true
    options.isVerbose = true
    options.isDeprecation = false

}


protobuf {

    protoc {
        artifact = "com.google.protobuf:protoc:$protocVersion"
    }
    plugins {
        id("grpc") {
            //As a codegen plugin to generate Java code
            //Declares which version of Maven Central library to use.
            //However, it is only declared here,
            //This description alone does not "apply" the plugin.
            //To apply it, you need to set generateProtoTasks, which will be described later.
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    // protobuf-gradle-The plugin will generate a task each time you run protoc.
    //You can set what to use as a code generator for this task.
    //In generateProtoTasks, make the settings.
    generateProtoTasks {
        all().forEach {
            //it means a task that is generated each time you run protoc.
            //This task is done with builtins (the code generator that comes with protoc)
            //plugins (a type of code generator combined with protocol)
            //There are two types of properties, and you can set the one you like.
            //Here, as plugins
            //I have two codegen plugins, grpc and grpckt declared above.
            //This setting "applies" these codegen plugins.

            it.plugins {
                id("grpc")
            }
        }
    }

}

