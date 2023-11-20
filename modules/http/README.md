## http

* [Ktor](https://ktor.io/docs)

TODO
* repositories (SQLite)
* test repositories with https://testcontainers.com
* local docker-compose db
* client
* docker
* chart

```bash
./gradlew :modules:http:test

# starts server
./gradlew :modules:http:run

curl http://localhost:8080/status
# version
curl -H 'X-My-Version: foo' http://localhost:8080/version
# list
curl -sS http://localhost:8080/user | jq
# get
curl -sS http://localhost:8080/user/e5c931fd-2ed0-4af7-bf17-a53d2d3daa66 | jq
# add
curl -v http://localhost:8080/user -H 'Content-Type: application/json' --data '{"name":"foo","age":42}'
```
