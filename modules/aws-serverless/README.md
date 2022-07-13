## aws-serverless

* [Serverless Framework](https://www.serverless.com/framework/docs)
* [Serverless.yml Reference](https://www.serverless.com/framework/docs/providers/aws/guide/serverless.yml)
* [Serverless Localstack](https://www.serverless.com/plugins/serverless-localstack)
* [LocalStack with Serverless Framework](https://docs.localstack.cloud/integrations/serverless-framework)

TODOs
* https://onexlab-io.medium.com/serverless-localstack-lambda-53fd8d46983
* https://github.com/serverless/examples/tree/v3/aws-java-simple-http-endpoint
* https://github.com/localstack/serverless-examples/tree/master/aws-java-simple-http-endpoint
* https://github.com/localstack/localstack-pro-samples
* https://github.com/aws-samples/serverless-kotlin-demo
* https://github.com/aws-samples/lambda-kotlin-groovy-example
* https://github.com/vikie1/aws-lambda-localstack-example
* https://github.com/mattmurr/kotlin-cdk-apigw-lambda

```bash
# build zip
# modules/aws-serverless/build/distributions/aws-serverless-0.1.0.zip
./gradlew clean :modules:aws-serverless:build

# TODO
docker-compose -f local/docker-compose-serverless.yml up

curl http://localhost:4566
curl http://localhost:4566/health

aws lambda create-function \
  --endpoint-url=http://localhost:4566 \
  --function-name fn-aws-serverless \
  --runtime java11 \
  --handler com.github.niqdev.aws.serverless.Handler \
  --role arn:aws:iam::000000000000:role/localstack-role \
  --zip-file fileb://modules/aws-serverless/build/distributions/aws-serverless-0.1.0.zip
  
aws lambda update-function-code \
  --endpoint-url=http://localhost:4566 \
  --function-name fn-aws-serverless \
  --zip-file fileb://modules/aws-serverless/build/distributions/aws-serverless-0.1.0.zip

aws --endpoint-url=http://localhost:4566 lambda get-function --function-name fn-aws-serverless

aws --endpoint-url=http://localhost:4566 lambda invoke --function-name fn-aws-serverless output.json

npm install --save-dev serverless-localstack

cd modules/aws-serverless
serverless deploy --config serverless.yml --stage local
serverless invoke --config serverless.yml --stage local --function fn-aws-serverless

# TODO same error, fix ".localstack"
# local-localstack | docker.errors.APIError: 400 Client Error for http+docker://localhost/v1.41/containers/create: Bad Request ("create ".localstack"/zipfile.76caa2f2: "\".localstack\"/zipfile.76caa2f2" includes invalid characters for a local volume name, only "[a-zA-Z0-9][a-zA-Z0-9_.-]" are allowed. If you intended to pass a host directory, use absolute path")

local-localstack | Class not found: com.github.niqdev.aws.serverless.Handler: java.lang.ClassNotFoundException
local-localstack | java.lang.ClassNotFoundException: com.github.niqdev.aws.serverless.Handler
local-localstack | 	at java.base/java.net.URLClassLoader.findClass(Unknown Source)
local-localstack | 	at java.base/java.lang.ClassLoader.loadClass(Unknown Source)
local-localstack | 	at java.base/java.lang.ClassLoader.loadClass(Unknown Source)
local-localstack | 	at java.base/java.lang.Class.forName0(Native Method)
local-localstack | 	at java.base/java.lang.Class.forName(Unknown Source)
```
