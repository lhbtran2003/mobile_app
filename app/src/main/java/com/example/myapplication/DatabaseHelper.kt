package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "QuanLyThuVien.db"
        const val DATABASE_VERSION = 1

        // Bảng PhanLoai
        const val TABLE_PHAN_LOAI = "PhanLoai"
        const val COLUMN_MA_PHAN_LOAI = "maPhanLoai"
        const val COLUMN_TEN_PHAN_LOAI = "tenPhanLoai"
        const val COLUMN_MO_TA_PHAN_LOAI = "moTa"

        // Bảng Sach_Tailieu
        const val TABLE_SACH = "Sach_Tailieu"
        const val COLUMN_MA_SACH = "maSach"
        const val COLUMN_TEN_SACH = "tenSach"
        const val COLUMN_TAC_GIA = "tacGia"
        const val COLUMN_NAM_XUAT_BAN = "namXuatBan"
        const val COLUMN_MA_PHAN_LOAI_FK = "maPhanLoai"

        // Bảng PhanLoaiSach
        const val TABLE_PHAN_LOAI_SACH = "PhanLoaiSach"
        const val COLUMN_MA_PHAN_LOAI_SACH = "maPhanLoaiSach"
        const val COLUMN_MA_SACH_FK = "maSach"
        const val COLUMN_MA_PHAN_LOAI_FK2 = "maPhanLoai"
        const val COLUMN_NGAY_PHAN_LOAI = "ngayPhanLoai"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tạo bảng PhanLoai
        val createPhanLoaiTable = """
            CREATE TABLE $TABLE_PHAN_LOAI (
                $COLUMN_MA_PHAN_LOAI INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TEN_PHAN_LOAI TEXT NOT NULL,
                $COLUMN_MO_TA_PHAN_LOAI TEXT
            )
        """

        // Tạo bảng Sach_Tailieu
        val createSachTable = """
            CREATE TABLE $TABLE_SACH (
                $COLUMN_MA_SACH INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TEN_SACH TEXT NOT NULL,
                $COLUMN_TAC_GIA TEXT NOT NULL,
                $COLUMN_NAM_XUAT_BAN INTEGER NOT NULL,
                $COLUMN_MA_PHAN_LOAI_FK INTEGER,
                FOREIGN KEY ($COLUMN_MA_PHAN_LOAI_FK) REFERENCES $TABLE_PHAN_LOAI($COLUMN_MA_PHAN_LOAI)
            )
        """

        // Tạo bảng PhanLoaiSach
        val createPhanLoaiSachTable = """
            CREATE TABLE $TABLE_PHAN_LOAI_SACH (
                $COLUMN_MA_PHAN_LOAI_SACH INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MA_SACH_FK INTEGER,
                $COLUMN_MA_PHAN_LOAI_FK2 INTEGER,
                $COLUMN_NGAY_PHAN_LOAI TEXT,
                FOREIGN KEY ($COLUMN_MA_SACH_FK) REFERENCES $TABLE_SACH($COLUMN_MA_SACH),
                FOREIGN KEY ($COLUMN_MA_PHAN_LOAI_FK2) REFERENCES $TABLE_PHAN_LOAI($COLUMN_MA_PHAN_LOAI)
            )
        """

        db.execSQL(createPhanLoaiTable)
        db.execSQL(createSachTable)
        db.execSQL(createPhanLoaiSachTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PHAN_LOAI_SACH")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SACH")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PHAN_LOAI")
        onCreate(db)
    }

    // Phương thức thêm phân loại
    fun insertPhanLoai(phanLoai: PhanLoai): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TEN_PHAN_LOAI, phanLoai.tenPhanLoai)
            put(COLUMN_MO_TA_PHAN_LOAI, phanLoai.moTa)
        }

        // Kiểm tra xem phân loại đã tồn tại chưa
        val cursor = db.query(
            TABLE_PHAN_LOAI,
            arrayOf(COLUMN_MA_PHAN_LOAI),
            "$COLUMN_TEN_PHAN_LOAI = ?",
            arrayOf(phanLoai.tenPhanLoai),
            null, null, null
        )

        val result = if (cursor.count > 0) {
            cursor.moveToFirst()
            cursor.getLong(0)
        } else {
            db.insert(TABLE_PHAN_LOAI, null, values)
        }

        cursor.close()
        db.close()
        return result
    }

    // Phương thức thêm sách
    fun insertSach(sach: Sach): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TEN_SACH, sach.tenSach)
            put(COLUMN_TAC_GIA, sach.tacGia)
            put(COLUMN_NAM_XUAT_BAN, sach.namXuatBan)
            put(COLUMN_MA_PHAN_LOAI_FK, sach.maPhanLoai)
        }

        val result = db.insert(TABLE_SACH, null, values)
        db.close()
        return result
    }

    // Phương thức lấy tất cả phân loại
    fun getAllPhanLoai(): List<PhanLoai> {
        val phanLoaiList = mutableListOf<PhanLoai>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_PHAN_LOAI, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val phanLoai = PhanLoai(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MA_PHAN_LOAI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEN_PHAN_LOAI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MO_TA_PHAN_LOAI))
                )
                phanLoaiList.add(phanLoai)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return phanLoaiList
    }

    // Phương thức lấy phân loại theo ID
    fun getPhanLoaiById(maPhanLoai: Int): PhanLoai? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PHAN_LOAI,
            null,
            "$COLUMN_MA_PHAN_LOAI = ?",
            arrayOf(maPhanLoai.toString()),
            null, null, null
        )

        var phanLoai: PhanLoai? = null
        if (cursor.moveToFirst()) {
            phanLoai = PhanLoai(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MA_PHAN_LOAI)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEN_PHAN_LOAI)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MO_TA_PHAN_LOAI))
            )
        }

        cursor.close()
        db.close()
        return phanLoai
    }

    // Phương thức lấy tất cả sách
    fun getAllSach(): List<Sach> {
        val sachList = mutableListOf<Sach>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_SACH, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val sach = Sach(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MA_SACH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEN_SACH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAC_GIA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAM_XUAT_BAN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MA_PHAN_LOAI_FK))
                )
                sachList.add(sach)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return sachList
    }

    // Phương thức lấy sách theo năm xuất bản
    fun getSachByYear(year: Int): List<Sach> {
        val sachList = mutableListOf<Sach>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_SACH,
            null,
            "$COLUMN_NAM_XUAT_BAN = ?",
            arrayOf(year.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val sach = Sach(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MA_SACH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEN_SACH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAC_GIA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAM_XUAT_BAN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MA_PHAN_LOAI_FK))
                )
                sachList.add(sach)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return sachList
    }

    // Phương thức thống kê sách theo phân loại
    fun thongKeSachTheoPhanLoai(): Map<String, Int> {
        val thongKe = mutableMapOf<String, Int>()
        val db = this.readableDatabase

        val query = """
            SELECT pl.$COLUMN_TEN_PHAN_LOAI, COUNT(s.$COLUMN_MA_SACH) as soLuong
            FROM $TABLE_PHAN_LOAI pl
            LEFT JOIN $TABLE_SACH s ON pl.$COLUMN_MA_PHAN_LOAI = s.$COLUMN_MA_PHAN_LOAI_FK
            GROUP BY pl.$COLUMN_MA_PHAN_LOAI, pl.$COLUMN_TEN_PHAN_LOAI
        """

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val tenPhanLoai = cursor.getString(0)
                val soLuong = cursor.getInt(1)
                thongKe[tenPhanLoai] = soLuong
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return thongKe
    }

    // Phương thức thêm phân loại sách
    fun insertPhanLoaiSach(maSach: Int, maPhanLoai: Int, ngayPhanLoai: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MA_SACH_FK, maSach)
            put(COLUMN_MA_PHAN_LOAI_FK2, maPhanLoai)
            put(COLUMN_NGAY_PHAN_LOAI, ngayPhanLoai)
        }

        val result = db.insert(TABLE_PHAN_LOAI_SACH, null, values)
        db.close()
        return result
    }
}