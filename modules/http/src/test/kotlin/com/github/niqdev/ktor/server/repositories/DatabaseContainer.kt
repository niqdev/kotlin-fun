package com.github.niqdev.ktor.server.repositories

import com.github.niqdev.ktor.server.DatabaseConfig
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.Slf4JSqlLogger
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

// https://java.testcontainers.org/test_framework_integration/junit_5
object DatabaseContainer {

  private val postgresContainer: PostgreSQLContainer<*> by lazy {
    PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
      .withDatabaseName("test_db")
      .withUsername("test_username")
      .withPassword("test_password")
      .also { it.start() }
      .also {
        val databaseConfig = DatabaseConfig(it.getJdbcUrl(), it.username, it.password)
        DatabaseSetup.migrateDatabase(DatabaseSetup.initDataSource(databaseConfig))
      }
  }

  val client: Jdbi by lazy {
    Jdbi.create(postgresContainer.getJdbcUrl(), postgresContainer.username, postgresContainer.password)
      .setSqlLogger(Slf4JSqlLogger())
  }
}
