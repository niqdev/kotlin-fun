FROM node:alpine3.16

# https://github.com/serverless/serverless/releases
ARG SERVERLESS_VERSION="3.21.0"
# https://github.com/localstack/serverless-localstack#change-log
ARG SERVERLESS_LOCALSTACK_VERSION="1.0.0"

RUN \
  apk upgrade --update && \
  apk add --no-cache \
    bash \
    curl \
    jq \
    aws-cli

WORKDIR /usr/src/app
COPY ./aws /root/.aws

RUN \
  npm install -g serverless@${SERVERLESS_VERSION} && \
  npm install --save-dev serverless-localstack@${SERVERLESS_LOCALSTACK_VERSION}

CMD ["/bin/bash"]
