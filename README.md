# codex-test

This project demonstrates an example attribute-based access control (ABAC) system written in Kotlin. Policy evaluation is delegated to [Open Policy Agent](https://www.openpolicyagent.org/) (OPA). The Rego policy can be found in `opa/policy.rego` and uses group permissions defined in `opa/data.json`.

## Building

Use Gradle to build and run the tests:

```bash
gradle test
```

The tests illustrate how on-call engineers, leader approval and group permissions affect access decisions. An `opa` CLI must be available on your `PATH` for the tests to evaluate the Rego policy.
