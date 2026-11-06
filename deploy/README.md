# Public demo deployment

Use branch `demo` only. Build locally with the required JDK using `./gradlew --no-daemon --max-workers=1 test bootJar`. Copy the executable JAR to a clean build context as `app.jar` alongside `deploy/Dockerfile`. No .env or production database is used.

Runtime uses the `demo` profile, synthetic H2 in-memory data, UID 10002, read-only root filesystem, limited JVM heap and Docker memory/CPU limits. Restart resets all shared dummy changes. Concurrent visitors using the same demo account share its state. Do not enter personal data.

The owning deployment configuration and operational commands are in KairunHanjun/Astrofy `deploy/demos/`. Host ports bind only to 127.0.0.1; HTTPS is provided by the existing Cloudflare Tunnel through nginx. Containers restart unless stopped, and Docker starts through Alpine OpenRC.
