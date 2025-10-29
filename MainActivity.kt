package com.adel.pertemuan4

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.adel.pertemuan4.AppExecutor
import com.adel.pertemuan4.DataDao
import com.adel.pertemuan4.DatabaseData
import com.adel.pertemuan4.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbData: DatabaseData
    private lateinit var dataDao: DataDao
    private lateinit var appExecutors: AppExecutor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Database dan Executor
        appExecutors = AppExecutor()
        dbData = DatabaseData.getDatabase(applicationContext)
        dataDao = dbData.DataDao()

        // Setup Spinner Status Pernikahan
        setupSpinner()

        // Setup Tombol Klik
        setupClickListeners()

        // Mengamati perubahan data di database
        observeData()
    }

    private fun setupSpinner() {
        val statusAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.status_pernikahan_array,
            android.R.layout.simple_spinner_item
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = statusAdapter
    }

    private fun setupClickListeners() {
        binding.btnSimpan.setOnClickListener {
            simpanData()
        }

        binding.btnReset.setOnClickListener {
            resetData()
        }
    }

    private fun simpanData() {
        val nama = binding.etNama.text.toString().trim()
        val nik = binding.etNik.text.toString().trim()
        val kabupaten = binding.etKabupaten.text.toString().trim()
        val kecamatan = binding.etKecamatan.text.toString().trim()
        val desa = binding.etDesa.text.toString().trim()
        val rt = binding.etRt.text.toString().trim()
        val rw = binding.etRw.text.toString().trim()

        val selectedKelaminId = binding.rgJenisKelamin.checkedRadioButtonId
        val statusPernikahan = binding.spinnerStatus.selectedItem.toString()

        // Validasi input
        if (nama.isEmpty() || nik.isEmpty() || kabupaten.isEmpty() || kecamatan.isEmpty() ||
            desa.isEmpty() || rt.isEmpty() || rw.isEmpty() || selectedKelaminId == -1) {
            Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val jenisKelamin = findViewById<RadioButton>(selectedKelaminId).text.toString()

        // Buat objek Data
        val newData = Data(
            nama = nama,
            nik = nik,
            kabupaten = kabupaten,
            kecamatan = kecamatan,
            desa = desa,
            rt = rt,
            rw = rw,
            jenisKelamin = jenisKelamin,
            statusPernikahan = statusPernikahan
        )

        // Simpan ke database di thread terpisah
        appExecutors.diskIO.execute {
            dataDao.insert(newData)
            // Tampilkan toast di main thread
            appExecutors.mainThread.execute {
                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
        }
    }

    private fun resetData() {
        // Hapus semua data di thread terpisah
        appExecutors.diskIO.execute {
            dataDao.deleteAll()
            // Tampilkan toast di main thread
            appExecutors.mainThread.execute {
                Toast.makeText(this, "Semua data berhasil dihapus!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeData() {
        // Amati LiveData
        dataDao.getAllData().observe(this, Observer { listData ->
            if (listData.isEmpty()) {
                binding.tvDaftarWarga.text = "Belum ada data warga yang tersimpan."
            } else {
                // Format data untuk ditampilkan di TextView
                val stringBuilder = StringBuilder()
                listData.forEachIndexed { index, data ->
                    stringBuilder.append("${index + 1}. ${data.nama} (${data.jenisKelamin}) - ${data.statusPernikahan}\n")
                    stringBuilder.append("   NIK: ${data.nik}\n")
                    stringBuilder.append("   Alamat: RT ${data.rt}/RW ${data.rw}, ${data.desa}, ${data.kecamatan}, ${data.kabupaten}\n\n")
                }
                binding.tvDaftarWarga.text = stringBuilder.toString()
            }
        })
    }

    private fun clearForm() {
        binding.etNama.text?.clear()
        binding.etNik.text?.clear()
        binding.etKabupaten.text?.clear()
        binding.etKecamatan.text?.clear()
        binding.etDesa.text?.clear()
        binding.etRt.text?.clear()
        binding.etRw.text?.clear()
        binding.rgJenisKelamin.clearCheck()
        binding.spinnerStatus.setSelection(0)
    }
}
