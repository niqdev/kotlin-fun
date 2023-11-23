package com.github.niqdev.ktor.server

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.github.niqdev.ktor.server.repositories.DatabaseSetup
import com.github.niqdev.ktor.server.repositories.UserRepositoryImpl
import com.github.niqdev.ktor.server.routes.statusRoutes
import com.github.niqdev.ktor.server.routes.userRoutes
import com.github.niqdev.ktor.server.routes.versionRoutes
import com.github.niqdev.ktor.server.services.UserServiceImpl
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

// DI https://insert-koin.io/docs/reference/koin-ktor/ktor

// https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.commonModule() {
  log.debug("Loading common plugins")
  install(CallLogging)

  // serialization
  install(ContentNegotiation) {
    jackson {
      configure(SerializationFeature.INDENT_OUTPUT, true)
      setPropertyNamingStrategy(PropertyNamingStrategies.SnakeCaseStrategy.INSTANCE)
    }
  }
}

fun Application.mainModule() {
  log.debug("Loading configs")
  val config = loadConfigOrThrow()
  log.info("\n$config")

  log.debug("Migrating database")
  val dataSource = DatabaseSetup.initDataSource(config.database)
  DatabaseSetup.migrateDatabase(dataSource)
  val jdbiClient = DatabaseSetup.initJdbiClient(dataSource)

  log.debug("Loading dependency graph")
  val userRepository = UserRepositoryImpl(jdbiClient)
  val userService = UserServiceImpl(userRepository)

  log.debug("Loading route plugins")
  routing {
    statusRoutes()
    userRoutes(userService)
    versionRoutes()
  }
}

private fun Application.loadConfigOrThrow(): ServerConfig =
  ServerConfig.load(environment.config.config("server").toMap()).getOrThrow()
