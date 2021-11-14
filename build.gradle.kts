plugins {
    kotlin("jvm") version "1.5.31"
    idea
    application
    jacoco
}

group "org.example"
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json-jvm
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
    useJUnitPlatform()
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("${buildDir}/jacoco/test.exec"))
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "11"
    }
}

application {
    mainClass.set("PerformanceBillingSystemKt")
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
    }
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

// 커버리지 검증
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.20".toBigDecimal()
            }
        }

        rule {
// 룰을 간단히 켜고 끌 수 있다.
            enabled = true

// 룰을 체크할 단위는 클래스 단위
            element = "CLASS"

// 브랜치 커버리지를 최소한 90% 만족시켜야 한다.
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }

// 라인 커버리지를 최소한 80% 만족시켜야 한다.
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }

// 빈 줄을 제외한 코드의 라인수를 최대 200라인으로 제한한다.
            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "200".toBigDecimal()
            }

// 커버리지 체크를 제외할 클래스들
            excludes = listOf()
        }
    }
}
