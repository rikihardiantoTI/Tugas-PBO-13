import mysql.connector
import sys

def connect_db():
    try:
        return mysql.connector.connect(
            host="localhost",
            user="root",      # Sesuaikan jika ada password di MySQL kamu
            password="",
            database="toko_retail"
        )
    except mysql.connector.Error as err:
        print(f"Error: {err}")
        sys.exit()

def tampil_menu():
    print("\n╔══════════════════════════════════╗")
    print("║         MENU TOKO RETAIL         ║")
    print("╠══════════════════════════════════╣")
    print("║ 1. Tampil Semua Data             ║")
    print("║ 2. Tambah Data                   ║")
    print("║ 3. Cari Data                     ║")
    print("║ 4. Ubah Data                     ║")
    print("║ 5. Hapus Data                    ║")
    print("║ 0. Keluar                        ║")
    print("╚══════════════════════════════════╝")

def format_tabel(records):
    print("\n╔══════════════════════════════════════════════════════════════════╗")
    print("║                    DAFTAR BARANG TOKO RETAIL                     ║")
    print("╠════╦════════╦════════════════════════════════╦═════════╦═════════╣")
    print("║ #  ║ Kode   ║ Nama Barang                    ║ Harga   ║ Stok    ║")
    print("╠════╬════════╬════════════════════════════════╬═════════╬═════════╣")
    
    if not records:
        print("║                            Data Kosong                           ║")
    else:
        for idx, row in enumerate(records, 1):
            print(f"║ {idx:<2} ║ {row[1]:<6} ║ {row[2]:<30} ║ {row[3]:<7} ║ {row[4]:<7} ║")
            
    print("╚════╩════════╩════════════════════════════════╩═════════╩═════════╝")
    print(f"Total: {len(records)} barang\n")

def tampil_data(db):
    cursor = db.cursor()
    cursor.execute("SELECT * FROM barang ORDER BY id ASC")
    format_tabel(cursor.fetchall())
    cursor.close()

def tambah_data(db):
    cursor = db.cursor()
    kode = input("Masukkan Kode Barang: ")
    nama = input("Masukkan Nama Barang: ")
    harga = int(input("Masukkan Harga: "))
    stok = int(input("Masukkan Stok: "))
    
    sql = "INSERT INTO barang (kode, nama_barang, harga, stok) VALUES (%s, %s, %s, %s)"
    val = (kode, nama, harga, stok)
    
    try:
        cursor.execute(sql, val)
        db.commit()
        print("✅ Data berhasil ditambahkan!")
    except mysql.connector.Error as err:
        print(f"❌ Gagal menambah data: {err}")
    cursor.close()

def cari_data(db):
    cursor = db.cursor()
    keyword = input("Masukkan Kata Kunci (Kode/Nama): ")
    sql = "SELECT * FROM barang WHERE kode LIKE %s OR nama_barang LIKE %s"
    val = (f"%{keyword}%", f"%{keyword}%")
    
    cursor.execute(sql, val)
    format_tabel(cursor.fetchall())
    cursor.close()

def ubah_data(db):
    cursor = db.cursor()
    tampil_data(db)
    kode = input("Masukkan Kode Barang yang ingin diubah: ")
    
    nama = input("Masukkan Nama Barang Baru: ")
    harga = int(input("Masukkan Harga Baru: "))
    stok = int(input("Masukkan Stok Baru: "))
    
    sql = "UPDATE barang SET nama_barang = %s, harga = %s, stok = %s WHERE kode = %s"
    val = (nama, harga, stok, kode)
    
    cursor.execute(sql, val)
    db.commit()
    
    if cursor.rowcount > 0:
        print("✅ Data berhasil diubah!")
    else:
        print("❌ Data tidak ditemukan!")
    cursor.close()

def hapus_data(db):
    cursor = db.cursor()
    tampil_data(db)
    kode = input("Masukkan Kode Barang yang ingin dihapus: ")
    
    sql = "DELETE FROM barang WHERE kode = %s"
    val = (kode,)
    
    cursor.execute(sql, val)
    db.commit()
    
    if cursor.rowcount > 0:
        print("✅ Data berhasil dihapus!")
    else:
        print("❌ Data tidak ditemukan!")
    cursor.close()

def main():
    db = connect_db()
    while True:
        tampil_menu()
        pilihan = input("Pilihan : ")
        
        if pilihan == '1':
            tampil_data(db)
        elif pilihan == '2':
            tambah_data(db)
        elif pilihan == '3':
            cari_data(db)
        elif pilihan == '4':
            ubah_data(db)
        elif pilihan == '5':
            hapus_data(db)
        elif pilihan == '0':
            print("Keluar dari program...")
            break
        else:
            print("❌ Pilihan tidak valid!")

if __name__ == "__main__":
    main()