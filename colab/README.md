# Panduan Build APK & AAB Android Menggunakan Google Colab & Google Drive

Folder ini berisi skrip Jupyter Notebook (`build_apk.ipynb`) yang telah diperbarui untuk melakukan kompilasi / build **APK (Android Package)** dan **AAB (Android App Bundle)** dari file ZIP source code project ini di Google Colab cloud secara otomatis.

---

## 🛠️ Apa Bedanya APK dan AAB?
- **APK (`.apk`)**: Format standar yang dapat langsung diunduh dan dipasang (di-install) di smartphone Android Anda untuk uji coba.
- **AAB (`.aab`)**: Format resmi **Android App Bundle** yang dibutuhkan saat hendak mempublikasikan aplikasi ke **Google Play Store**.

---

## 🛠️ Langkah-Langkah Penggunaan di Google Colab

1. **Unduh Source Code Proyek (ZIP)**:
   - Unduh/export seluruh source code proyek ini sebagai file **ZIP** (misalnya beri nama `source_code.zip`).

2. **Upload File ZIP ke Google Drive**:
   - Buka [Google Drive](https://drive.google.com).
   - Unggah file `source_code.zip` langsung ke halaman utama **Drive Saya** (`My Drive`).

3. **Buka Notebook di Google Colab**:
   - Upload file `colab/build_apk.ipynb` dari folder proyek ini ke [Google Colab](https://colab.research.google.com) (Pilih opsi **Upload** -> pilih `build_apk.ipynb`).

4. **Jalankan Sel Berurutan di Google Colab**:
   - **Langkah 1**: Jalankan sel *Hubungkan ke Google Drive* (`drive.mount('/content/drive')`) dan izinkan akses Google Drive.
   - **Langkah 2**: Jalankan sel ekstraksi `source_code.zip` dari Google Drive ke Colab.
   - **Langkah 3**: Jalankan sel *Persiapan Environment Build*. Sel ini akan memasang Java 17, Gradle 9.3.1, dan Android SDK 34 secara otomatis (~1-2 menit).
   - **Langkah 4**: Jalankan sel *Proses Build APK & AAB*. Sel ini akan menjalankan kompilasi:
     `!/opt/gradle/gradle-9.3.1/bin/gradle assembleDebug bundleDebug`
   - **Langkah 5**: Kedua file hasil kompilasi secara otomatis akan dikirim dan disimpan langsung di Google Drive Anda:
     - `AplikasiZakatHutang-debug.apk` (Siap install di HP)
     - `AplikasiZakatHutang-debug.aab` (App Bundle untuk Play Store / Distribusi)

---

## 📱 Mengunduh Hasil Build
Setelah proses build selesai, buka [Google Drive](https://drive.google.com) Anda, cari file `AplikasiZakatHutang-debug.apk` (untuk install langsung di HP) atau `AplikasiZakatHutang-debug.aab`.
