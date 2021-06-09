§1require-%:
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
