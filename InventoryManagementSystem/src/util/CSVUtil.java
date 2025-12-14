package util;

import java.io.*;
import java.util.*;
import model.Product;

public class CSVUtil {

    // ✅ Export product list to CSV
    public static void exportToCSV(List<Product> products, String filePath) {
        try (PrintWriter pw = new PrintWriter(new File(filePath))) {
            pw.println("ID,Name,Category,Quantity,Price");
            for (Product p : products) {
                pw.println(p.getId() + "," + p.getName() + "," + p.getCategory() + "," + p.getQuantity() + "," + p.getPrice());
            }
            pw.flush();
            System.out.println("✅ Data exported to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ Import products from CSV (used for bulk upload)
    public static List<Product> importFromCSV(String filePath) {
        List<Product> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    Product p = new Product(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        Integer.parseInt(data[3]),
                        Double.parseDouble(data[4])
                    );
                    list.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
