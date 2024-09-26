## aws-serverless-sqs

Examples
* https://github.com/localstack/localstack/issues/3585
* https://github.com/localstack/localstack/issues/967
* https://github.com/localstack/localstack/issues/4513
* https://github.com/localstack/localstack/issues/1216
* https://faun.pub/enable-aws-s3-bucket-events-notification-publishing-to-sqs-locally-using-localstack-45f369f74399
* https://stackoverflow.com/questions/68988931/localstack-lambda-is-not-triggered-by-sqs
* https://www.zakariaamine.com/2019-02-17/lambda-with-sqs-eventsource-cli

### Development

```bash
# local-up
docker-compose -f local/docker-compose-serverless-sqs.yml up

# local-down
docker-compose -f local/docker-compose-serverless-sqs.yml down -v
rm -frv ./local/.localstack ./local/data/.serverless ./local/data/*.zip ./local/data/*.log

# build
./gradlew :modules:aws-serverless-sqs:clean :modules:aws-serverless-sqs:build
```

### AWS cli

```bash
aws iam create-role \
  --endpoint-url=http://localhost:4566 \
  --role-name localstack-role \
  --assume-role-policy-document '{"Version": "2012-10-17", "Statement": [{"Effect": "Allow", "Action": ["lambda:InvokeFunction"]}, {"Effect": "Allow", "Action": ["sqs:SendMessage", "sqs:ReceiveMessage", "sqs:DeleteMessage", "sqs:GetQueueAttributes"]}]}'

# create
aws lambda create-function \
  --endpoint-url=http://localhost:4566 \
  --function-name fn-aws-serverless-sqs \
  --runtime java11 \
  --handler com.github.niqdev.aws.sqs.Handler::handleRequest \
  --role arn:aws:iam::000000000000:role/localstack-role \
  --zip-file fileb://modules/aws-serverless-sqs/build/distributions/aws-serverless-sqs-0.1.0.zip

# invoke
aws lambda invoke \
  --endpoint-url=http://localhost:4566 \
  --function-name fn-aws-serverless-sqs \
  --payload $(echo "{\"key\":\"value\"}" | base64) \
  local/.localstack/logs/aws-serverless-sqs-output.json

aws s3 ls --endpoint-url=http://localhost:4566
aws s3api --endpoint-url=http://localhost:4566 get-bucket-notification-configuration --bucket my-bucket
aws sqs list-queues --endpoint-url=http://localhost:4566
aws sqs get-queue-attributes \
  --endpoint-url=http://localhost:4566 \
  --queue-url http://localhost:4566/000000000000/MyQueue \
  --attribute-names QueueArn

aws lambda create-event-source-mapping \
  --endpoint-url=http://localhost:4566 \
  --event-source-arn arn:aws:sqs:us-east-1:000000000000:MyQueue \
  --batch-size 1 \
  --maximum-retry-attempts 3 \
  --function-name fn-aws-serverless-sqs

aws sqs send-message \
  --endpoint-url=http://localhost:4566 \
  --queue-url http://localhost:4566/000000000000/MyQueue \
  --message-body $(echo "{\"key\":\"value\"}" | base64)
 
aws sqs receive-message \
  --endpoint-url=http://localhost:4566 \
  --queue-url http://localhost:4566/000000000000/MyQueue

aws s3 cp \
  --endpoint-url=http://localhost:4566 \
  README.md s3://my-bucket
```

### TODO Serverless

```bash
# verify status
curl http://localhost:4566/health | jq
docker exec -it local-serverless-sqs-dev curl http://localstack:4566/health | jq
docker exec -it local-serverless-sqs-dev aws --endpoint-url=http://localstack:4566 s3 ls
docker exec -it local-serverless-sqs-dev aws --endpoint-url=http://localstack:4566 sqs list-queues

# local-deploy
cp modules/aws-serverless-sqs/build/distributions/aws-serverless-sqs-*.zip local/data/aws-serverless-sqs.zip
docker exec -it --workdir /usr/src/app/data local-serverless-sqs-dev \
  serverless deploy --config serverless-sqs.yml --stage local

# produce
docker exec -it local-serverless-sqs-dev aws --endpoint-url=http://localstack:4566 sqs send-message \
  --queue-url http://localstack:4566/000000000000/MyQueue \
  --message-body "hello"
```
