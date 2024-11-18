# Hypriority

- Hypriority는 Spring Boot 애플리케이션에서 Redisson 기반의 우선순위 작업 큐를 쉽게 구현할 수 있도록 설계된 라이브러리입니다.
- 이 라이브러리는 우선순위 기반 작업 처리, 비동기 작업 처리, 애노테이션 기반 작업 리스너 기능을 제공합니다.

### 1. 📦 Gradle 의존성 추가

```kotlin
dependencies {
    implementation("com.tripleauth:hypriority:{version}")


    // Apple Silicon (ARM64) 기반 MacOS 환경에서는 Netty의 DNS 네이티브 모듈을 사용하여 성능과 안정성 개선합
    val isMacOsArm = System.getProperty("os.arch").contains("aarch64") &&
            System.getProperty("os.name").lowercase().contains("mac")
    if (isMacOsArm) {
        dependencies {
            implementation("io.netty:netty-resolver-dns-native-macos:4.1.115.Final:osx-aarch_64")
        }
    }

}
```

### 2. 🛠️ Bean 등록을 위한 ClientConfig 클래스 작성

```kotlin

package com.example.config

import com.tripleauth.hypriority.config.HypriorityConfig
import com.tripleauth.hypriority.config.HypriorityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(basePackages = ["com.tripleauth.hypriority"])
@Import(HypriorityConfig::class)
class HypriorityClientConfig {

    @Bean
    fun hypriorityProperties(): HypriorityProperties {
        return HypriorityProperties()
    }
}


```

### 3. 🛠️ application.yml 설정

```yaml
hypriority:
  redis:
    host: redis.example.com
    port: 6379
    database: 1
    timeout: 5000
    connectionPoolSize: 10
    connectionMinimumIdleSize: 5
```

- hypriority.redis.host: Redis 서버의 호스트 주소
- hypriority.redis.port: Redis 서버의 포트 번호
- hypriority.redis.database: 사용할 Redis 데이터베이스 번호
- hypriority.redis.timeout: 연결 타임아웃 (밀리초)
- hypriority.redis.connectionPoolSize: 연결 풀의 최대 크기
- hypriority.redis.connectionMinimumIdleSize: 연결 풀의 최소 유휴 연결 수

### 4. 📝 DataClass를 사용한 Job 정의

- Hypriority에서는 DataClass를 사용하여 Job 데이터를 정의할 수 있습니다.
예시로 EmailNotificationJob 클래스를 사용하겠습니다.
```kotlin
// EmailNotificationJob DataClass 정의 (EmailNotificationJob.kt)
package com.example.model

data class EmailNotificationJob(
    val recipient: String,
    val subject: String,
    val message: String,
    val isHtml: Boolean
)
```

### 5. 🚀 Job 제출 예제 (EmailJobService.kt)
```kotlin
package com.example.service

import com.example.model.EmailNotificationJob
import com.tripleauth.hypriority.manager.HypriorityManager
import com.tripleauth.hypriority.model.JobPriority
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class EmailJobService(
    private val hypriorityManager: HypriorityManager
) {

    private val logger = KotlinLogging.logger {}
    private val queueName = "email-notification-queue"

    fun submitEmailJob(recipient: String, subject: String, message: String, isHtml: Boolean) {
        val jobData = EmailNotificationJob(recipient, subject, message, isHtml)
        val jobId = "email-job-${System.currentTimeMillis()}"
        val priority = JobPriority.HIGH

        hypriorityManager.addJob(queueName, jobId, jobData, priority)
        logger.info { "Submitted EmailNotificationJob with ID: $jobId to queue: $queueName" }
    }
}

```

### 6. 🛠️ Job 처리 예제 (EmailJobConsumer.kt)
- @HypriorityListener 애노테이션을 사용, 큐에서 작업을 자동으로 가져오고, DataClass인 EmailNotificationJob으로 데이터를 처리합니다.

```kotlin
package com.example.consumer

import com.example.model.EmailNotificationJob
import com.tripleauth.hypriority.annotation.HypriorityListener
import com.tripleauth.hypriority.model.Job
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class EmailJobConsumer {

    private val logger = KotlinLogging.logger {}

    // 주요 속성 queueName (필수)
    // 예시: "email-notification-queue"

    // concurrency (선택)
    // 동시에 처리할 작업의 개수를 설정합니다.
    // 기본값은 1이며, 2 이상의 값으로 설정하면 여러 스레드가 동시에 작업을 가져와 처리합니다.
    @HypriorityListener(queueName = "email-notification-queue", concurrency = 2)
    fun processEmailJob(job: Job<EmailNotificationJob>) {
        val data = job.data
        logger.info { "Sending email to ${data.recipient} with subject '${data.subject}'" }
    }
}

```

