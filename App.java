import java.sql.*;
import java.util.Scanner;

public class App {
    // Konfigurasi Database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/toko_retail";
    private static final String DB_USER = "root"; // sesuaikan user mysql Anda
    private static final String DB_PASS = "";     // sesuaikan password mysql Anda

   private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int pilihan;
        do {
            tampilkanMenu();
            System.out.print("Pilihan : ");
            while (!scanner.hasNextInt()) {
                System.out.println("Input harus berupa angka!");
                System.out.print("Pilihan : ");
                scanner.next();
            }
            pilihan = scanner.nextInt();
            scanner.nextLine(); // membersihkan buffer newline

            switch (pilihan) {
                case 1:
                    tampilSemuaData();
                    break;
                case 2:
                    tambahData();
                    break;
                case 3:
                    cariData();
                    break;
                case 4:
                    ubahData();
                    break;
                case 5:
                    hapusData();
                    break;
                case 0:
                    System.out.println("Terima kasih telah menggunakan aplikasi Toko Retail!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid! Silakan pilih menu 0-5.");
            }
            System.out.println();
        } while (pilihan != 0);
    }

    private static void tampilkanMenu() {
        System.out.println("+---------------------------------------+");
        System.out.println("|           MENU TOKO RETAIL            |");
        System.out.println("+---------------------------------------+");
        System.out.println("|  1. Tampil Semua Data                 |");
        System.out.println("|  2. Tambah Data                       |");
        System.out.println("|  3. Cari Data                         |");
        System.out.println("|  4. Ubah Data                         |");
        System.out.println("|  5. Hapus Data                        |");
        System.out.println("|  0. Keluar                            |");
        System.out.println("+---------------------------------------+");
    }

    // MENU 1: Tampil Semua Data
    private static void tampilSemuaData() {
        String sql = "SELECT * FROM barang";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n+-------------------------------------------------------------+");
            System.out.println("|                  DAFTAR BARANG TOKO RETAIL                  |");
            System.out.println("+-----+--------+-------------------------------+-------+------+");
            System.out.printf("| %-3s | %-6s | %-29s | %-5s | %-4s |\n", "#", "Kode", "Nama Barang", "Harga", "Stok");
            System.out.println("+-----+--------+-------------------------------+-------+------+");

            int no = 1;
            while (rs.next()) {
                System.out.printf("| %-3d | %-6s | %-29s | %-5d | %-4d |\n",
                        no++,
                        rs.getString("kode"),
                        rs.getString("nama_barang"),
                        rs.getInt("harga"),
                        rs.getInt("stok"));
            }
            System.out.println("+-----+--------+-------------------------------+-------+------+");
            System.out.println("Total: " + (no - 1) + " barang");

        } catch (SQLException e) {
            System.out.println("Error database: " + e.getMessage());
        }
    }

    // MENU 2: Tambah Data
    private static void tambahData() {
        System.out.println("\n--- TAMBAH DATA BARANG ---");
        System.out.print("Masukkan Kode Barang: ");
        String kode = scanner.nextLine();
        System.out.print("Masukkan Nama Barang: ");
        String nama = scanner.nextLine();
        System.out.print("Masukkan Harga: ");
        int harga = scanner.nextInt();
        System.out.print("Masukkan Stok: ");
        int stok = scanner.nextInt();

        String sql = "INSERT INTO barang (kode, nama_barang, harga, stok) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, kode);
            pstmt.setString(2, nama);
            pstmt.setInt(3, harga);
            pstmt.setInt(4, stok);
            
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data barang berhasil ditambahkan!");
            }
        } catch (SQLException e) {
            System.out.println("Gagal menambah data: " + e.getMessage());
        }
    }

    // MENU 3: Cari Data
    private static void cariData() {
        System.out.println("\n--- CARI DATA BARANG ---");
        System.out.print("Masukkan Kode atau Nama Barang yang dicari: ");
        String keyword = scanner.nextLine();

        String sql = "SELECT * FROM barang WHERE kode = ? OR nama_barang LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, keyword);
            pstmt.setString(2, "%" + keyword + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n+-----+--------+-------------------------------+-------+------+");
                System.out.printf("| %-3s | %-6s | %-29s | %-5s | %-4s |\n", "#", "Kode", "Nama Barang", "Harga", "Stok");
                System.out.println("+-----+--------+-------------------------------+-------+------+");
                
                int no = 1;
                while (rs.next()) {
                    System.out.printf("| %-3d | %-6s | %-29s | %-5d | %-4d |\n",
                            no++,
                            rs.getString("kode"),
                            rs.getString("nama_barang"),
                            rs.getInt("harga"),
                            rs.getInt("stok"));
                }
                System.out.println("+-----+--------+-------------------------------+-------+------+");
                System.out.println("Ditemukan: " + (no - 1) + " barang");
            }
        } catch (SQLException e) {
            System.out.println("Gagal mencari data: " + e.getMessage());
        }
    }

    // MENU 4: Ubah Data
    private static void ubahData() {
        System.out.println("\n--- UBAH DATA BARANG ---");
        System.out.print("Masukkan Kode Barang yang ingin diubah: ");
        String kode = scanner.nextLine();

        // Cek terlebih dahulu apakah barang ada
        String cekSql = "SELECT * FROM barang WHERE kode = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement cekPstmt = conn.prepareStatement(cekSql)) {
            
            cekPstmt.setString(1, kode);
            try (ResultSet rs = cekPstmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Barang dengan kode " + kode + " tidak ditemukan!");
                    return;
                }
                // Jika ada, tampilkan info lama
                System.out.println("Data lama: " + rs.getString("nama_barang") + " | Harga: " + rs.getInt("harga") + " | Stok: " + rs.getInt("stok"));
            }

            // Input data baru
            System.out.print("Nama Barang Baru: ");
            String namaBaru = scanner.nextLine();
            System.out.print("Harga Baru: ");
            int hargaBaru = scanner.nextInt();
            System.out.print("Stok Baru: ");
            int stokBaru = scanner.nextInt();

            String updateSql = "UPDATE barang SET nama_barang = ?, harga = ?, stok = ? WHERE kode = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, namaBaru);
                pstmt.setInt(2, hargaBaru);
                pstmt.setInt(3, stokBaru);
                pstmt.setString(4, kode);
                
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Data barang berhasil diperbarui!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mengubah data: " + e.getMessage());
        }
    }

    // MENU 5: Hapus Data
    private static void hapusData() {
        System.out.println("\n--- HAPUS DATA BARANG ---");
        System.out.print("Masukkan Kode Barang yang ingin dihapus: ");
        String kode = scanner.nextLine();

        String sql = "DELETE FROM barang WHERE kode = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, kode);
            int rowsDeleted = pstmt.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Barang dengan kode " + kode + " berhasil dihapus!");
            } else {
                System.out.println("Barang dengan kode " + kode + " tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal menghapus data: " + e.getMessage());
        }
    }
}