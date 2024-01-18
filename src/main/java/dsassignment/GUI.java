package dsassignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;;

public class GUI {

    private static DefaultTableModel tableModel;
    private static JTable dataTable;
    private static DatabaseManager manager;
    private static JLabel userAlert;
    private static String filePath;

    public static void main(String[] args) {
        JFrame frame = new JFrame("DS Database ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int option = JOptionPane.showOptionDialog(
                frame,
                "Do you want to load an existing database or create a new one?",
                "MyDatabase",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[] { "Load", "Create New" },
                "Load");

        if (option == JOptionPane.YES_OPTION) {
            // Load existing file
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

            // Set a file filter to display only .ser files
            javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().toLowerCase().endsWith(".ser");
                }

                @Override
                public String getDescription() {
                    return "Serialized Files (*.ser)";
                }
            };

            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Choose a .ser file to load");
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePath = selectedFile.getAbsolutePath();
            } else {
                System.exit(0);
            }
        } else {
            // Create new file
            String newFileName = JOptionPane.showInputDialog(
                    frame,
                    "Enter the name of the new file (without extension):",
                    "Create new database",
                    JOptionPane.PLAIN_MESSAGE);

            if (newFileName == null || newFileName.trim().isEmpty()) {
                System.exit(0);
            }

            filePath = newFileName + ".ser";

        }

        manager = new DatabaseManager();
        checkFile(manager, filePath);

        JPanel panel = new JPanel(null);
        panel.setBackground(Color.LIGHT_GRAY);

        setupUI(panel);

        frame.getContentPane().add(panel);
        frame.setSize(1080, 720);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static void createNewFile(String filePath, DatabaseManager manager) {
        File file = new File(filePath);

        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                manager.databases = new MyHashMap<>();
                manager.SaveData(filePath);
            } else {
                System.out.println("File creation failed.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void checkFile(DatabaseManager manager, String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            manager.databases = manager.loadData(filePath);
        } else {
            createNewFile(filePath, manager);
        }
    }

    private static void setupUI(JPanel panel) {
        JLabel dataTypeLabel = new JLabel("Data Type:");
        dataTypeLabel.setBounds(70, 100, 100, 50);
        String[] dataTypes = { "String", "Number", "Character", "Array", "Boolean", "Date" };
        JComboBox<String> dataTypeComboBox = new JComboBox<>(dataTypes);
        dataTypeComboBox.setBounds(160, 100, 200, 60);
        dataTypeComboBox.addActionListener(e -> checkSelectedDataType(dataTypeComboBox));

        JLabel indexLabel = new JLabel("Index:");
        indexLabel.setBounds(70, 200, 100, 50);
        JTextField indexField = new JTextField();
        indexField.setBounds(160, 200, 200, 60);

        JLabel valueLabel = new JLabel("Value:");
        valueLabel.setBounds(70, 300, 100, 50);
        JTextField valueField = new JTextField();
        valueField.setBounds(160, 300, 200, 60);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(100, 500, 200, 80);
        deleteButton.addActionListener(e -> deleteData());
        panel.add(deleteButton);

        JButton saveButton = new JButton("Save to Database");
        saveButton.setBounds(100, 400, 200, 80);
        saveButton.addActionListener(e -> saveData(dataTypeComboBox, indexField, valueField));

        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(650, 600, 200, 50);
        clearButton.addActionListener(e -> clearData());

        JLabel currentFileName = new JLabel("Current file directory : \n" + filePath);
        currentFileName.setBounds(5, 5, 600, 50);

        userAlert = new JLabel("Enter string value");
        userAlert.setBounds(160, 280, 250, 15);

        setupTable(panel);

        panel.add(dataTypeLabel);
        panel.add(dataTypeComboBox);
        panel.add(indexLabel);
        panel.add(indexField);
        panel.add(valueLabel);
        panel.add(valueField);
        panel.add(saveButton);
        panel.add(clearButton);
        panel.add(userAlert);
        panel.add(currentFileName);

    }

    private static void setupTable(JPanel panel) {
        tableModel = new DefaultTableModel();

        tableModel.addColumn("Index");
        tableModel.addColumn("Value");
        tableModel.addColumn("Data Type");

        dataTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBounds(500, 100, 500, 400);

        dataTable.setFillsViewportHeight(true);
        manager.databases = manager.loadData(filePath);
        showData();

        panel.add(scrollPane);
    }

    private static void saveData(JComboBox<String> dataTypeComboBox, JTextField indexField, JTextField valueField) {
        String selectedDataType = (String) dataTypeComboBox.getSelectedItem();

        if (!valueField.getText().isEmpty() && !indexField.getText().isEmpty()) {
            switch (selectedDataType) {
                case "String":
                    manager.Insert(indexField.getText(), valueField.getText());
                    break;
                case "Number":
                    Number num = NumberConverter(valueField.getText());
                    manager.Insert(indexField.getText(), num);
                    break;
                case "Character":
                    char character = valueField.getText().charAt(0);
                    manager.Insert(indexField.getText(), character);
                    break;
                case "Array":
                    Object[] splitValue = valueField.getText().split(",");
                    manager.Insert(indexField.getText(), splitValue);
                    break;
                case "Boolean":
                    Boolean bool = Boolean.parseBoolean(valueField.getText());
                    manager.Insert(indexField.getText(), bool);
                    break;
                case "Date":
                    String dateString = valueField.getText();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Adjust the format accordingly
                    try {
                        Date date = dateFormat.parse(dateString);
                        manager.Insert(indexField.getText(), date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;  
                default:
                    break;
            }
            tableModel.setRowCount(0);
            showData();
            manager.SaveData(filePath);
        }
    }

    private static void checkSelectedDataType(JComboBox<String> dataTypeComboBox) {
        String selectedDataType = (String) dataTypeComboBox.getSelectedItem();

        switch (selectedDataType) {
            case "String":
                userAlert.setText("Enter string value :");
                break;
            case "Number":
                userAlert.setText("Enter number value :");
                break;
            case "Character":
                userAlert.setText("Enter character value :");
                break;
            case "Array":
                userAlert.setText("Enter array (seperated by commas) value :");
                break;
            case "Boolean":
                userAlert.setText("Enter boolean value (true or false) :");
                break;
                case "Date":
                userAlert.setText("Enter date (dd-MM-yyyy) :");
            default:
                break;
        }

    }

    private static void clearData() {
        manager.ClearDatabase();
        manager.SaveData(filePath);
        tableModel.setRowCount(0);
        showData();
    }

    private static void deleteData() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow != -1) {
            String indexToDelete = (String) tableModel.getValueAt(selectedRow, 0);
            manager.Delete(indexToDelete);
            tableModel.removeRow(selectedRow);
            manager.SaveData(filePath);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showData() {
        if (manager.databases != null) {
            for (Entry<String, Object> e : manager.databases.bucket) {
                Entry<String, Object> currentEntry = e;

                while (currentEntry != null) {
                    String index = currentEntry.getKey();
                    Object value = currentEntry.getValue();

                    if (value instanceof Object[]) {
                        Object[] array = (Object[]) value;
                        StringBuilder builder = new StringBuilder();
                        builder.append("[");
                        for (int i = 0; i < array.length; i++) {
                            builder.append(array[i].toString());
                            if (i < array.length - 1) {
                                builder.append(",");
                            }
                        }
                        builder.append("]");
                        tableModel.addRow(new Object[] { index, builder.toString(), "Array" });
                    }else if (value instanceof Date){
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyyy");
                        tableModel.addRow(new Object[] {index, sdf.format(value), "Date" });
                    
                    } else {
                        String data = value.getClass().getSimpleName();
                        if (data.equals("JSONArray")) {
                            data = "Array";
                        }
                        tableModel.addRow(new Object[] { index, value, data });
                    }

                    currentEntry = currentEntry.next;
                }
            }
        } else {
            System.out.println("Database is null");
        }
    }

    private static Number NumberConverter(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e1) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e2) {
                try {
                    return Float.parseFloat(value);
                } catch (NumberFormatException e3) {
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e4) {
                        try {
                            return Byte.parseByte(value);
                        } catch (NumberFormatException e5) {
                            try {
                                return Short.parseShort(value);
                            } catch (NumberFormatException e6) {
                                throw new IllegalArgumentException("Unable to convert the string to a numeric value");
                            }
                        }
                    }
                }
            }
        }
    }
}

