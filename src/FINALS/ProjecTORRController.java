package FINALS;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.ResourceBundle;

public class ProjecTORRController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField dateField;
    @FXML
    private TextArea fileReadField;
    @FXML
    private Button convertBtn;

    private FileChooser fileChooser = new FileChooser();
    private XWPFDocument document = new XWPFDocument();
    private XSSFWorkbook workbook;
    private File selectedFile;
    private FileInputStream fis;

    private InputStream input;

    private Alert info = new Alert(AlertType.INFORMATION);
    private Alert warning = new Alert(AlertType.WARNING);

    private Stage edit = new Stage();

    private void initFormats() {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Modern Word Processed Files", "*.docx"),
                new FileChooser.ExtensionFilter("Modern Excel Files", "*.xlsx")
        );
    }

    public void getName(String name) {
        nameField.setText(name);
    }

    private void OpenSaveError(){
        warning.setTitle("ERROR");
        warning.setHeaderText(null);

        warning.showAndWait();
    }
    private void openData() {
        info.setTitle("SUCCESS");
        info.setHeaderText(null);
        info.setContentText("File has been opened.");
        info.showAndWait();
    }

    private void docxCreated() {
        info.setTitle("SUCCESS");
        info.setContentText("Word-processed file has been created");
        info.showAndWait();
    }

    private void noData() {
        warning.setTitle("ERROR");
        warning.setHeaderText(null);
        warning.setContentText("No file opened.");
        warning.showAndWait();
    }

    private void closeData() {
        Alert info = new Alert(AlertType.INFORMATION);
        info.setTitle("SUCCESS");
        info.setHeaderText(null);
        info.setContentText("File has been successfully closed.");
        info.showAndWait();
    }


    public void openFile(ActionEvent event) throws Exception {
        if (fileReadField.isWrapText() == true) {
            warning.setContentText("A file has already been opened.");
            OpenSaveError();
        }
        else {
            selectedFile = fileChooser.showOpenDialog(edit);

            String fileName = selectedFile.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, selectedFile.getName().length());

            if (fileExtension.equals("txt")) {

                Path selectedPath = Paths.get(selectedFile.getAbsolutePath());

                input = Files.newInputStream(selectedPath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String data;
                while ((data = reader.readLine()) != null) {
                    fileReadField.appendText(data + "\n");
                }

                input.close();
                convertBtn.setDisable(false);
            } else if (fileExtension.equals("docx")) {

                document = new XWPFDocument(new FileInputStream(selectedFile));
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                fileReadField.setText(extractor.getText());

                convertBtn.setDisable(true);


            } else if (fileExtension.equals("xlsx")) {

                File excelFile = new File(selectedFile.toString());
                fis = new FileInputStream(excelFile);
                workbook = new XSSFWorkbook(fis);

                XSSFSheet sheet = workbook.getSheetAt(0);

                fileReadField.appendText("Fibonacci Range | Fibonacci Sequence\n");

                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();

                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        switch (cell.getCellType()) {
                            case NUMERIC:

                                fileReadField.appendText(String.valueOf(cell.getNumericCellValue()));
                                break;

                            case STRING:
                                fileReadField.appendText(cell.getStringCellValue());
                                break;
                        }
                        fileReadField.appendText(" ");
                    }
                    fileReadField.appendText("\n");
                }
                workbook.close();
                fis.close();
                convertBtn.setDisable(false);
            }
            openData();
            fileReadField.setWrapText(true);
        }
    }


    public void convertToDocx (ActionEvent event) throws Exception {
        if (fileReadField != null) {
            String fileName = selectedFile.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, selectedFile.getName().length());
            String extensionSub = ".txt";

            if (fileExtension.equals("xlsx")) {
                extensionSub = ".xlsx";
            }

            String newFile = selectedFile.getName().replace(extensionSub, ".docx");
            File convertedFile = new File(newFile);

            if (convertedFile.exists()) {
                warning.setContentText("File already exists.");
                OpenSaveError();
            } else {
                FileOutputStream fos = new FileOutputStream(convertedFile);
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                String data = fileReadField.getText();

                run.setText(data);
                document.write(fos);
                fos.close();
                info.setHeaderText("File Name: " + newFile);
                docxCreated();
            }

            fileReadField.clear();
            fileReadField.setWrapText(false);
            convertBtn.setDisable(true);
        }
    }

    public void closeFile (ActionEvent event){
        if (!fileReadField.isWrapText()) {
            noData();
        } else {
            fileReadField.clear();
            fileReadField.setWrapText(false);
            convertBtn.setDisable(true);
            closeData();
        }
    }

    public void toEditor (ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("File EdiTORR.fxml"));
        Parent next = loader.load();

        Scene scene = new Scene(next);

        EdiTORRController ec = loader.getController();
        ec.getName(nameField.getText());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize (URL url, ResourceBundle rb){
        nameField.setEditable(false);
        convertBtn.setDisable(true);
        initFormats();
        fileReadField.setEditable(false);
        edit.setTitle("BibboNacci EdiTORR");
        dateField.setText(String.valueOf(LocalDate.now()));
        dateField.setEditable(false);
    }
}