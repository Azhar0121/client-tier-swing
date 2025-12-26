# client-tier-swing
project 3-tier web socket
Studi kasus: Sistem Inventaris Barang Laboratorium 

Ringkasan arsitektur (3-tier)
- Klien (Client Tier): Aplikasi desktop Java (Swing) — antarmuka pengguna dan klien HTTP/WebSocket.
- Aplikasi (Application Tier): API REST berbasis PHP — menangani operasi CRUD dan logika bisnis (project terpisah).
- WebSocket (Realtime Tier): Server Node.js menggunakan pustaka `ws` — mengirim notifikasi realtime ke klien.
- Data (Data Tier): MySQL / MariaDB — penyimpanan data persisten.

Stack teknologi
- Aplikasi: PHP (REST API) di atas LAMP/LEMP.
- Realtime: Node.js + `ws` untuk komunikasi WebSocket.
- Database: MySQL atau MariaDB.

Cara kerja
- Klien melakukan permintaan HTTP ke API PHP untuk operasi Create/Read/Update/Delete.
- Setelah data berubah, API (atau backend) memberi tahu server WebSocket Node.js, yang kemudian menyiarkan notifikasi ke semua klien terhubung sehingga UI bisa memuat ulang data secara realtime.
