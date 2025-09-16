# Qubership Integration Platform - Catalog Library

This component is phased out. The only supported branch for now are: v0.1 as it was already released.
All important code moved into [runtime catalog repository](https://github.com/Netcracker/qubership-integration-runtime-catalog) to reduce complexity of solution. ([design time catalog](https://github.com/Netcracker/qubership-integration-designtime-catalog) code that used this library also moved to [runtime catalog repository](https://github.com/Netcracker/qubership-integration-runtime-catalog)).

Contains model, configuration, and business logic classes that are shared between [runtime](https://github.com/Netcracker/qubership-integration-runtime-catalog) and [design-time](https://github.com/Netcracker/qubership-integration-designtime-catalog) catalog services of Qubership Integration Platform.

## Installation

Catalog Library is an ordinary Maven jar package. It requires Maven and Java 21 for build.

## Contribution

For the details on contribution, see [Contribution Guide](CONTRIBUTING.md). For details on reporting of security issues see [Security Reporting Process](SECURITY.md).

The library uses [Checkstyle](https://checkstyle.org/) via [Maven Checkstyle Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/) to ensure code style consistency among Qubership Integration Platform's libraries and services. The rules are located in a separate [repository](https://github.com/Netcracker/qubership-integration-checkstyle).

Commits and pool requests should follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) strategy.

## Licensing

This software is licensed under Apache License Version 2.0. License text is located in [LICENSE](LICENSE) file.

## Additional Resources

- [Qubership Integration Platform](https://github.com/Netcracker/qubership-integration-platform) — сore deployment guide.
