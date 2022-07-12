dependencies {
  implementation("aws.sdk.kotlin:lambda:${Versions.awsKotlin}")
  implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
  runtimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")
}
