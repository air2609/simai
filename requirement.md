# sistem informasi management asset dan inventory

minmal fitur-fitur yang harus ada :

## 1. Master Data (Fondasi Sistem)
Data Aset: Kode aset unik/QR/barcode, nama aset, kategori, merek/model, nomor seri, tanggal perolehan, nilai perolehan, umur manfaat, metode penyusutan, status (aktif, rusak, dalam perbaikan, dipinjam, dihapusbukukan), serta lokasi dan penanggung jawab (custodian).

Data Inventori/Item: SKU/kode barang, nama item, kategori, satuan (pcs, box, liter, meter), stok minimum (reorder point), lokasi gudang/rak, serta status batch/kedaluwarsa (jika relevan).

Data Pendukung: Lokasi/cabang/ruangan, departemen/unit kerja, pengguna/karyawan, dan data vendor/pemasok.

## 2. Siklus Hidup Aset (Asset Lifecycle)
Pengadaan & Penerimaan (Receiving): Pencatatan barang masuk dari PO/vendor, pengecekan fisik, dan generate label/barcode/QR aset.

Penempatan & Mutasi (Assignment & Transfer): Pencatatan serah terima aset ke individu/divisi serta perpindahan lokasi antar-ruangan/cabang dengan riwayat perpindahan yang jelas.

Peminjaman & Pengembalian: Alur pengajuan pinjam, persetujuan, tanggal jatuh tempo, dan pengecekan kondisi saat dikembalikan.

Perawatan & Perbaikan (Maintenance): Jadwal preventive maintenance, tiket kerusakan/perbaikan, biaya servis, dan riwayat teknisi/vendor.

Penghapusan / Pelepasan (Disposal / Write-off): Proses jual, hibah, lelang, atau pemusnahan aset rusak/usang lengkap dengan berita acara dan persetujuan.

## 3. Manajemen Inventori & Gudang
Transaksi Stok: Stok masuk (inbound), stok keluar/pemakaian (outbound), dan transfer antar-gudang.

Stock Opname / Audit Fisik: Pencocokan stok sistem dengan fisik di lapangan, pencatatan selisih (variance), dan penyesuaian stok (adjustment) yang terotorisasi.

Peringatan Stok Minimum (Low Stock Alert): Notifikasi otomatis saat jumlah barang menyentuh batas minimum agar pengadaan tidak terlambat.

## 4. Hak Akses, Keamanan & Audit Trail
Role-Based Access Control (RBAC): Pembagian peran yang jelas, misalnya Admin/Asset Manager, Staff Gudang/Operator, Approver/Manajer, dan Auditor/Viewer.

Log Aktivitas / Audit Trail: Rekaman siapa melakukan apa, kapan, dan nilai sebelum–sesudah perubahan data (sangat krusial untuk akuntabilitas dan audit).

Workflow Persetujuan (Approval): Alur persetujuan berjenjang untuk mutasi aset, penghapusan aset, atau penyesuaian stok besar.

## 5. Pelaporan & Integrasi
Laporan & Dashboard: Ringkasan total nilai aset, aset per lokasi/penanggung jawab, jadwal perawatan jatuh tempo, status stok menipis, pergerakan barang, dan rekap penyusutan.

Pencarian & Pemindaian: Pencarian cepat berdasarkan kode, seri, atau pengguna, serta dukungan scan QR/barcode (misalnya lewat kamera HP atau scanner).

Integrasi (Opsional sesuai kebutuhan): Koneksi ke sistem akuntansi/keuangan, HRIS (untuk sinkronisasi data karyawan & mutasi pegawai), atau modul procurement.

Saran Praktis Sebelum Mulai Koding
Rapikan aturan bisnis dulu: Pastikan definisi mana yang masuk "aset" dan mana yang "inventori" disepakati bersama tim operasional dan keuangan.

Mulai dari inti (MVP): Bangun dulu master data, transaksi masuk/keluar/mutasi, audit trail, dan laporan dasar sebelum menambah fitur lanjutan seperti notifikasi otomatis atau integrasi eksternal.

Desain label & kode sejak awal: Format kode aset/SKU yang konsisten akan sangat memudahkan saat stock opname dan pelacakan jangka panjang.


# Design Database

## A. Master & Referensi
users: id, name, email, password_hash, role (admin, operator, approver, viewer), department_id, is_active, created_at

departments: id, code, name

locations: id, code, name, parent_id (mendukung hierarki gedung/lantai/ruangan/rak), address

vendors: id, code, name, phone, email, address

categories: id, code, name, type (asset | inventory), useful_life_months (opsional untuk aset)

## B. Modul Aset (Dilacak per Unit)
assets:

id, asset_code (unik, misal AST-2026-0001), name, category_id, vendor_id

brand, model, serial_number

purchase_date, purchase_price (gunakan tipe desimal/numerik presisi, hindari floating point untuk uang)

useful_life_months, salvage_value

status (available, assigned, maintenance, disposed)

current_location_id, custodian_user_id (penanggung jawab saat ini)

notes, created_at, updated_at

asset_movements (riwayat mutasi, peminjaman, & perubahan status aset):

id, asset_id, movement_type (receive, assign, transfer, loan, return, maintenance_in, maintenance_out, dispose)

from_location_id, to_location_id

from_user_id, to_user_id

reference_no (nomor surat jalan/berita acara/tiket), notes, moved_by_user_id, moved_at

asset_maintenances (opsional di MVP tahap akhir, atau masuk v1.1):

id, asset_id, vendor_id, maintenance_date, cost, description, status

## C. Modul Inventori (Dilacak per Kuantitas / SKU)
items (master SKU barang habis pakai / suku cadang):

id, sku (unik, misal ITM-0001), name, category_id, unit (pcs, box, unit, liter)

min_stock (batas stok minimum), default_location_id, is_active, created_at, updated_at

inventory_balances (saldo stok per item & lokasi agar query stok cepat):

id, item_id, location_id, quantity (gunakan tipe desimal/numerik jika ada satuan pecahan, atau integer jika selalu bulat)

updated_at (unique constraint pada (item_id, location_id))

stock_movements (buku besar transaksi stok masuk/keluar/transfer/opname):

id, item_id, location_id, movement_type (inbound, outbound, transfer_in, transfer_out, opname_adjustment)

quantity_delta (positif untuk menambah stok, negatif untuk mengurangi stok)

unit_cost (opsional untuk estimasi nilai stok), reference_no, notes

created_by_user_id, created_at

## D. Audit Trail & Stock Opname
stock_opnames & stock_opname_items:

stock_opnames: id, opname_code, location_id, opname_date, status (draft, submitted, approved), created_by_user_id, approved_by_user_id

stock_opname_items: id, stock_opname_id, item_id, system_qty, physical_qty, variance_qty, notes

audit_logs (catatan perubahan data penting):

id, user_id, entity_type (asset, item, stock_opname, dll.), entity_id, action (create, update, status_change, approve), old_values (JSON/teks), new_values (JSON/teks), created_at

Catatan desain penting: Jangan ubah inventory_balances secara langsung tanpa mencatat stock_movements di dalam satu database transaction (BEGIN ... COMMIT / ROLLBACK). Jika satu transaksi gagal, saldo dan riwayat tetap sinkron.

# Alur Fitur MVP (Prioritas Utama)
Untuk rilis awal yang siap dipakai operasional harian, cukup fokus pada 6 alur utama ini:

## 1. Setup Master Data & Hak Akses

Admin mengisi master departments, locations, vendors, categories, dan akun users beserta role-nya.

## 2. Pencatatan Aset Masuk & Penempatan

Operator/Admin input aset baru (assets) dengan kode unik & nomor seri, lalu sistem mencatat histori awal di asset_movements (receive).

Saat aset diserahkan ke pegawai atau dipindah ruangan, sistem memperbarui custodian_user_id / current_location_id / status pada assets dan menambah baris baru di asset_movements (assign atau transfer).

## 3. Transaksi Stok Inventori (Masuk & Keluar)

Stok Masuk (inbound): Barang datang dari vendor → tambah baris di stock_movements (quantity_delta > 0) → tambah inventory_balances.

Stok Keluar (outbound): Permintaan/pemakaian barang oleh divisi/user → validasi stok cukup → kurangi inventory_balances dan catat di stock_movements (quantity_delta < 0).

## 4. Stock Opname Sederhana

Buat sesi opname per lokasi (draft), isi jumlah fisik (physical_qty), sistem menghitung selisih (variance_qty = physical_qty - system_qty).

Setelah disetujui (approved), sistem otomatis mencatat penyesuaian di stock_movements (opname_adjustment) dan memperbarui inventory_balances.

## 5. Pencarian & Filter Cepat

Cari aset berdasarkan kode aset, nomor seri, nama, lokasi, atau penanggung jawab.

Cari item inventori berdasarkan SKU, nama, atau filter barang yang quantity <= min_stock.

## 6. Dashboard & Laporan Inti

Ringkasan jumlah aset per status, total nilai perolehan aset, daftar item stok menipis, serta histori pergerakan aset dan stok per periode (bisa diekspor ke CSV/Excel).

# Urutan Pengerjaan 
Buat migrasi tabel master + assets + asset_movements + items + inventory_balances + stock_movements + audit_logs.

Bangun CRUD master data dan autentikasi + role.

Bangun transaksi aset (input & mutasi) serta transaksi inventori (masuk & keluar) dengan transaction lock/atomic update.

Tambahkan stock opname, laporan/ekspor, dan audit log.