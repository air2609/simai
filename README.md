# SIMAI — Sistem Informasi Manajemen Aset & Inventori

Aplikasi monorepo untuk manajemen aset (dilacak per unit) dan inventori (dilacak per kuantitas/SKU).

## Tech Stack

| Layer    | Teknologi                                  |
|----------|--------------------------------------------|
| Backend  | Spring Boot 3.5.x, Java 25, Maven          |
| Database | PostgreSQL 16 (migrasi via Flyway)         |
| Frontend | Vue 3 (Vite), Vue Router, Pinia, Axios, Element Plus |

## Struktur Project

```
simai/
├── README.md
├── docker-compose.yml          # PostgreSQL (+ pgAdmin) untuk development
├── backend/                    # Spring Boot 3.5 + Java 25
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/simai/
│       │   ├── SimaiApplication.java
│       │   ├── config/         # Security, OpenAPI, DataInitializer
│       │   ├── security/       # JWT (filter, service, user details)
│       │   ├── common/         # ApiResponse, exception handler, util
│       │   ├── domain/         # JPA entities + enums
│       │   ├── repository/     # Spring Data JPA repositories
│       │   ├── service/        # Business logic
│       │   ├── dto/            # Request/response DTO
│       │   └── controller/     # REST controllers
│       └── resources/
│           ├── application.yml
│           └── db/migration/   # Flyway SQL migrations
└── frontend/                   # Vue 3 + Vite
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── api/                # Axios client + modul API
        ├── router/             # Vue Router
        ├── stores/             # Pinia stores
        ├── layouts/            # Layout aplikasi
        ├── components/         # Komponen bersama
        └── views/              # Halaman (dashboard, master, asset, inventory, stock opname, report)
```

## Menjalankan Aplikasi

### 1. Database

```bash
docker compose up -d
```

Membuat PostgreSQL di `localhost:5432`, database `simai`, user/password `simai`.

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run      # atau: mvn spring-boot:run
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Default user seed: `admin@simai.local` / `admin123` (ubah setelah login pertama)

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

- Web: http://localhost:5173
- Proxy `/api` diarahkan ke backend `http://localhost:8080`

## API Master Data (CRUD)

Semua endpoint diawali `/api` dan butuh `Authorization: Bearer <token>` (kecuali login/health).
Mutasi data (POST/PUT/DELETE) hanya untuk role `ADMIN`; `users` seluruhnya khusus `ADMIN`.

| Resource     | Endpoint                    |
|--------------|-----------------------------|
| Auth         | `POST /api/auth/login`      |
| Departments  | `GET/POST /api/departments`, `GET/PUT/DELETE /api/departments/{id}` |
| Locations    | `GET/POST /api/locations`, `GET/PUT/DELETE /api/locations/{id}`    |
| Vendors      | `GET/POST /api/vendors`, `GET/PUT/DELETE /api/vendors/{id}`        |
| Categories   | `GET/POST /api/categories`, `GET/PUT/DELETE /api/categories/{id}` (filter `?type=ASSET|INVENTORY`) |
| Users        | `GET/POST /api/users`, `GET/PUT/DELETE /api/users/{id}`            |

Setiap create/update/delete otomatis tercatat di tabel `audit_logs` (nilai sebelum & sesudah).

## Modul & Fitur (sesuai requirement.md)

1. **Master Data** — users, departments, locations, vendors, categories.
2. **Siklus Hidup Aset** — receive, assign, transfer, loan/return, maintenance, disposal (riwayat di `asset_movements`).
3. **Manajemen Inventori** — inbound/outbound/transfer, stock opname, low stock alert.
4. **RBAC & Audit Trail** — role admin/operator/approver/viewer, log aktivitas (`audit_logs`).
5. **Laporan & Dashboard** — nilai aset, aset per status/lokasi, stok menipis, riwayat pergerakan.

## Catatan Penting

- **Jangan ubah `inventory_balances` secara langsung.** Selalu catat `stock_movements` dalam satu transaksi database (atomic) agar saldo dan riwayat tetap sinkron.
- Skema database dikelola Flyway (`backend/src/main/resources/db/migration`), bukan `hibernate.ddl-auto`.
- Money memakai `NUMERIC(19,2)`, quantity memakai `NUMERIC(19,4)` — hindari floating point untuk uang/stok pecahan.
