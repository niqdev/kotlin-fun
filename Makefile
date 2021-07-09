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

##############################

.PHONY: local-up
local-up: require-docker
	docker-compose -f local/docker-compose.yml up -d

.PHONY: local-down
local-down: require-docker
	docker-compose -f local/docker-compose.yml down -v

##############################

.PHONY: local-stack-create
local-stack-create: require-aws check-param-env
	./local/scripts/cft_apply.sh "create" ${env}

.PHONY: local-stack-delete
local-stack-delete: require-aws check-param-env
	./local/scripts/cft_apply.sh "delete" ${env}

.PHONY: local-stack-clean
local-stack-clean:
	rm -frv local/.localstack
