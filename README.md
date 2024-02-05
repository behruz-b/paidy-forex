## Forex proxy for getting Currency Exchange Rates

#### Prepare project to run
*   Override configurations for Forex API (One Frame). [Paidy One Frame](https://hub.docker.com/r/paidyinc/one-frame) image can be run locally
```scala
app {
  one-frame {
    uri: "http://localhost:8090/rates"
    token: "10dc303535874aeccc86a8251e6992f5"
  }
}
```
*   Override configurations for Redis URI (for proxy requests). Cache is 5 min.
```scala
app {
  redis {
    uri: "redis://127.0.0.1"
    prefix: "paidy-forex"
  }
}
```
*   Override configurations for Project host/port (default will be used 0.0.0.0:8080)
```scala
app {
  http {
    host: "0.0.0.0"
    port: "8080"
  }
}
```
* Run project
```
sbt run
```

#### Architecture overview
<img src="/overview.png?raw=true" width=700>
