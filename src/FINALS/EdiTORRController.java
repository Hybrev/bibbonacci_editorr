package FINALS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.nio.file.StandardOpenOption.CREATE;

public class EdiTORRController implements Initializable {

    @FXML private TextField NameField;
    @FXML private TextField DateField;
    @FXML private TextField sequenceField;
    @FXML private TextField rangeField;

    @FXML private TableView<Fibonacci> LogTable;
    @FXML private TableColumn<Fibonacci, Integer> rangeCol;
    @FXML private TableColumn<Fibonacci, String> sequenceCol;


    private XSSFWorkbook workbook = new XSSFWorkbook();
    private Alert info = new Alert(Alert.AlertType.INFORMATION);
    private Alert warning = new Alert(Alert.AlertType.WARNING);

    private int fib_sum, fib_range, stack = 0;
    private ArrayList<Integer> numbers = new ArrayList<>();

    private void errorScreen(){
        warning.setTitle("ERROR");
        warning.setHeaderText(null);
        warning.showAndWait();
    }

    private void emptyTable(){
        warning.setContentText("Table has no data entries.");
    }

    private void errorInput(){
        warning.setContentText("Please enter a natural number starting from 1");
        rangeField.clear();
        sequenceField.clear();
    }

    private void rangeAdded(){
        info.setContentText("Successfully added to range text area.");
        info.setTitle("SUCCESS");
        info.setHeaderText(null);
        info.showAndWait();
    }

    public void getName(String name){ NameField.setText(name); }

    private ArrayList<Integer> fibonacci(int range){
        numbers.clear();
        this.fib_sum = 0;

        int fib_x = 1, fib_y = 1;
        numbers.add(fib_x);
        numbers.add(fib_y);

        for (int i = 0; i < range; i++) {
            fib_sum = fib_x + fib_y;
            fib_x = fib_y;
            fib_y = fib_sum;
            numbers.add(fib_sum);
        }
        return numbers;
    }



    public void inputSequence(ActionEvent event){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Range Index Input");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter a valid natural number:");

        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()) {
            this.fib_range = Integer.parseInt(result.get());

            if (fib_range > 1) {
                rangeField.setText(String.valueOf(fib_range));
                sequenceField.setText(String.valueOf(fibonacci(fib_range - 2)));
                rangeAdded();
            }
            else if(fib_range == 1) {
                fibonacci(fib_range);
                rangeField.setText(String.valueOf(fib_range));
                sequenceField.setText(String.valueOf(fibonacci(fib_range).remove(1)));
                rangeAdded();
            }
            else {
                errorInput();
                errorScreen();
            }
        }
    }

    public void generateSequence(ActionEvent event) {
        fib_range = (int) ((Math.random() * ((20 - 1) + 1)) + 1) + 2;

        rangeField.setText(String.valueOf(fib_range));
        sequenceField.setText(String.valueOf(fibonacci(fib_range - 2)));
        System.out.println(numbers);
    }

    public void addData(ActionEvent event) {

        if (fib_range <= 0) {
            errorInput();
        } else {
            if(fib_range == 1){
                numbers.remove(1);
            }
            Fibonacci fibonacci = new Fibonacci(fib_range, String.valueOf(numbers));
            LogTable.getItems().add(fibonacci);
            info.setContentText("Successfully added to table data.");
            info.setTitle("SUCCESS");
            info.setHeaderText(null);
            info.showAndWait();
        }
    }

    public void clearData(ActionEvent event) {
        if (LogTable.getItems().isEmpty()) {
            emptyTable();
            errorScreen();
        } else {
            LogTable.getItems().clear();
        }
    }

    public void createExcel(ActionEvent event) throws Exception{
        if (LogTable.getItems().isEmpty()) {
            emptyTable();
            errorScreen();
        }
        else {
            stack++;

            XSSFSheet sheet = workbook.createSheet("File Entry - " + stack);

            for (int i = 0; i < LogTable.getItems().size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                for (int j = 0; j < LogTable.getColumns().size(); j++) {
                    if (LogTable.getColumns().get(j).getCellData(i) != null) {
                        row.createCell(j).setCellValue(LogTable.getColumns().get(j).getCellData(i).toString());
                    } else {
                        row.createCell(j).setCellValue("");
                    }
                }
                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
            }
            File workbookToFile = new File(NameField.getText() + " " + DateField.getText() + ".xlsx");

            FileOutputStream fos = new FileOutputStream(workbookToFile);
            workbook.write(fos);
            fos.close();

            info.setTitle("SUCCESS");
            info.setHeaderText("Attributes of the file:\n" + "\nFile name: " + workbookToFile.getName() + "\nFile Path: " + workbookToFile.getAbsolutePath() + "\nFile Size: " + workbookToFile.length());
            info.setContentText("Successfully exported the data to .xlsx file format");
            info.showAndWait();
        }
    }


    @FXML
    public void saveTxt(ActionEvent event) {
        if(LogTable.getItems().isEmpty()){
            emptyTable();
        }
        else {
            Path file = Paths.get(String.valueOf(new File(NameField.getText() + " " + DateField.getText() + ".txt")));
            String cell_data;
            int i = 0;
            try {
                OutputStream output = new BufferedOutputStream(Files.newOutputStream(file, CREATE));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));

                for (Object o : LogTable.getItems()) {
                    cell_data = rangeCol.getCellData((Fibonacci) o) + " has these sequence of numbers: " + sequenceCol.getCellData((Fibonacci) o);
                    writer.write(cell_data, 0, cell_data.length());
                    writer.newLine();
                }

                writer.close();
                InputStream input = new
                        BufferedInputStream(Files.newInputStream(file));
                BufferedReader reader = new
                        BufferedReader(new InputStreamReader(input));
                cell_data = reader.readLine();

                while (cell_data != null) {
                    cell_data = reader.readLine();
                }

                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                reader.close();

                info.setTitle("SUCCESS");
                info.setHeaderText("Attributes of the file:\n" + "\nFile name: " + file.getFileName() + "\nFile Path: " + file.toAbsolutePath() + "\nFile Size: " + attr.size());
                info.setContentText("Successfully exported the data to .txt file.");
                info.showAndWait();

            } catch (Exception e) {
                warning.setTitle("ERROR");
                warning.setHeaderText(null);
                warning.setContentText("Message" + e);
            }
        }
    }

    public void toProjector(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("File ProjecTORR.fxml"));
        Parent next = loader.load();

        Scene scene = new Scene(next);

        ProjecTORRController pc = loader.getController();
        pc.getName(NameField.getText());

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow() ;
        stage.setScene(scene);
        stage.show();
    }

    //code below initializes on startup
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NameField.setEditable(false);
        DateField.setEditable(false);
        DateField.setText(String.valueOf(LocalDate.now()));

        rangeField.setEditable(false);
        sequenceField.setEditable(false);

        rangeCol.setCellValueFactory(new PropertyValueFactory<>("Range"));
        sequenceCol.setCellValueFactory(new PropertyValueFactory<>("Sequence"));
    }

    private ObservableList<Fibonacci> observableList = FXCollections.observableArrayList(
            new Fibonacci(1, "[1]")
    );



}
