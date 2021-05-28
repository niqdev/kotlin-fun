package com.github.niqdev.reactor

import reactor.core.publisher.Flux
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

// https://projectreactor.io
// https://yongtze-chi.medium.com/data-ingest-with-reactive-streams-in-java-fe441f2ff739
// https://www.vinsguru.com/reactor-flux-file-reading
// https://spring.io/blog/2019/12/13/flight-of-the-flux-3-hopping-threads-and-schedulers

/**
 * ./gradlew runReactorExample
 */
fun main() {

  val inputPath = Path.of("../../README.md")
  val outputPath = Path.of("../../test.txt")
  val buffer = Files.newBufferedWriter(outputPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)

  Flux.using({ Files.lines(inputPath) }, { lines -> Flux.fromStream(lines) }, { it.close() })
    .map { line -> ">>> $line" }
    .subscribe(
      { stream ->
        buffer.write(stream)
        buffer.newLine()
      },
      { buffer.close() },
      { buffer.close() }
    )
}
