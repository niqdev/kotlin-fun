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
