package com.github.niqdev.ktor.server.repositories

import com.github.niqdev.ktor.server.DatabaseConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import javax.sql.DataSource

object DatabaseSetup {

  fun initDataSource(config: DatabaseConfig): DataSource =
    PGSimpleDataSource().apply {
      setURL(config.url)
      user = config.username
      password = config.password
    }

  // https://documentation.red-gate.com/fd/quickstart-api-184127575.html
  fun migrateDatabase(dataSource: DataSource): MigrateResult =
    Flyway.configure().dataSource(dataSource).load().migrate()

  fun initJdbiClient(dataSource: DataSource): Jdbi =
    Jdbi.create(dataSource)
}
