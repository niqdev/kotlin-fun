## http

### Development

* [Ktor](https://ktor.io/docs)
* [Jdbi](https://jdbi.org)
* [Flyway](https://documentation.red-gate.com/fd)
* [Testcontainers](https://java.testcontainers.org)

TODO
* bruno
* http-client

```bash
./gradlew :modules:http:test

# starts dependencies
docker-compose -f local/docker-compose-http.yml up -d
docker-compose -f local/docker-compose-http.yml logs --follow
docker-compose -f local/docker-compose-http.yml down -v

# starts server
./gradlew :modules:http:run

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

* [Ktor/Docker](https://ktor.io/docs/docker.html)

```bash
# publish local image
./gradlew :modules:http:publishImageToLocalRegistry

# publish remote image
./gradlew :modules:http:publishImage
```

TODO
* chart
