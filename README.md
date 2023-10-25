# Java gRPC Playground

## Goals

1. [x] Implement gRPC TODO resource
2. [x] Implement OAuth 2.0 Authentification
~~3. [ ] Implement ALTS Authentification~~
4. [ ] Unit testing
5. [ ] Error Handling - Stub Mocking
6. [ ] Load Balancing
7. [ ] SSL/TLS
8. [ ] Implement uni- vs bi-directional
9. [ ] Implement sync- vs async-stubs
10. [ ] Implement streams
11. [ ] Implement Health
12. [ ] Logging, Monitoring, etc

# ALTS Authentication

The ALTS is a Google's proprietary application layer transport security protocol. 
The protocol is available on GCP, but applying it outside GCP is not straightforward.
The following error was returned:

```
WARNUNG: [Channel<5>: (metadata.google.internal.:8080)] Failed to resolve name.
status=Status{
  code=UNAVAILABLE,
  description=Unable to resolve host metadata.google.internal.,
  cause=java.lang.RuntimeException: java.net.UnknownHostException: metadata.google.internal.
```
