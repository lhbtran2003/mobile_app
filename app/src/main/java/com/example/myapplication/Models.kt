package com.example.myapplication

// Lớp Model cho bảng PhanLoai
data class PhanLoai(
    val maPhanLoai: Int,
    val tenPhanLoai: String,
    val moTa: String
)

// Lớp Model cho bảng Sach_Tailieu
data class Sach(
    val maSach: Int,
    val tenSach: String,
    val tacGia: String,
    val namXuatBan: Int,
    val maPhanLoai: Int
)

// Lớp Model cho bảng PhanLoaiSach
data class PhanLoaiSach(
    val maPhanLoaiSach: Int,
    val maSach: Int,
    val maPhanLoai: Int,
    val ngayPhanLoai: String
)