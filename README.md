# lib-sepa
A JVM client library for the Single Euro Payments Area (SEPA) protocol to simplify the actual process for cashless euro payments – via credit transfer and direct debit – to any service that implements the SEPA protocol.

## Testing

Run the unit tests with:

```
./gradlew test
```

The suite includes a JAXB marshalling test (`SepaCreditTransferTest`) that generates a `pain.001.001.03`
document and asserts on its contents. This exercises the full `jakarta.xml.bind` marshalling path, guarding
against namespace regressions: the generated model classes (produced by XJC with `useJakarta = true`) and the
`org.glassfish.jaxb:jaxb-runtime` implementation must both use the `jakarta.xml.bind` namespace so the library
imports cleanly into a Spring Boot 3.x application.

