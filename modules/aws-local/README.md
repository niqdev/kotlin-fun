### aws-local

* [LocalStack](https://localstack.cloud)

```
java -jar modules/aws-local/build/libs/aws-local-all.jar

make local-up
make local-down

aws --endpoint-url=http://localhost:4566 s3 ls
aws --endpoint-url=http://localhost:4566 s3api create-bucket --bucket my-bucket
aws --endpoint-url=http://localhost:4566 s3 cp README.md s3://my-bucket
aws --endpoint-url=http://localhost:4566 s3 ls s3://my-bucket
```

<!--
https://theodorebrgn.medium.com/localstacks-guide-to-run-aws-serverless-environment-locally-discover-the-power-of-lambda-f958f8b6330
https://sopin.dev/2021/01/13/Running-AWS-Lambda-written-in-Java-with-Docker
https://docs.min.io/docs/minio-gateway-for-s3.html
https://medium.com/digio-australia/multipart-upload-to-s3-using-aws-sdk-for-java-d3fd2e17f515
-->
