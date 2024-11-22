package viewmodel;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dao.DbConnectivityClass;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Person;
import service.MyLogger;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class DB_GUI_Controller implements Initializable {

    @FXML
    public Text incorrectField;
    @FXML
    public Button clearBtn;
    @FXML
    public Button addBtn;
    @FXML
    public Button deleteBtn;
    @FXML
    public Button editBtn;
    @FXML
    public ChoiceBox major2;
    @FXML
    public Text sys_txt1;
    @FXML
    public Text sys_txt4;
    @FXML
    public Text sys_txt2;
    public MenuItem exportPDFbtn;
    public Button deselect;
    @FXML
    TextField first_name, last_name, department,  email, imageURL;
    @FXML
    ImageView img_view;
    @FXML
    public MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();
    PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
    Image image = new Image("file:C:/Users/matth/OneDrive/Desktop/Coding/CSC311_DB_UI_semesterlongproject/src/main/resources/images/riot.png");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            pause.setOnFinished(event -> {
                sys_txt2.setText("");
            });
            tv.setEditable(true);

            tv_fn.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_fn.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                String newValue = event.getNewValue();
                cnUtil.editUser(person.getId(), "first_name", newValue);
                person.setFirstName(newValue);
                tv_fn.getTableView().refresh();
                first_name.setText(newValue);
            });

            tv_ln.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_ln.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                String newValue = event.getNewValue();
                cnUtil.editUser(person.getId(), "last_name", newValue);
                person.setLastName(newValue);
                tv_ln.getTableView().refresh();
                last_name.setText(newValue);
            });

            tv_department.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_department.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                String newValue = event.getNewValue();
                cnUtil.editUser(person.getId(), "department", newValue);
                person.setDepartment(newValue);
                tv_department.getTableView().refresh();
                department.setText(newValue);
            });

            tv_major.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_major.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                String newValue = event.getNewValue();
                cnUtil.editUser(person.getId(), "major", newValue);
                person.setMajor(newValue);
                tv_major.getTableView().refresh();
                major2.setValue(newValue);
            });

            tv_email.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_email.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                String newValue = event.getNewValue();
                cnUtil.editUser(person.getId(), "email", newValue);
                person.setEmail(newValue);
                tv_email.getTableView().refresh();
                email.setText(newValue);
            });


            img_view.setImage(image);



            major2.getItems().add("Position");
            major2.getItems().addAll(Major.values());
            major2.getSelectionModel().selectFirst();

            deselect.setDisable(true);
            editBtn.setDisable(true);
            deleteBtn.setDisable(true);
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void OpenFile(ActionEvent actionEvent) {
        sys_txt2.setText("Loading...");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File f = fileChooser.showOpenDialog(menuBar.getScene().getWindow());

        if (f != null) {
            Task<Void> loadFileTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] fields = line.split(",");
                            if (fields.length >= 5) {
                                String firstName = fields[0];
                                String lastName = fields[1];
                                String department = fields[2];
                                String major = fields[3];
                                String email = fields[4];

                                Person p = new Person(firstName, lastName, department, major, email);
                                cnUtil.insertUser(p);
                                p.setId(cnUtil.retrieveId(p));
                                data.add(p);

                                System.out.println(p);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            loadFileTask.setOnSucceeded(e -> {
                sys_txt2.setText("Import Complete!");
                sys_txt2.setFill(Color.GREEN);
                pause.play();
            });

            new Thread(loadFileTask).start();
        }
    }

    @FXML
    public void exportFile(ActionEvent actionEvent) {


        StringBuilder file = new StringBuilder();


        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File f = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
        if(f!=null) {
            for (int i = 0; i < data.size(); i++) {
                file.append(data.get(i).getId()).append(",");
                file.append(data.get(i).getFirstName()).append(',');
                file.append(data.get(i).getLastName()).append(',');
                file.append(data.get(i).getDepartment()).append(',');
                file.append(data.get(i).getMajor()).append(',');
                file.append(data.get(i).getEmail());
                file.append("\n");
            }
            System.out.println(file);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
                writer.write(file.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No file selected");
        }
        }




    @FXML
    protected void addNewRecord() {

            if(isFormValid()) {
                Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                        major2.getValue().toString(), email.getText());
                cnUtil.insertUser(p);
                cnUtil.retrieveId(p);
                p.setId(cnUtil.retrieveId(p));
                data.add(p);
                clearForm();
                incorrectField.setText("");
                sys_txt2.setText("Add Successful");
                sys_txt2.setFill(Color.GREEN);
                pause.play();

            }
            else{
                incorrectField.setText("One or more fields entered incorrectly, please try again");
                System.out.println("Invalid");
                sys_txt2.setText("Addition Fail");
                sys_txt2.setFill(Color.RED);
                pause.play();

            }


    }

    private boolean isFormValid(){

        String nameRegex = "^[a-zA-Z\\s\\-'.]{1,100}$";
        String deptMajorRegex = "^[a-zA-Z0-9\\s&,'-().]{1,150}$";
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}$";

        boolean isValidFirstName = first_name.getText().matches(nameRegex);
        boolean isValidLastName = last_name.getText().matches(nameRegex);
        boolean isValidDepartment = department.getText().matches(deptMajorRegex);
        boolean isValidMajor = !Objects.equals(major2.getValue().toString(), "Major");
        boolean isValidEmail = email.getText().matches(emailRegex);

        return isValidFirstName && isValidLastName && isValidMajor && isValidDepartment && isValidEmail;
    }


    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
       // major.setText("");
        email.setText("");

    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.setTitle("About");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void editRecord() {

            Person p = tv.getSelectionModel().getSelectedItem();
            if(p != null && isFormValid()) {

                int index = data.indexOf(p);
                Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                        major2.getValue().toString(), email.getText());
                cnUtil.editUser(p.getId(), p2);
                data.remove(p);
                data.add(index, p2);
                tv.getSelectionModel().select(index);
                incorrectField.setText("");
                sys_txt2.setText("Edit Successful");
                sys_txt2.setFill(Color.GREEN);
                pause.play();
            }
            else{
                incorrectField.setText("One or more fields entered incorrectly, please try again");
                System.out.println("Invalid");
                sys_txt2.setText("Edit Unsuccessful");
                sys_txt2.setFill(Color.RED);
                pause.play();

            }


    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        if(p != null) {

        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
            sys_txt2.setText("Record Deleted");
            sys_txt2.setFill(Color.RED);
            editBtn.setDisable(true);
            deleteBtn.setDisable(true);
            pause.play();

        }


    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        // Get the selected person from the table view
        Person selectedPerson = tv.getSelectionModel().getSelectedItem();

        if (selectedPerson != null) {
            // Enable buttons and populate fields if a valid person is selected
            editBtn.setDisable(false);
            deleteBtn.setDisable(false);
            deselect.setDisable(false);
            first_name.setText(selectedPerson.getFirstName());
            last_name.setText(selectedPerson.getLastName());
            department.setText(selectedPerson.getDepartment());
            major2.setValue(selectedPerson.getMajor()); // Assuming major2 is a ComboBox
            email.setText(selectedPerson.getEmail());
            // Uncomment and populate imageURL if needed
            // imageURL.setText(selectedPerson.getImageURL());
        }


        if (mouseEvent.getClickCount() == 2) {
            Random rand = new Random();


            if (selectedPerson == null) {

                Person newPerson = new Person("", "", "", "", ""); // Adjust constructor as needed


                int index = data.size();
                Person p2 = new Person(index + 1, "Placeholder", "Placeholder", "Placeholder",
                        "Placeholder", "Placeholder" + (index + rand.nextInt()));


                cnUtil.insertUser(p2);


                data.add(p2);
                first_name.setText(p2.getFirstName());
                last_name.setText(p2.getLastName());
                department.setText(p2.getDepartment());
                major2.setValue(p2.getMajor());
                email.setText(p2.getEmail());


                Platform.runLater(() -> {
                    tv.scrollTo(p2);
                    tv.getSelectionModel().select(p2);
                    tv.edit(data.indexOf(p2), tv_fn);
                });


                incorrectField.setText("");
                sys_txt2.setText("Edit Button to Edit The New User");
                sys_txt2.setFill(Color.GREEN);
                pause.play();
            }
        }
    }



    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }


    @FXML
    protected void exportPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File pdfFile = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

        if (pdfFile != null) {
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                Document document = new Document();
                PdfWriter.getInstance(document, fos);
                document.open();

                // Add Title
                document.add(new Paragraph("Database Export", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(new Paragraph("\n"));

                // Add Table
                PdfPTable pdfTable = new PdfPTable(6); // 6 columns for the fields
                pdfTable.addCell("ID");
                pdfTable.addCell("First Name");
                pdfTable.addCell("Last Name");
                pdfTable.addCell("Department");
                pdfTable.addCell("Major");
                pdfTable.addCell("Email");

                for (Person person : data) {
                    pdfTable.addCell(String.valueOf(person.getId()));
                    pdfTable.addCell(person.getFirstName());
                    pdfTable.addCell(person.getLastName());
                    pdfTable.addCell(person.getDepartment());
                    pdfTable.addCell(person.getMajor());
                    pdfTable.addCell(person.getEmail());
                }

                document.add(pdfTable);
                document.close();

                sys_txt2.setText("PDF Exported Successfully!");
                sys_txt2.setFill(Color.GREEN);
                pause.play();
            } catch (Exception e) {
                sys_txt2.setText("PDF Export Failed!");
                sys_txt2.setFill(Color.RED);
                pause.play();
                e.printStackTrace();
            }
        }
    }


    @FXML
    protected void deselect() {
        tv.getSelectionModel().clearSelection();
        editBtn.setDisable(true);
        deleteBtn.setDisable(true);
        deselect.setDisable(true);
        clearForm();
        incorrectField.setText("");
        sys_txt2.setText("Selection cleared");
        sys_txt2.setFill(Color.BLUE);
        pause.play();
    }


    private static enum Major {Intern, Manager, Executive, Director, Programmer, Associate,Technician, Artist}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }

}