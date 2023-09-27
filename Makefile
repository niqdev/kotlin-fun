.DEFAULT_GOAL := help

require-%:
	@ if [ "$(shell command -v ${*} 2> /dev/null)" = "" ]; then \
		echo "[$*] not found"; \
		exit 1; \
	fi

check-param-%:
	@ if [ "${${*}}" = "" ]; then \
		echo "Missing parameter: [$*]"; \
		exit 1; \
	fi

# https://pablosanjose.com/the-best-makefile-help
help: ## Show this help message
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  \033[36m\033[0m\n"} /^[$$()% a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

##############################

.PHONY: local-up
local-up: require-docker ## Starts Localstack
	docker-compose -f local/docker-compose.yml up -d

.PHONY: local-down
local-down: require-docker ## Stop Localstack
	docker-compose -f local/docker-compose.yml down -v

##############################

.PHONY: local-stack-create
local-stack-create: require-aws check-param-env ## TODO
	./local/scripts/cft_apply.sh "create" ${env}

.PHONY: local-stack-delete
local-stack-delete: require-aws check-param-env ## TODO
	./local/scripts/cft_apply.sh "delete" ${env}

.PHONY: local-stack-clean
local-stack-clean: ## TODO
	rm -frv local/.localstack

##############################

.PHONY: app-bin
app-bin: ## Build distribution
	./gradlew clean build installDist
	rm -fr ./kfun
	ln -s ./modules/app/build/install/app/bin/app ./kfun
