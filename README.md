# Sky’s Coffee

**Alur pemesanan kedai kopi, dari menemukan menu hingga mencatat order.**

[English](README.en.md) · [Kode sumber](https://github.com/KairunHanjun/SkysCoffee) · [Apache 2.0](LICENSE)

Aplikasi web berbasis Java untuk mengeksplorasi katalog, akun pelanggan, keranjang, dan checkout. Proyek ini menjadi latihan menghubungkan pengalaman pelanggan dengan autentikasi dan pemrosesan data di server. Identitas visual serta aset Sky’s Coffee dipertahankan dari proyek awal.

## Fitur

- Penelusuran katalog dan detail produk kopi.
- Registrasi, login, dan profil pelanggan.
- Keranjang yang terpisah untuk setiap akun.
- Pengelolaan jumlah barang dan perhitungan pesanan.
- Pada demo lokal: checkout simulasi serta profil dan riwayat order.

## Teknologi

| Bagian | Teknologi |
| --- | --- |
| Backend | Java 21, Spring Boot 3.3.0 |
| Antarmuka | Thymeleaf, HTML, CSS, JavaScript |
| Akun & data | Spring Security, Spring Data JPA, MariaDB pada implementasi awal |
| Demo lokal | H2 di memori, katalog dan akun fiktif |
| Build | Gradle Wrapper, JUnit, MockMvc |

## Menjalankan source repository

Siapkan **JDK 21**, MariaDB, dan konfigurasi layanan yang digunakan implementasi awal. Dependensi integrasi Midtrans, email, dan OAuth tersedia di source; keberhasilan alur produksi tersebut belum diverifikasi.

```sh
git clone https://github.com/KairunHanjun/SkysCoffee.git
cd SkysCoffee
./gradlew bootRun
```

Pada Windows, gunakan `gradlew.bat`. Konfigurasi lokal dan environment variable perlu disesuaikan sebelum menjalankan. Jangan memasukkan kredensial ke README atau repository.

## Demo portofolio lokal

> Instruksi berikut mengikuti demo di workspace portofolio. Commit README/LICENSE ini tidak menyertakan perubahan implementasi demo yang masih lokal. Gunakan hanya pada checkout yang memiliki `start-demo.ps1` dan profil `application-demo.yml`.

```powershell
.\start-demo.ps1
# Port alternatif:
.\start-demo.ps1 -Port 8082
```

Dengan source demo yang sesuai, alternatif lintas platform:

```sh
./gradlew bootRun --args='--spring.profiles.active=demo --server.port=8081'
```

Buka **http://127.0.0.1:8081**. Hentikan dengan `Ctrl+C`. Launcher memakai JDK 21 portable dari `../.portfolio-tools` bila tersedia; selain itu atur `JAVA_HOME`.

| Username | Kata sandi demo | Kondisi awal |
| --- | --- | --- |
| `coffee.demo` | `DemoCoffee123!` | Keranjang Cappuccino dan Iced Vanilla Latte |
| `guest.demo` | `DemoCoffee123!` | Keranjang kosong |

Demo memakai **H2 di memori**. Akun, perubahan keranjang, dan order kembali ke kondisi awal setelah restart. MariaDB dan layanan cloud tidak diperlukan untuk mode ini.

### Alur yang tersedia pada demo

- Enam produk, pencarian, filter kategori, dan detail kopi.
- Login BCrypt serta registrasi lokal tanpa email/OTP.
- Maksimal 20 cup per produk; harga dan total dihitung di server.
- Biaya kirim contoh Rp10.000 hanya untuk keranjang berisi.
- Checkout POST dengan CSRF dan penanganan pengiriman ulang.
- Profil serta riwayat order yang dibatasi berdasarkan pemilik akun.

Semua order berstatus **SIMULATED / NOT PAID**. Demo tidak memproses pembayaran Midtrans, mengirim email, atau mengatur pengiriman barang.

## Build dan pengujian

```sh
./gradlew test bootJar
```

Laporan tes berada di `build/reports/tests/test/index.html`; hasil JAR di `build/libs/`. Pada pemeriksaan demo lokal, **19 tes lulus** dan build berhasil. Pemeriksaan tersebut mencakup autentikasi, keranjang, harga server, CSRF, checkout, pemisahan akun, registrasi, dan profil; bukan klaim hasil CI untuk source awal repository.

## Struktur ringkas

```text
src/main/java/         Controller, service, entity, dan konfigurasi
src/main/resources/    Template, aset visual, dan konfigurasi
src/test/              Pengujian aplikasi
gradle/                Gradle Wrapper
```

## Batas dan pengembangan berikutnya

Pembayaran nyata, OTP produksi, email, OAuth, dan konfigurasi MariaDB lama belum diverifikasi untuk deployment. Pengembangan berikutnya dapat berfokus pada sandbox pembayaran, notifikasi transaksi, dan penyimpanan persisten. Screenshot lokal berada di workspace terpisah `../portfolio-materials/screenshots/SkysCoffee`, bukan bagian dari clone repository ini.

Proyek awal dikembangkan mandiri oleh **Guntur Ahmad Lion Putranto**. Penyempurnaan demo dan dokumentasi portofolio dibantu Codex.

## Lisensi

Proyek menggunakan **Apache License 2.0** yang sudah tersedia pada [LICENSE](LICENSE). Lisensi asli dipertahankan. Dependensi, gambar, dan font pihak ketiga tetap mengikuti lisensinya masing-masing.
