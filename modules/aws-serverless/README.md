## aws-serverless

* [Serverless Framework](https://www.serverless.com/framework/docs)
* [Serverless.yml Reference](https://www.serverless.com/framework/docs/providers/aws/guide/serverless.yml)
* [Serverless Localstack](https://www.serverless.com/plugins/serverless-localstack)
* [LocalStack with Serverless Framework](https://docs.localstack.cloud/integrations/serverless-framework)
* [LocalStack](https://docs.localstack.cloud)

Examples
* https://onexlab-io.medium.com/serverless-localstack-lambda-53fd8d46983
* https://github.com/serverless/examples/tree/v3/aws-java-simple-http-endpoint
* https://github.com/localstack/serverless-examples/tree/master/aws-java-simple-http-endpoint
* https://github.com/localstack/localstack-pro-samples
* https://github.com/aws-samples/serverless-kotlin-demo
* https://github.com/aws-samples/lambda-kotlin-groovy-example
* https://github.com/vikie1/aws-lambda-localstack-example
* https://github.com/mattmurr/kotlin-cdk-apigw-lambda

### Development

Localstack
```bash
# start
docker-compose -f local/docker-compose-localstack.yml up
docker-compose -f local/docker-compose-localstack.yml logs --follow

# verify
curl http://localhost:4566/health

# cleanup
docker-compose -f local/docker-compose-localstack.yml down -v
rm -fr local/.localstack
rm -fr modules/aws-serverless/.serverless
```

Lambda
```bash
# build zip
# modules/aws-serverless/build/distributions/aws-serverless-0.1.0.zip
./gradlew :modules:aws-serverless:clean :modules:aws-serverless:build
# it's NOT runnable
java -jar modules/aws-serverless/build/libs/aws-serverless-0.1.0.jar

# create
aws lambda create-function \
  --endpoint-url=http://localhost:4566 \
  --function-name fn-aws-serverless \
  --runtime java11 \
  --handler com.github.niqdev.aws.serverless.Handler::handleRequest \
  --role arn:aws:iam::000000000000:role/localstack-role \
  --zip-file fileb://modules/aws-serverless/build/distributions/aws-serverless-0.1.0.zip

# update
aws lambda update-function-code \
  --endpoint-url=http://localhost:4566 \
  --function-name fn-aws-serverless \
  --zip-file fileb://modules/aws-serverless/build/distributions/aws-serverless-0.1.0.zip

# verify
aws lambda get-function \
  --endpoint-url=http://localhost:4566 \
  --function-name fn-aws-serverless

# invoke
aws lambda invoke \
  --endpoint-url=http://localhost:4566 \
  --function-name fn-aws-serverless local/.localstack/logs/aws-serverless-output.json
```

### Serverless

Setup first time only
```bash
# serverless
curl -o- -L https://slss.io/install | VERSION=3.20.0 bash

# nvm, node, npm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash
nvm install --lts

# serverless-localstack
npm install --save-dev serverless-localstack@0.4.36
```

Serverless
```bash
cd modules/aws-serverless
serverless deploy --config serverless.yml --stage local
serverless invoke --config serverless.yml --stage local --function fn-aws-serverless

# test endpoint
curl -v http://localhost:4566/restapis/<RANDOM_VALUE>/local/_user_request_/hello
http http://localhost:4566/restapis/<RANDOM_VALUE>/local/_user_request_/hello
```

### Serverless Docker

```bash
# local-up
docker-compose -f local/docker-compose-serverless.yml up

# local-down
docker-compose -f local/docker-compose-serverless.yml down -v
rm -frv ./local/.localstack ./local/data/.serverless ./local/data/*.zip ./local/data/*.log

# build
./gradlew :modules:aws-serverless:clean :modules:aws-serverless:build

# verify status
curl http://localhost:4566/health | jq
docker exec -it local-serverless-dev curl http://localstack:4566/health | jq
docker exec -it local-serverless-dev aws --endpoint-url=http://localstack:4566 s3api create-bucket --bucket example
docker exec -it local-serverless-dev aws --endpoint-url=http://localstack:4566 s3 ls

# --workdir /usr/src/app/data
# serverless.yml MUST be in the root path

# local-deploy
cp modules/aws-serverless/build/distributions/aws-serverless-*.zip local/data/aws-serverless.zip
docker exec -it --workdir /usr/src/app/data local-serverless-dev \
  serverless deploy --config serverless.yml --stage local

# local-invoke with serverless
docker exec -it --workdir /usr/src/app/data local-serverless-dev \
  serverless invoke --config serverless.yml --stage local --function fn-aws-serverless --path event-example.json

# local-invoke with cli
docker exec -it --workdir /usr/src/app/data local-serverless-dev \
  aws --endpoint-url=http://localstack:4566 lambda invoke --function-name fn-aws-serverless --payload file://event-example.json event.log

# test endpoint
docker exec -it local-serverless-dev bash
curl -v http://localstack:4566/restapis/mm90aislcg/local/_user_request_/hello
```
