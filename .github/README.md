# 🚀 Build APK & AAB Otomatis dengan GitHub Actions

Proyek ini telah dilengkapi dengan workflow **GitHub Actions** (`.github/workflows/android_build.yml`) untuk membuat file **APK** dan **AAB (App Bundle)** secara otomatis di cloud GitHub tanpa memerlukan komputer berspesifikasi tinggi atau Android Studio di lokal.

---

## 🛠️ Cara Kerja & Pemicu (Triggers)

Workflow akan berjalan secara otomatis saat:
1. **Push Code**: Setiap kali Anda melakukan `git push` ke branch `main` atau `master`.
2. **Pull Request**: Ketika ada PR yang dibuat ke branch `main` atau `master`.
3. **Manual (Workflow Dispatch)**: Anda bisa memicu proses build kapan saja dari tombol menu **Actions** di repository GitHub Anda.

---

## 📖 Cara Menggunakan & Mengunduh Hasil Build (APK / AAB)

1. **Push/Upload Kode ke Repository GitHub Anda**:
   - Pastikan seluruh file proyek ini (termasuk folder `.github/workflows/`) sudah di-commit dan di-push ke repository GitHub Anda.

2. **Buka Tab "Actions" di GitHub**:
   - Masuk ke repository GitHub Anda.
   - Klik tab **Actions** di bagian atas menu repository.
   - Pilih workflow **Build Android APK & AAB**.

3. **Jalankan Manual (Opsional)**:
   - Klik tombol **Run workflow** -> pilih branch (misal: `main`) -> Klik **Run workflow**.

4. **Unduh APK & AAB (Artifacts)**:
   - Setelah proses kompilasi selesai (ditandai dengan centang hijau `Build APK and AAB`), klik nama alur kerja tersebut.
   - Gulir layar ke bagian paling bawah di bawah judul **Artifacts**.
   - Anda akan menemukan file unduhan:
     - 📱 `AplikasiZakatHutang-Debug-APK` (Siap diunduh dan dipasang di HP Android).
     - 📦 `AplikasiZakatHutang-Debug-AAB` (App Bundle resmi untuk Google Play Store).
