package com.github.niqdev

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

/*

# setup minio profile
aws configure --profile minio
#AWS Access Key ID [None]: LOCAL_ACCESS_KEY
#AWS Secret Access Key [None]: LOCAL_SECRET_KEY
#Default region name [None]: us-east-1
#Default output format [None]:

aws --profile minio --endpoint-url http://localhost:9000 s3 mb s3://my-bucket
aws --profile minio --endpoint-url http://localhost:9000 s3 cp --recursive ./local/data/<PATH> s3://my-bucket

*/
object aws {
  val localstackUrl = "http://localhost:4566"
  val minioUrl = "http://localhost:9000"

  val credentials = AwsBasicCredentials.create("LOCAL_ACCESS_KEY", "LOCAL_SECRET_KEY")
  val s3Client =
    S3Client
      .builder()
      .credentialsProvider(StaticCredentialsProvider.create(credentials))
      .endpointOverride(java.net.URI.create(minioUrl))
      .region(Region.US_EAST_1)
      .build()
}
