import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;





public class Main {
//   private final String FILE_TO_MOVE = "database.txt";
// private final String TARGET_FILE = "temp.txt";

  public static void main(String[] args) throws IOException {
    // scanner
    Scanner input = new Scanner(System.in);

    // variabel
    String pilihanUser;
    boolean ulang = true;

    while (ulang) {
      clearScreen();

      // menu
      System.out.println("Database Mahasiswa IT 2021!!");
      System.out.println("1. View Data");
      System.out.println("2. Search Data");
      System.out.println("3. Insert Data");
      System.out.println("4. Update Data");
      System.out.println("5. Delete Data");

      System.out.print("\nMasukkan pilihan anda :");
      pilihanUser = input.next();

      // condition
      switch (pilihanUser) {
        case "1":
          System.out.println("\n======================");
          System.out.println("VIEW DATA");
          System.out.println("======================");
          tampilkanData();
          break;
        case "2":
          System.out.println("\n======================");
          System.out.println("SEARCH DATA");
          System.out.println("======================");
          searchData();
          break;
        case "3":
          System.out.println("\n======================");
          System.out.println("INSERT DATA");
          System.out.println("======================");
          insertData();
          // tampilkanData();
          break;
        case "4":
          System.out.println("\n======================");
          System.out.println("UPDATE DATA");
          System.out.println("======================");

          break;
        case "5":
          System.out.println("\n======================");
          System.out.println("DELETE DATA");
          System.out.println("======================");
          deleteData();

          break;
        default:
          System.err.println("Maaf inputan salah!!");
      }
      ulang = getYesOrNo("Apakah anda ingin melanjutkan");
    }
  }

  private static void deleteData() throws IOException{
    //  mengambil database 
    File database = new File("database.txt");
    FileReader fileInput = new FileReader(database);
    BufferedReader bufferedInput = new BufferedReader(fileInput);


    // buat temp
    File tempDB =  new File("temp.txt");
    FileWriter fileOutput = new FileWriter(tempDB);
    BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);


    // tampilkan data
    System.out.println("List Data Mahasiswa");
    tampilkanData();



    // konfirmasi delete dari user
    Scanner input = new Scanner(System.in);
    System.out.print("Masukkan nomor yang akan di hapus: ");
    int deleteData = input.nextInt();


    // looping untuk membaca tiap bari data 
    boolean isFound = false;
    int dataCounts =0;

    String data = bufferedInput.readLine();

    while (data != null) {
      dataCounts++;
      boolean isDelete = false;

      StringTokenizer token = new StringTokenizer(data, ",");


      // data yang ingin fi hapus
      if (deleteData == dataCounts) {
        System.out.println("\nData yang ingin anda hapus adalah :");
        System.out.println("=======================================");
        System.out.println("NRP\t:"+ token.nextToken());
        System.out.println("Nama\t:"+ token.nextToken());
        System.out.println("Umur\t:"+ token.nextToken());
        System.out.println("Alamat\t:"+ token.nextToken());

        isDelete = getYesOrNo("Apakah anda akan menghapus data ini?");
        isFound = true;
      }

      if (isDelete) {
        System.out.println("Data berhasil dihapus");
      }else{
        // memindahkan data dari original ke temp
        bufferedOutput.write(data);
        bufferedOutput.newLine();
      }
      data = bufferedInput.readLine();
    }

    if (!isFound) {
      System.err.println("Data tidak titemukan");
    }

    bufferedOutput.flush();
    fileInput.close();
    bufferedInput.close();
    fileOutput.close();
    bufferedOutput.close();
    System.gc();


    // delete database original
    database.delete();
    // rename temp menjadi original
    tempDB.renameTo(database);
  }

  private static void insertData() throws IOException {
    FileWriter fileWriter = new FileWriter("database.txt", true);
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    Scanner input = new Scanner(System.in);

    String nrp, nama, umur, alamat;
    System.out.print("Masukkan NRP\t:");
    nrp = input.nextLine();
    System.out.print("Masukkan Nama\t:");
    nama = input.nextLine();
    System.out.print("Masukkan Umur\t:");
    umur = input.nextLine();
    System.out.print("Masukkan Alamat\t:");
    alamat = input.nextLine();

    String[] keywords = { nrp + "," + nama + "," + umur + "," + alamat };
    System.out.println(Arrays.toString(keywords));

    boolean isExist = cekDataDiDatabase(keywords, false);

    if (!isExist) {

      long nomorEntry = 1;
      String nameWithOutSpace = nama.replaceAll("\\s+", "");
      // String primaryKey = nameWithOutSpace+"_"+
      System.out.println("\nData yang akan di inputkan adalah");
      System.out.println("=====================================");
      System.out.println("NRP\t:" + nrp);
      System.out.println("Nama\t:" + nama);
      System.out.println("Umur\t:" + umur);
      System.out.println("Alamat\t:" + alamat);

      boolean isTambah = getYesOrNo("Anda yakin ingin menambah data tersebut?");

      if (isTambah) {
        bufferedWriter.write(nrp + "," + nama + "," + umur + "," + alamat);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }

    } else {
      System.out.println("Data yang anda masukkan sudah ada di database dengan detail sebagai berikut:");
      cekDataDiDatabase(keywords, true);
    }

    bufferedWriter.close();
    fileWriter.close();
    System.gc();
  }

  private static void searchData() throws IOException {

    // membaca database ada atau tidak

    try {
      File file = new File("database.txt");
    } catch (Exception e) {
      System.err.println("Database Tidak ditemukan");
      System.err.println("Silahkan tambah data terlebih dahoeloe");
      return;
    }

    // kita ambil keyword dari user

    Scanner terminalInput = new Scanner(System.in);
    System.out.print("Masukan kata kunci untuk mencari Data: ");
    String cariString = terminalInput.nextLine();
    String[] keywords = cariString.split("\\s+");

    // kita cek keyword di database
    cekDataDiDatabase(keywords, true);
    System.gc();
  }

  private static boolean cekDataDiDatabase(String[] keywords, boolean isDisplay) throws IOException {

    FileReader fileInput = new FileReader("database.txt");
    BufferedReader bufferInput = new BufferedReader(fileInput);

    String data = bufferInput.readLine();
    boolean isExist = false;
    int nomorData = 0;

    if (isDisplay) {
      System.out.println("\n| No |\tNRP          |\tNama                   |\tUmur         |\tAlamat");
      System.out.println(
          "----------------------------------------------------------------------------------------------------------");
    }

    while (data != null) {

      // cek keywords didalam baris
      isExist = true;

      for (String keyword : keywords) {
        isExist = isExist && data.toLowerCase().contains(keyword.toLowerCase());
      }

      // jika keywordsnya cocok maka tampilkan

      if (isExist) {
        if (isDisplay) {
          nomorData++;
          StringTokenizer stringToken = new StringTokenizer(data, ",");

          System.out.printf("| %2d ", nomorData); // for nomor
          System.out.printf("|\t%11s  ", stringToken.nextToken()); // for nrp
          System.out.printf("|\t%-20s   ", stringToken.nextToken()); // for nama
          System.out.printf("|\t%-10s   ", stringToken.nextToken() + " Tahun"); // for umur
          System.out.printf("|\t%s   ", stringToken.nextToken()); // for alamat
          System.out.print("\n");
        } else {
          break;
        }
      }

      data = bufferInput.readLine();
    }

    if (isDisplay) {
      System.out.println(
          "----------------------------------------------------------------------------------------------------------");
    }
    
    fileInput.close();
    bufferInput.close();
    System.gc();
    return isExist;
  }

  private static void tampilkanData() throws IOException {
    FileReader fileInput;
    BufferedReader bufferInput;

    try {
      fileInput = new FileReader("database.txt");
      bufferInput = new BufferedReader(fileInput);
    } catch (Exception e) {
      System.err.println("Database tidak ditemukan!");
      System.err.println("Silahan Insert data dulu");
      insertData();
      return;
    }

    System.out.println("\n| No |\tNRP          |\tNama                   |\tUmur         |\tAlamat");
    System.out.println(
        "----------------------------------------------------------------------------------------------------------");

    String data = bufferInput.readLine();
    int nomorData = 0;
    while (data != null) {
      nomorData++;

      // StringTokenizer
      StringTokenizer stringToken = new StringTokenizer(data, ",");

      System.out.printf("| %2d ", nomorData); // for nomor
      System.out.printf("|\t%11s  ", stringToken.nextToken()); // for nrp
      System.out.printf("|\t%-20s   ", stringToken.nextToken()); // for nama
      System.out.printf("|\t%-10s   ", stringToken.nextToken() + " Tahun"); // for umur
      System.out.printf("|\t%s   ", stringToken.nextToken()); // for alamat
      System.out.print("\n");

      data = bufferInput.readLine();
    }

    System.out.println(
        "----------------------------------------------------------------------------------------------------------");
    
    fileInput.close();
    bufferInput.close();
    System.gc();
  
}

  private static boolean getYesOrNo(String message) {
    Scanner input = new Scanner(System.in);
    // confirmation
    System.out.print("\n" + message + " (y/n)?");
    String pilihanUser = input.next();

    while (!pilihanUser.equalsIgnoreCase("y") && !pilihanUser.equalsIgnoreCase("n")) {
      System.err.println("Pilihan anda bukan y dan n");
      System.out.print(message + " (y/n)?");
      pilihanUser = input.next();
    }

    
    return pilihanUser.equalsIgnoreCase("y") ? true : false;
  }

  private static void clearScreen() {
    try {
      if (System.getProperty("os.name").contains("Windows")) {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      } else {
        System.out.print("\033\143");
      }
    } catch (Exception ex) {
      System.err.println("tidak bisa di bersihkan");
    }
  }
}