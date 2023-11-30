## http

TODO
* [bruno](https://www.usebruno.com)
* http-client

### Development

* [Ktor](https://ktor.io/docs)
* [Jdbi](https://jdbi.org)
* [Flyway](https://documentation.red-gate.com/fd)
* [Testcontainers](https://java.testcontainers.org)

```bash
# runs tests
./gradlew :modules:http:test

# starts dependencies
docker-compose -f local/docker-compose-http.yml up -d
docker-compose -f local/docker-compose-http.yml logs --follow
docker-compose -f local/docker-compose-http.yml down -v

# starts server
./gradlew :modules:http:run

# healthcheck
curl http://localhost:8080/status
# version
curl -H 'X-My-Version: foo' http://localhost:8080/version
# list
curl -sS http://localhost:8080/user | jq
# get
curl -v http://localhost:8080/user/e5c931fd-2ed0-4af7-bf17-a53d2d3daa66
# add
curl -sS http://localhost:8080/user -H 'Content-Type: application/json' --data '{"name":"foo","age":42}' | jq
```

### Deployment

* [niqdev/kotlin-fun-http](https://hub.docker.com/r/niqdev/kotlin-fun-http)

```bash
# publishes local image
./gradlew :modules:http:publishImageToLocalRegistry

# publishes remote image
./gradlew :modules:http:publishImage

# starts test container
docker run --rm \
  --name kotlin-fun-http \
  --network http-server_default \
  -e POSTGRES_URL="jdbc:postgresql://postgres:5432/example_db" \
  -p 8080:8080 \
  niqdev/kotlin-fun-http
```

* [Helm](https://helm.sh/docs)
* PostgreSQL charts
  - [bitnami/postgresql](https://github.com/bitnami/charts/tree/main/bitnami/postgresql)
  - [cetic/helm-postgresql](https://github.com/cetic/helm-postgresql)
* [Deploy Helm OCI charts with ArgoCD](https://drake0103.medium.com/deploy-helm-oci-charts-with-argocd-583699c7d739)

```bash
# create chart
mkdir -p helm-charts && helm create helm-charts/kotlin-fun-http

# verify chart
helm template helm-charts/kotlin-fun-http --debug > tmp-app.yaml
```

* [argo-cd](https://github.com/hckops/kube-template/blob/main/applications/templates/examples/kotlin-fun.yaml) app

```bash
TODO port-forward
```
