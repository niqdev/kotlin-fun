package com.github.niqdev

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

object aws {

  val localstackUrl = "http://localhost:4566"
  val minioUrl = "http://localhost:9000"

  val credentials = AwsBasicCredentials.create("LOCAL_ACCESS_KEY", "LOCAL_SECRET_KEY")
  val s3Client = S3Client.builder()
    .credentialsProvider(StaticCredentialsProvider.create(credentials))
    .endpointOverride(java.net.URI.create(minioUrl))
    .region(Region.US_EAST_1)
    .build()
}
