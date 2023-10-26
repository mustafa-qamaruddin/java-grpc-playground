# Java gRPC Playground

## Goals

1. [x] Implement gRPC TODO resource
2. [x] Implement OAuth 2.0 Authentification

~~3. [ ] Implement ALTS Authentification~~

4. [x] Unit testing - Stub Mocking
5. [x] Error Handling
6. [ ] Load Balancing
7. [ ] SSL/TLS
8. [ ] Implement uni- vs bi-directional
9. [ ] Implement sync- vs async-stubs
10. [ ] Implement streams with error handling
11. [ ] Implement Health
12. [ ] Logging, Monitoring, etc

## ALTS Authentication

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

## Error Handling

### Don't Use Trailers for Errors

You could use a similar approach (put error details in trailing response metadata) if you’re not using protocol buffers, 
but you’d likely need to find or develop library support for accessing this data in order to make practical use of it in 
your APIs. There are important considerations to be aware of when deciding whether to use such an extended error model, 
however, including:

1. Library implementations of the extended error model may not be consistent across languages in terms of requirements 
for and expectations of the error details payload
2. Existing proxies, loggers, and other standard HTTP request processors don’t have visibility into the error details 
and thus wouldn’t be able to leverage them for monitoring or other purposes
3. Additional error detail in the trailers interferes with head-of-line blocking, and will decrease HTTP/2 header 
compression efficiency due to more frequent cache misses
4. Larger error detail payloads may run into protocol limits (like max headers size), effectively losing the original 
error

### Error Messages Localization

Wenn Sie eine Fehlermeldung für Nutzer benötigen, verwenden Sie google.rpc.LocalizedMessage als Detailfeld. Das 
Nachrichtenfeld in google.rpc.LocalizedMessage kann lokalisiert werden. Achten Sie darauf, dass das Nachrichtenfeld in 
google.rpc.Status auf Englisch ist.

-- [Google Error Model](https://cloud.google.com/apis/design/errors?hl=de#error_model)

### io.grpc.Status vs com.google.rpc.Status

´io.grpc.Status´ is specific to the gRPC framework and deals with RPC-specific status information, while 
´com.google.rpc.Status´ is used in Google APIs and offers a more general format for representing API operation outcomes,
including gRPC-based APIs used in Google Cloud services.

A custom error model could be introduced to contain the necessary fields as per the application requirements. In this 
prototype, the ´com.google.rpc.Status´ provides off-the-shelf conversion to and from ´io.grpc.Status´. Besides, the 
ability to provide additional informations of `Any` type.

-- [Google Error Model](https://cloud.google.com/apis/design/errors?hl=de#error_model)

### Error Codes Extension

Eine Verarbeitung von durchschnittlich drei Fehlercodes pro API-Aufruf würde bedeuten, dass die meisten Anwendungslogik 
nur für die Fehlerbehandlung vorgesehen ist, was für Entwickler keine gute Erfahrung wäre.

-- [Google Error Model](https://cloud.google.com/apis/design/errors?hl=de#error_model)

## Unit Testing

Mocking the client stub provides a false sense of security when writing tests. Mocking stubs and responses allows for 
tests that don't map to reality, causing the tests to pass, but the system-under-test to fail. The gRPC client library 
is complicated, and accurately reproducing that complexity with mocks is very hard. You will be better off and write 
less code by using InProcessTransport instead. 
-- [gRPC Unit Test](https://github.com/grpc/grpc-java/blob/master/examples/README.md#unit-test-examples)


TODO how did the test pass the interceptor for authentication?
