## http

### Development

* [Ktor](https://ktor.io/docs) and [snippets](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets)
* [Jdbi](https://jdbi.org)
* [Flyway](https://documentation.red-gate.com/fd)
* [Testcontainers](https://java.testcontainers.org)
* [bruno](https://www.usebruno.com) Open Collection `local/bruno/kotlin-fun-http`

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
# openapi spec
curl -sS http://localhost:8080/swagger/documentation.yaml | yq
# version
curl -H 'X-My-Version: foo' http://localhost:8080/version
# list
curl -sS http://localhost:8080/user | jq
# get
curl -v http://localhost:8080/user/e5c931fd-2ed0-4af7-bf17-a53d2d3daa66
# add
curl -sS http://localhost:8080/user -H 'Content-Type: application/json' --data '{"name":"foo","age":42}' | jq
# upload
curl -i -H "Content-Type: application/x-www-form-urlencoded" --data-binary "@README.md" http://localhost:8080/file/upload
# download (-J allows remote header filename)
curl -sS -J http://localhost:8080/file/download-archive -o my-archive.zip

# invokes api
./gradlew :modules:http:run-client --args="arg_user"
./gradlew :modules:http:run-client --args="arg_upload"
./gradlew :modules:http:run-client --args="arg_download"
```

OpenAPI, Swagger UI and Redoc
- see [Generate OpenAPI Specification](https://www.jetbrains.com/help/idea/ktor.html#openapi)
- first time only, with the `Application.kt` file open, select `Help > Find Action` and type `Generate OpenAPI documentation for Ktor in Module: kotlin-fun.modules.http.main`
- set `Update OpenAPI automatically` in `modules/http/src/main/resources/openapi/documentation.yaml`
- local Redoc demo
    ```bash
    # see http://localhost:8081
    docker run -it --rm -p 8081:80 \
      -v $(pwd)/modules/http/src/main/resources/openapi/documentation.yaml:/usr/share/nginx/html/swagger.yaml \
      -e SPEC_URL=swagger.yaml redocly/redoc
    ```
- live [Redocly](https://redocly.com/docs/redoc/) demo https://redocly.github.io/redoc/?url=https://raw.githubusercontent.com/niqdev/kotlin-fun/main/modules/http/src/main/resources/openapi/documentation.yaml

### Deployment

* [niqdev/kotlin-fun-http](https://hub.docker.com/r/niqdev/kotlin-fun-http) docker image updated on every push, see [ci-workflow](https://github.com/niqdev/kotlin-fun/blob/main/.github/workflows/ci.yml)

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

* PostgreSQL [Helm](https://helm.sh/docs) charts (StatefulSet)
  - [cetic/helm-postgresql](https://github.com/cetic/helm-postgresql) is ***obsolete*** and supports only v11
  - [bitnami/postgresql](https://github.com/bitnami/charts/tree/main/bitnami/postgresql) requires OCI registry config in argo, see [Deploy Helm OCI charts with ArgoCD](https://drake0103.medium.com/deploy-helm-oci-charts-with-argocd-583699c7d739)

```bash
# creates chart
mkdir -p helm-charts && helm create helm-charts/kotlin-fun-http

# verifies chart
helm template helm-charts/kotlin-fun-http --debug > tmp-app.yaml
```

* [argo-cd](https://github.com/hckops/kube-template/blob/main/applications/templates/examples/kotlin-fun.yaml) app deployed on Kubernetes

```bash
# port forward locally
kubectl --kubeconfig clusters/do-template-kubeconfig.yaml -n examples \
  port-forward svc/niqdev-kotlin-fun-http-main-v0-1-0 8888:8080

# verifies service
curl http://localhost:8888/status

# POSTGRES_PASSWORD=pgpassword
kubectl --kubeconfig clusters/do-template-kubeconfig.yaml -n examples \
  exec -it sts/kotlin-fun-database -c kotlin-fun-database -- \
  psql -h localhost -U postgres --password -p 5432 example_db

# show tables
\dt
```

***IMPORTANT: after stopping the cluster, don't forget to delete the Volumes Block Storage on DigitalOcean i.e. pvc***
