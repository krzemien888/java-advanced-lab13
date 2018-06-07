package pwr.lab13.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import pwr.lab13.main.LabFile;
import pwr.lab13.main.LabFileBuilder;

public class MainAppController implements Initializable {

    private static final String SORT_FILE_NAME = "Nazwa pliku";
    private static final String SORT_FILE_SIZE = "Rozmiar";

    private ObservableList<LabFile> data;
    private ObservableList<TableColumn> columns;

    @FXML
    private TableView table;

    @FXML
    private AnchorPane app;

    @FXML
    private TextField directoryTextField;

    @FXML
    private CheckMenuItem columnMd5;

    @FXML
    private CheckMenuItem columnSha256;

    @FXML
    private CheckMenuItem columnSha512;

    @FXML
    private BorderPane loadingPane;

    @FXML
    private ComboBox sortableComboBox;

    @FXML
    private CheckBox sortAscCheckBox;

    private String currentDir;

    private String activeSortColumn = SORT_FILE_NAME;
    private boolean activeSortAsc = true;

    private final String CSV_SEPARATOR = ";";
    private final char QUOTE_CHAR = '"';

    @FXML
    public void handleChangeDirButtonAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Lab13");
        File defaultDirectory = new File("c:/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(getStage());
        changeDirTo(selectedDirectory.getPath());
    }

    private Window getStage() {
        return (Stage) app.getScene().getWindow();
    }

    private void showLoadingPane(boolean show) {
        Platform.runLater(() -> {
            loadingPane.setVisible(show);
        });
    }

    public void close() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initTable();
        sortableComboBox.getItems().addAll(SORT_FILE_NAME, SORT_FILE_SIZE);
        sortableComboBox.setValue(SORT_FILE_NAME);
        sortableComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                activeSortColumn = t1;
                resortTable();
            }
        });

        sortAscCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                activeSortAsc = new_val;
                resortTable();
            }
        });

        Platform.runLater(() -> {   
            changeDirTo("C:\\");
        });
    }

    public void resortTable() {
        changeDirTo(currentDir);
    }

    @FXML
    public void closeAppAction(ActionEvent event) {
        close();
    }

    @FXML
    public void changeVisibleColumnAction(ActionEvent event) {
        initTable();
        changeDirTo(currentDir);
    }

    private void changeDirTo(String dir) {
        currentDir = dir;
        Thread thread = new Thread(() -> {
            showLoadingPane(true);
            directoryTextField.setText(dir);
            File fd = new File(dir);
            ArrayList<LabFile> files = new ArrayList();
            //Dodaj katalog nadrzędny
            if (fd.getParentFile() != null) {
                files.add(LabFileBuilder.newBuilder(fd.getParentFile(), "..").convert());
            }
            File[] filesa = fd.listFiles();
            if (filesa != null) {
                for (File f : filesa) {
                    //Dodaj pojedyńczy plik za pomocą buildera
                    LabFileBuilder builder = LabFileBuilder.newBuilder(f, f.getName()).withSha512(columnSha512.isSelected()).withMd5(columnMd5.isSelected()).withSha256(columnSha256.isSelected());
                    files.add(builder.convert());
                }
            }

            //Sortuj listę plików
            Comparator<LabFile> comparatorObj;
            if (activeSortColumn.equals(SORT_FILE_NAME)) {
                comparatorObj = Comparator.comparing(LabFile::getFileName);
            } else {
                comparatorObj = Comparator.comparing(LabFile::getSizeBytes);
            }
            files.sort(activeSortAsc ? comparatorObj : comparatorObj.reversed());

            data = FXCollections.observableArrayList(files);
            data.removeAll(data);
            data.addAll(files);
            table.setItems(data);
            showLoadingPane(false);
        });

        thread.setDaemon(true);
        thread.start();

    }

    private void switchDirectory(TableRow<LabFile> row) {
        LabFile rowData = row.getItem();
        if (rowData.isIsDirectory()) {
            changeDirTo(rowData.getFullPath());
        }
    }

    private void initTable() {
        table.setItems(data);

        table.setEditable(true);
        table.setOnKeyReleased(t -> {
            if (!table.getSelectionModel().isEmpty()
                    && t.getCode() == KeyCode.ENTER) {
                LabFile lf = (LabFile) table.getSelectionModel().getSelectedItem();
                if (lf.isIsDirectory()) {
                    changeDirTo(lf.getFullPath());
                }
            }
        });

        table.setRowFactory(tv -> {
            TableRow<LabFile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    switchDirectory(row);
                }
            });
            return row;
        });

        ArrayList<TableColumn> columnsNew = new ArrayList<>();
        TableColumn fileNameCol = new TableColumn("Nazwa pliku");
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        columnsNew.add(fileNameCol);

        TableColumn columnSize = new TableColumn("Rozmiar");
        columnSize.setSortable(false);
        columnSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        columnsNew.add(columnSize);

        if (columnMd5.isSelected()) {
            TableColumn md5Column = new TableColumn("MD5");
            md5Column.setSortable(false);
            md5Column.setCellValueFactory(new PropertyValueFactory<>("md5"));
            columnsNew.add(md5Column);
        }

        if (columnSha256.isSelected()) {
            TableColumn sha256Column = new TableColumn("SHA256");
            sha256Column.setSortable(false);
            sha256Column.setCellValueFactory(new PropertyValueFactory<>("sha256"));
            columnsNew.add(sha256Column);
        }

        if (columnSha512.isSelected()) {
            TableColumn crc32Column = new TableColumn("SHA512");
            crc32Column.setSortable(false);
            crc32Column.setCellValueFactory(new PropertyValueFactory<>("sha512"));
            columnsNew.add(crc32Column);
        }

        columns = FXCollections.observableArrayList(columnsNew);
        table.getColumns().clear();
        table.getColumns().addAll(columns);
    }

    private String getSelectedFileName() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik jako");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Pliki CSV", "*.csv")
        );
        File f = fileChooser.showSaveDialog(getStage());
        return f != null ? f.getAbsolutePath() : null;
    }

    @FXML
    public void saveListAs(ActionEvent event) {
        String fileName = getSelectedFileName();
        if (fileName == null) {
            return;
        }
        try {
            try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
                for (LabFile lf : data) {
                    StringBuilder str = new StringBuilder();
                    str.append(QUOTE_CHAR).append(lf.getFileName()).append(QUOTE_CHAR).append(CSV_SEPARATOR);
                    str.append(QUOTE_CHAR).append(lf.isIsDirectory() ? "-" : lf.getSizeBytes()).append(QUOTE_CHAR).append(CSV_SEPARATOR);
                    if (columnMd5.isSelected()) {
                        str.append(QUOTE_CHAR).append(lf.isIsDirectory() ? "-" : lf.getMd5()).append(QUOTE_CHAR).append(CSV_SEPARATOR);
                    }
                    if (columnSha256.isSelected()) {
                        str.append(QUOTE_CHAR).append(lf.isIsDirectory() ? "-" : lf.getSha256()).append(QUOTE_CHAR).append(CSV_SEPARATOR);
                    }
                    if (columnSha512.isSelected()) {
                        str.append(QUOTE_CHAR).append(lf.isIsDirectory() ? "-" : lf.getSha512()).append(QUOTE_CHAR).append(CSV_SEPARATOR);
                    }
                    writer.println(str.toString());
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            System.err.print(ex.getMessage());
        }
    }

}
