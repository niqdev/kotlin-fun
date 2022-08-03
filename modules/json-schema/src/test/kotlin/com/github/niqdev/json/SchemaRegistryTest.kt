package com.github.niqdev.json

import io.confluent.kafka.schemaregistry.json.JsonSchema
import io.confluent.kafka.schemaregistry.json.JsonSchemaProvider
import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig
import io.confluent.kafka.schemaregistry.rest.SchemaRegistryRestApplication
import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.embedded.kafka.EmbeddedKafkaListener
import io.kotest.matchers.shouldBe

class SchemaRegistryTest : WordSpec({

  // see Testcontainers: in-memory kafka and zookeeper
  listener(EmbeddedKafkaListener(kafkaPort = 6005, zookeeperPort = 9005))

  "SchemaRegistry" should {

    "verify" {
      val props = java.util.Properties()
      // without kafka schema-registry doesn't start
      props[SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG] = "localhost:6005"
      props[SchemaRegistryConfig.SCHEMA_PROVIDERS_CONFIG] = JsonSchemaProvider::class.java.name
      val app = SchemaRegistryRestApplication(props)
      app.start()

      val client = MockSchemaRegistry.getClientForScope("testClient")
      val schemaString = "/employee.schema.json".jsonToString()
      val jsonSchema = JsonSchema(schemaString)

      // validate should throw if invalid
      // jsonSchema.validate()

      // e.g. topic name
      val subject = "test-subject"
      val schemaId = client.register(subject, jsonSchema)

      schemaId shouldBe 1
      client.allSubjects shouldBe listOf(subject)
      val schema = client.getSchemaById(1)
      schema.schemaType() shouldBe "JSON"
      // title
      schema.name() shouldBe "Record of employee"
      schema.rawSchema() shouldBe jsonSchema.rawSchema()

      // TODO make sure it's always invoked
      app.stop()
    }
  }
})
