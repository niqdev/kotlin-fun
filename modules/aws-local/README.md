### aws-local

* [LocalStack](https://localstack.cloud)

```bash
java -jar modules/aws-local/build/libs/aws-local-all.jar

# setup
make local-up
# TODO wait for localstack to be ready: replace with docker-entrypoint-initaws.d
make local-stack-create env=local

# validate template
aws --endpoint-url=http://localhost:4566 cloudformation validate-template --template-body file://local/cft/my-stack.template

# create bucket
aws --endpoint-url=http://localhost:4566 s3 mb s3://my-bucket-test
aws --endpoint-url=http://localhost:4566 s3api create-bucket --bucket my-bucket-test

# verify bucket
aws --endpoint-url=http://localhost:4566 s3 ls
aws --endpoint-url=http://localhost:4566 s3 ls s3://my-bucket-test
aws --endpoint-url=http://localhost:4566 s3 cp README.md s3://my-bucket-test

# create queue
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name my-queue-test

# verify queue
aws --endpoint-url=http://localhost:4566 sqs list-queues
# queue arn
aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes --queue-url http://localhost:4566/queue/my-queue-test --attribute-names All | jq -c ".Attributes.QueueArn"

# create notification
aws --endpoint-url=http://localhost:4566 s3api put-bucket-notification-configuration --bucket my-bucket-test --notification-configuration file://local/cft/my-queue-test-notification.json
# verify notification
aws --endpoint-url=http://localhost:4566 s3api get-bucket-notification-configuration --bucket my-bucket-test

# poll messages
aws --endpoint-url=http://localhost:4566 sqs receive-message --queue-url http://localhost:4566/queue/my-queue-test

# empty bucket
aws --endpoint-url=http://localhost:4566 s3 rm s3://my-bucket-test --recursive
# delete non-empty bucket
aws --endpoint-url=http://localhost:4566 s3 rb s3://my-bucket-test --force

# cleanup
make local-stack-delete env=local
make local-down
make local-stack-clean
```

<!--
https://theodorebrgn.medium.com/localstacks-guide-to-run-aws-serverless-environment-locally-discover-the-power-of-lambda-f958f8b6330
https://sopin.dev/2021/01/13/Running-AWS-Lambda-written-in-Java-with-Docker
https://docs.min.io/docs/minio-gateway-for-s3.html
https://medium.com/digio-australia/multipart-upload-to-s3-using-aws-sdk-for-java-d3fd2e17f515
-->
