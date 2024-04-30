package backend;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;


class CSVIO {

    private static boolean createStorageDirectory() {
        try {
            final File CURRENT_DIRECTORY = new File(System.getProperty("user.dir"));
            File storageDirectory = new File(CURRENT_DIRECTORY, "storage");
            if (storageDirectory.exists()) {
                System.out.println("Storage directory already exists.");
                return true;
            } else {
                System.out.println("Storage directory does not exist. \n Creating storage directory...");
                if (storageDirectory.mkdir()) {
                    System.out.println("Created storage directory.");
                    return true;
                } else {
                    System.out.println("Failed to create storage directory.");
                    return false;
                }
            }
        } catch (SecurityException e) {
            System.out.println("Permission denied to create storage directory.");
            return false;
        }
    }

    static boolean createTable(String tableName, String[] columns, String key) throws IOException {
        try {
            if (!createStorageDirectory()) {
                return false;
            }
            File tableFile = new File(System.getProperty("user.dir") + "/storage/" + tableName + ".csv");
            if (tableFile.createNewFile()) {
                System.out.println("Table " + tableName + " created.");
                try (CSVWriter writer = new CSVWriter(new FileWriter(tableFile))) {
                    ArrayList<String> headerList = new ArrayList<String>();
                    boolean keySupplied = false;
                    for (String column : columns) {
                        if (column.equals(key)) {
                            // Ensure the key is the first in the list if required
                            headerList.addFirst(column);
                            keySupplied = true;
                        } else {
                            headerList.add(column);
                        }
                    }
                    if (!keySupplied) {
                        System.out.println("Key does not match any column for table: " + tableName);
                        return false;
                    }
                    String[] headerArray = headerList.toArray(new String[0]);
                    writer.writeNext(headerArray);
                    System.out.println("Header data written to table.");
                    return true;
                } catch (IOException e) {
                    System.out.println("Failed to write header to table " + tableName);
                    throw e;  // Re-throw IOException to be handled by caller
                }
            } else {
                System.out.println("Table '" + tableName + "' already exists.");
                return false;
            }
        } catch (IOException e) {
            throw e;  // Re-throw IOException to be handled by caller
        }
    }

    static boolean createRow(String tableName, Dictionary<String, String> attributes) throws IOException, CsvValidationException {
        File tableFile = new File(System.getProperty("user.dir") + "/storage/" + tableName + ".csv");
        if (!tableFile.exists()) {
            System.out.println("Table " + tableName + " does not exist.");
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(tableFile))) {
            String[] header = reader.readNext();
            if (header == null) {
                System.out.println("Table " + tableName + " is missing headers or corrupted.");
                return false;
            }

            String keyColumnValue = attributes.get(header[0]);
            if (keyColumnValue == null) {
                System.out.println("Key attribute '" + header[0] + "' is missing in provided attributes.");
                return false;
            }

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equals(keyColumnValue)) {
                    System.out.println("An item with '" + header[0] + "': '" + keyColumnValue + "' already exists.");
                    return false;
                }
            }

            String[] newRow = new String[header.length];
            Arrays.fill(newRow, ""); // Initialize all elements to an empty string to handle missing values

            for (int i = 0; i < header.length; i++) {
                if (attributes.get(header[i]) != null) {
                    newRow[i] = attributes.get(header[i]);
                }
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(tableFile, true))) {
                writer.writeNext(newRow);
                System.out.println("New item added to table " + tableName);
                return true;
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println("Failed to update table " + tableName);
            throw e;  // Re-throw IOException to be handled by caller
        }
    }

    static boolean updateRow(String tableName, Dictionary<String, String> attributes) throws IOException, CsvValidationException {
        File tableFile = new File(System.getProperty("user.dir") + "/storage/" + tableName + ".csv");
        if (!tableFile.exists()) {
            System.out.println("Table " + tableName + " does not exist.");
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(tableFile))) {
            String[] header = reader.readNext();
            if (header == null) {
                System.out.println("Table " + tableName + " is missing headers or corrupted.");
                return false;
            }

            String keyColumnValue = attributes.get(header[0]);
            if (keyColumnValue == null) {
                System.out.println("Key attribute '" + header[0] + "' is missing in provided attributes.");
                return false;
            }

            ArrayList<String[]> rows = new ArrayList<>();
            boolean updated = false;

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equals(keyColumnValue)) {
                    for (int i = 0; i < header.length; i++) {
                        if (attributes.get(header[i]) != null) {
                            nextLine[i] = attributes.get(header[i]);
                        }
                    }
                    updated = true;
                }
                rows.add(nextLine);
            }

            if (!updated) {
                System.out.println("No item found with key '" + keyColumnValue + "'. No update performed.");
                return false;
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(tableFile))) {
                writer.writeNext(header);
                for (String[] row : rows) {
                    writer.writeNext(row);
                }
                System.out.println("Item updated successfully in table " + tableName);
                return true;

            } catch (IOException e) {
                System.out.println("Failed to update table " + tableName);
                throw e;  // Re-throw IOException to be handled by caller
            }

        } catch (IOException | CsvValidationException e) {
            System.out.println("Failed to update table " + tableName);
            throw e;  // Re-throw IOException to be handled by caller
        }
    }

    static boolean deleteRow(String tableName, String keyValue) throws IOException, CsvValidationException {
        File tableFile = new File(System.getProperty("user.dir") + "/storage/" + tableName + ".csv");
        if (!tableFile.exists()) {
            System.out.println("Table " + tableName + " does not exist.");
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(tableFile))) {
            String[] header = reader.readNext();
            if (header == null) {
                System.out.println("Table " + tableName + " is missing headers.");
                return false;
            }

            ArrayList<String[]> rows = new ArrayList<>();
            boolean found = false;

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equals(keyValue)) {
                    found = true;
                } else {
                    rows.add(nextLine);
                }
            }

            if (!found) {
                System.out.println("No item found with key '" + keyValue + "'. No deletion performed.");
                return false;
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(tableFile))) {
                writer.writeNext(header);
                for (String[] row : rows) {
                    writer.writeNext(row);
                }
                System.out.println("Item deleted successfully from table " + tableName);
                return true;
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println("Failed to delete item from table " + tableName);
            throw e;  // Re-throw IOException to be handled by caller
        }
    }

    static boolean deleteTable(String tableName) throws IOException {
        File tableFile = new File(System.getProperty("user.dir") + "/storage/" + tableName + ".csv");
        if (!tableFile.exists()) {
            System.out.println("Table " + tableName + " does not exist.");
            return false;
        }

        if (tableFile.delete()) {
            System.out.println("Table " + tableName + " deleted successfully.");
            return true;
        } else {
            System.out.println("Failed to delete table " + tableName);
            return false;
        }
    }

    static boolean tableExists(String tableName) {
        File tableFile = new File(System.getProperty("user.dir") + "/storage/" + tableName + ".csv");
        return tableFile.exists();
    }

    static boolean itemExists(String tableName, String key) throws IOException, CsvValidationException {
        File tableFile = new File(System.getProperty("user.dir") + "/storage/" + tableName + ".csv");
        if (!tableFile.exists()) {
            System.out.println("Table " + tableName + " does not exist.");
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(tableFile))) {
            String[] header = reader.readNext();
            if (header == null) {
                System.out.println("Table " + tableName + " is missing headers or is corrupted.");
                return false;
            }
            String keyColumnValue;
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                keyColumnValue = nextLine[0];  // Assuming the key is always in the first column
                if (keyColumnValue != null && keyColumnValue.equals(key)) {
                    System.out.println("An item with key '" + key + "' exists in table '" + tableName + "'.");
                    return true;
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println("Failed to check for item existence in table " + tableName);
            throw e;  // Re-throw to be handled by the caller
        }
        System.out.println("No item with key '" + key + "' found in table '" + tableName + "'.");
        return false;
    }

    static Dictionary<String, String> getItem(String tableName, String keyValue) throws IOException, CsvValidationException {
        File tableFile = new File(System.getProperty("user.dir") + "/storage/" + tableName + ".csv");
        if (!tableFile.exists()) {
            System.out.println("Table " + tableName + " does not exist.");
            return null;
        }

        try (CSVReader reader = new CSVReader(new FileReader(tableFile))) {
            String[] header = reader.readNext();
            if (header == null) {
                System.out.println("Table " + tableName + " is missing headers or is corrupted.");
                return null;
            }

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equals(keyValue)) {
                    Dictionary<String, String> rowData = new Hashtable<>();
                    for (int i = 0; i < header.length; i++) {
                        rowData.put(header[i], nextLine[i]);
                    }
                    return rowData;
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println("Failed to retrieve item from table " + tableName);
            throw e;  // Re-throw to be handled by the caller
        }

        System.out.println("No item found with key '" + keyValue + "' in table '" + tableName + "'.");
        return null;
    }

    static ArrayList<Dictionary<String, String>> search(String tableName, Dictionary<String, String> attributes) throws IOException, CsvValidationException {
        File tableFile = new File(System.getProperty("user.dir") + "/storage/" + tableName + ".csv");
        if (!tableFile.exists()) {
            System.out.println("Table " + tableName + " does not exist.");
            return null;
        }

        try (CSVReader reader = new CSVReader(new FileReader(tableFile))) {
            String[] header = reader.readNext();
            if (header == null) {
                System.out.println("Table " + tableName + " is missing headers or is corrupted.");
                return null;
            }
            ArrayList<Dictionary<String, String>> items = new ArrayList<>();
            String[] nextLine;
            search:
            while ((nextLine = reader.readNext()) != null) {
                Dictionary<String, String> rowData = new Hashtable<>();
                for (int i = 0; i < header.length; i++) {
                    rowData.put(header[i], nextLine[i]);
                }

                Enumeration enu = attributes.keys();
                while(enu.hasMoreElements()) {
                    String key = (String) enu.nextElement();
                    if(!rowData.get(key).toLowerCase().contains(attributes.get(key).toLowerCase())) {
                        continue search;
                    }
                }
                items.add(rowData);
            }
            return items;
        } catch (IOException | CsvValidationException e) {
            System.out.println("Failed to read table: " + tableName);
            throw e;
        }
    }
}
