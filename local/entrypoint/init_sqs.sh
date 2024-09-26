#!/bin/bash

ENDPOINT=http://localstack:4566

echo "[*] create bucket: $BUCKET_NAME"
aws s3api create-bucket \
  --endpoint-url=${ENDPOINT} \
  --bucket ${BUCKET_NAME}

echo "[*] create queue: $QUEUE_NAME"
aws sqs create-queue \
  --endpoint-url=${ENDPOINT} \
  --queue-name ${QUEUE_NAME}

echo "[*] create bucket notification"
aws s3api put-bucket-notification-configuration \
  --endpoint-url=${ENDPOINT} \
  --bucket ${BUCKET_NAME} \
  --notification-configuration file:///docker-entrypoint-initaws.d/my-queue-notification.json
