# Sky’s Coffee

**A coffee shop ordering journey, from discovering a menu to recording an order.**

[Bahasa Indonesia](README.md) · [Source](https://github.com/KairunHanjun/SkysCoffee) · [Apache 2.0](LICENSE)

A Java web application exploring product browsing, customer accounts, shopping carts, and checkout. The project connects customer-facing pages with authentication and server-side processing, while retaining the original Sky’s Coffee visual identity and assets.

## Features

- Coffee catalog and product details.
- Registration, login, and customer profiles.
- Account-specific carts and quantity management.
- Order calculations and checkout flows.
- In the local portfolio demo: simulated checkout and order history.

## Stack

Java 21, Spring Boot 3.3.0, Thymeleaf, Spring Security, Spring Data JPA, MariaDB in the original implementation, Gradle Wrapper, and JUnit/MockMvc. The separate portfolio demo uses in-memory H2 and fictional data.

## Run the repository source

Prepare **JDK 21**, MariaDB, and configuration for the original application's services. Midtrans, email, and OAuth integrations are present in source but have not been production-verified.

```sh
git clone https://github.com/KairunHanjun/SkysCoffee.git
cd SkysCoffee
./gradlew bootRun
```

On Windows, use `gradlew.bat`. Supply environment-specific configuration first and keep credentials out of documentation and version control.

## Local portfolio demo

> These commands describe the demo in the portfolio workspace. This README/LICENSE update does not include implementation changes that remain local. Use a checkout containing `start-demo.ps1` and `application-demo.yml`.

```powershell
.\start-demo.ps1
.\start-demo.ps1 -Port 8082
```

With the matching demo source available:

```sh
./gradlew bootRun --args='--spring.profiles.active=demo --server.port=8081'
```

Open **http://127.0.0.1:8081**, stop with `Ctrl+C`, and use JDK 21. The launcher selects the sibling portable JDK when available, otherwise `JAVA_HOME`.

| Username | Demo password | Initial cart |
| --- | --- | --- |
| `coffee.demo` | `DemoCoffee123!` | Cappuccino and Iced Vanilla Latte |
| `guest.demo` | `DemoCoffee123!` | Empty |

The **in-memory H2 database** resets accounts, carts, and orders on restart. This mode does not require MariaDB or cloud services.

### Demonstrated workflows

- Six products, search, category filters, and product details.
- BCrypt login and local registration without email or OTP.
- Up to 20 cups per product; server-side pricing and totals.
- A sample Rp10,000 delivery fee for non-empty carts.
- CSRF-protected POST checkout and duplicate-submission handling.
- Profiles and order history restricted to the owning account.

Orders are **SIMULATED / NOT PAID**. The demo does not process payments, send email, or arrange deliveries.

## Build and tests

```sh
./gradlew test bootJar
```

Test reports: `build/reports/tests/test/index.html`. JAR output: `build/libs/`. Local portfolio verification recorded **19 passing tests** and a successful build, covering accounts, carts, prices, CSRF, checkout, and profiles. These are results for the local demo implementation, not a CI claim for the repository's original source.

## Structure

```text
src/main/java/         Controllers, services, entities, configuration
src/main/resources/   Templates, visual assets, application configuration
src/test/             Application tests
gradle/               Gradle Wrapper
```

## Scope and next steps

Real payments, production OTP, email, OAuth, and legacy MariaDB deployment remain unverified. Future work can focus on payment sandbox integration, transaction notifications, and persistent storage. Screenshots live separately at `../portfolio-materials/screenshots/SkysCoffee` in the portfolio workspace; they are not part of this repository clone.

Originally developed independently by **Guntur Ahmad Lion Putranto**. Codex assisted with subsequent demo refinements and portfolio documentation.

## License

The existing **Apache License 2.0** is preserved; see [LICENSE](LICENSE). Third-party dependencies, images, and fonts retain their respective licenses.
