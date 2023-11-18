## http

* [Ktor](https://ktor.io/docs)

> TODO json + client + docker + chart

```bash
# starts server
./gradlew :modules:http:run

curl http://localhost:8080/status
curl -v http://localhost:8080/user -H 'Content-Type: application/json' --data '{"name":"foo"}'
```
