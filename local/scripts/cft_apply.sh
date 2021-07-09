#!/bin/bash

CURRENT_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")"; pwd -P)
cd ${CURRENT_PATH}

##############################

PARAM_ACTION=${1:?"Missing ACTION"}
PARAM_ENVIRONMENT=${2:?"Missing ENVIRONMENT"}

ROOT_PATH="${CURRENT_PATH}/../.."
TEMPLATE_PATH="${ROOT_PATH}/local/cft"

STACK_PREFIX="my-stack"
STACK_NAME="${STACK_PREFIX}-${PARAM_ENVIRONMENT}"

ENDPOINT_URL="http://localhost:4566"
CLI_PARAM=$([[ "$PARAM_ENVIRONMENT" == "local" ]] && echo "--endpoint-url ${ENDPOINT_URL}" || echo "")

##############################

echo "[+] cft_apply"

echo "[*] ACTION=${PARAM_ACTION}"
echo "[*] ENVIRONMENT=${PARAM_ENVIRONMENT}"

case ${PARAM_ACTION} in
  "create")
    aws ${CLI_PARAM} \
	    cloudformation create-stack \
	    --stack-name ${STACK_NAME} \
	    --template-body file://${TEMPLATE_PATH}/${STACK_PREFIX}.template \
	    --parameters file://${TEMPLATE_PATH}/${STACK_NAME}.json \
	    --tags Key=Service,Value=${STACK_PREFIX} Key=Stack,Value=${STACK_NAME}
  ;;
  "update")
    echo "TODO update"
  ;;
  "delete")
    aws ${CLI_PARAM} \
	    cloudformation delete-stack \
	    --stack-name ${STACK_NAME}
  ;;
  *)
    echo "ERROR: unknown command"
    exit 1
  ;;
esac

echo "[-] cft_apply"
