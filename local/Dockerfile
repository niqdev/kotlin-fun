FROM node:alpine3.16

# https://github.com/serverless/serverless/releases
ARG SERVERLESS_VERSION="3.20.0"
# https://github.com/localstack/serverless-localstack#change-log
ARG SERVERLESS_LOCALSTACK_VERSION="0.4.36"

RUN apk upgrade --update && apk --no-cache add \
  bash \
  curl \
  jq \
  yq

WORKDIR /usr/src/app

RUN \
  npm install -g serverless@${SERVERLESS_VERSION} && \
  npm install --save-dev serverless-localstack@${SERVERLESS_LOCALSTACK_VERSION}

CMD ["/bin/bash"]
