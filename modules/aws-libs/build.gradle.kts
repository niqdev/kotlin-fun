dependencies {
  implementation(platform("software.amazon.awssdk:bom:${Versions.aws}"))
  implementation("software.amazon.awssdk:s3")
  implementation("software.amazon.awssdk:sqs")

  implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
  runtimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")
}
