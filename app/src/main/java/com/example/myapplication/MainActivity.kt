package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tableLayout: TableLayout
    private lateinit var edtTenSach: EditText
    private lateinit var edtTacGia: EditText
    private lateinit var edtNamXuatBan: EditText
    private lateinit var spinnerPhanLoai: Spinner
    private lateinit var btnThem: Button
    private lateinit var btnThongKe: Button
    private lateinit var btnTimKiem2023: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupDatabase()
        setupSpinner()
        setupButtons()
        loadBooksToTable()
    }

    private fun initViews() {
        tableLayout = findViewById(R.id.tableLayout)
        edtTenSach = findViewById(R.id.edtTenSach)
        edtTacGia = findViewById(R.id.edtTacGia)
        edtNamXuatBan = findViewById(R.id.edtNamXuatBan)
        spinnerPhanLoai = findViewById(R.id.spinnerPhanLoai)
        btnThem = findViewById(R.id.btnThem)
        btnThongKe = findViewById(R.id.btnThongKe)
        btnTimKiem2023 = findViewById(R.id.btnTimKiem2023)
    }

    private fun setupDatabase() {
        dbHelper = DatabaseHelper(this)

        // Thêm dữ liệu phân loại mẫu
        val phanLoaiList = listOf(
            PhanLoai(1, "Văn học", "Sách văn học Việt Nam và thế giới"),
            PhanLoai(2, "Khoa học", "Sách khoa học tự nhiên"),
            PhanLoai(3, "Lịch sử", "Sách lịch sử Việt Nam và thế giới"),
            PhanLoai(4, "Công nghệ", "Sách về công nghệ thông tin"),
            PhanLoai(5, "Giáo dục", "Sách giáo khoa và tham khảo")
        )

        phanLoaiList.forEach { phanLoai ->
            dbHelper.insertPhanLoai(phanLoai)
        }

        // Thêm dữ liệu sách mẫu
        val sachList = listOf(
            Sach(1, "Tôi thấy hoa vàng trên cỏ xanh", "Nguyễn Nhật Ánh", 2010, 1),
            Sach(2, "Lập trình Android", "Trần Văn A", 2023, 4),
            Sach(3, "Lịch sử Việt Nam", "Nguyễn Văn B", 2022, 3),
            Sach(4, "Vật lý đại cương", "Lê Thị C", 2023, 2),
            Sach(5, "Toán học cao cấp", "Phạm Văn D", 2021, 5)
        )

        sachList.forEach { sach ->
            dbHelper.insertSach(sach)
        }
    }

    private fun setupSpinner() {
        val phanLoaiList = dbHelper.getAllPhanLoai()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            phanLoaiList.map { "${it.maPhanLoai} - ${it.tenPhanLoai}" })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPhanLoai.adapter = adapter
    }

    private fun setupButtons() {
        btnThem.setOnClickListener {
            themSach()
        }

        btnThongKe.setOnClickListener {
            thongKeSach()
        }

        btnTimKiem2023.setOnClickListener {
            timKiemSach2023()
        }
    }

    private fun themSach() {
        val tenSach = edtTenSach.text.toString()
        val tacGia = edtTacGia.text.toString()
        val namXuatBan = edtNamXuatBan.text.toString().toIntOrNull() ?: 0
        val selectedPosition = spinnerPhanLoai.selectedItemPosition
        val phanLoaiList = dbHelper.getAllPhanLoai()

        if (tenSach.isNotEmpty() && tacGia.isNotEmpty() && namXuatBan > 0 && selectedPosition >= 0) {
            val maPhanLoai = phanLoaiList[selectedPosition].maPhanLoai
            val newSach = Sach(0, tenSach, tacGia, namXuatBan, maPhanLoai)

            val result = dbHelper.insertSach(newSach)
            if (result > 0) {
                Toast.makeText(this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show()
                clearInputs()
                loadBooksToTable()
            } else {
                Toast.makeText(this, "Thêm sách thất bại!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputs() {
        edtTenSach.text.clear()
        edtTacGia.text.clear()
        edtNamXuatBan.text.clear()
        spinnerPhanLoai.setSelection(0)
    }

    private fun loadBooksToTable() {
        // Xóa các row cũ (trừ header)
        val childCount = tableLayout.childCount
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1)
        }

        val sachList = dbHelper.getAllSach()

        sachList.forEach { sach ->
            val tableRow = TableRow(this)

            val tvMaSach = TextView(this)
            tvMaSach.text = sach.maSach.toString()
            tvMaSach.setPadding(8, 8, 8, 8)

            val tvTenSach = TextView(this)
            tvTenSach.text = sach.tenSach
            tvTenSach.setPadding(8, 8, 8, 8)

            val tvTacGia = TextView(this)
            tvTacGia.text = sach.tacGia
            tvTacGia.setPadding(8, 8, 8, 8)

            val tvNamXuatBan = TextView(this)
            tvNamXuatBan.text = sach.namXuatBan.toString()
            tvNamXuatBan.setPadding(8, 8, 8, 8)

            val phanLoai = dbHelper.getPhanLoaiById(sach.maPhanLoai)
            val tvPhanLoai = TextView(this)
            tvPhanLoai.text = phanLoai?.tenPhanLoai ?: "Không xác định"
            tvPhanLoai.setPadding(8, 8, 8, 8)

            tableRow.addView(tvMaSach)
            tableRow.addView(tvTenSach)
            tableRow.addView(tvTacGia)
            tableRow.addView(tvNamXuatBan)
            tableRow.addView(tvPhanLoai)

            tableLayout.addView(tableRow)
        }
    }

    private fun thongKeSach() {
        val thongKe = dbHelper.thongKeSachTheoPhanLoai()
        val message = StringBuilder("Thống kê sách theo phân loại:\n\n")

        thongKe.forEach { (tenPhanLoai, soLuong) ->
            message.append("$tenPhanLoai: $soLuong cuốn\n")
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Thống kê")
            .setMessage(message.toString())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun timKiemSach2023() {
        val sachList = dbHelper.getSachByYear(2023)
        val message = StringBuilder("Sách xuất bản năm 2023:\n\n")

        if (sachList.isEmpty()) {
            message.append("Không có sách nào xuất bản năm 2023")
        } else {
            sachList.forEach { sach ->
                message.append("- ${sach.tenSach} (${sach.tacGia})\n")
            }
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Sách năm 2023")
            .setMessage(message.toString())
            .setPositiveButton("OK", null)
            .show()
    }
}