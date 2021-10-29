package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.ManageUser;
import model.Photo;
import model.SearchHelper;
import model.Tag;
import model.User;

/**
 * @author xudong jiang
 * @author yuting chen
 *
 */
public class PhotoController {

    private Stage tmpStage;
    private ManageUser mgUsr;
    private User currentUser;
    private Album currentAlbum;
    private Photo currentPhoto;
    private Photo tempPhoto;
    private Album tempAlbum;
    private boolean searchResult;
    private int idx;
    private int albumIdx;
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @FXML
    private Image image;
    @FXML
    private ImageView iv;
    @FXML
    private Button backBT;
    @FXML
    private Button captionBT;
    @FXML
    private Button rightNextBT;
    @FXML
    private Button leftPrevBT;
    @FXML
    private Button addTagBT;
    @FXML
    private Button deleteTagBT;
    @FXML
    private Button addTagTypeBT;
    @FXML
    private Button editTypeBT;
    @FXML
    private TextArea captionTA;
    @FXML
    private Text latestDate;

    @FXML
    private ListView<Tag> list;

    private ObservableList<Tag> obs;
    ObservableList<String> options;

    /**
     * @param e
     */
    @FXML
    private void addTagTypeBT_handler(ActionEvent e) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Tag Type");
        dialog.setHeaderText("Tag");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField = new TextField("");
        dialogPane.setContent(new VBox(8, textField));

        Platform.runLater(textField::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                if (textField.getText().trim().isEmpty()) {
                    Alert error = new Alert(AlertType.ERROR, "Please fill the value\nTRY AGAIN!", ButtonType.OK);
                    error.showAndWait();
                } else {
                    String check = textField.getText();
                    if (duplicationCheck(check))
                        return check;
                    else {
                        Alert error = new Alert(AlertType.ERROR, "Duplicate type is not allowed.", ButtonType.OK);
                        error.showAndWait();
                    }
                }
            }
            return null;
        });

        Optional<String> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((String results) -> {
            currentUser.tagType.add(results);
            options = FXCollections.observableArrayList(currentUser.tagType);
        });

        Date time = new Date();
        currentPhoto.setDate(time);
        if (searchResult)
            tempPhoto.setDate(time);
        String time1 = FORMAT.format(currentPhoto.getDate());
        latestDate.setText(time1);
    }

    /**
     * @param e
     */
    @FXML
    private void editTypeBT_handler(ActionEvent e) {

        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly", ButtonType.OK);
            error.showAndWait();
        } else {
            Dialog<Tag> dialog = new Dialog<>();
            dialog.setTitle("Tag");
            dialog.setHeaderText("Tag");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Tag getTag = list.getSelectionModel().getSelectedItem();
            int idxOfgetTag = currentPhoto.tags.indexOf(getTag);
            TextField textField = new TextField(getTag.getValue());

            ComboBox<String> comboBox = new ComboBox<>(options);
            final Label explain = new Label();
            comboBox.getSelectionModel().select(getTag.getkey());
            if (comboBox.getValue().equals(comboBox.getValue())) {
                explain.setText("Please enter " + comboBox.getValue());
                explain.setTextFill(Color.web("#0000FF"));
            }
            dialogPane.setContent(new VBox(8, comboBox, textField, explain));

            comboBox.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    if (comboBox.getValue().equals(comboBox.getValue())) {
                        explain.setText("Please enter " + comboBox.getValue());
                        explain.setTextFill(Color.web("#0000FF"));
                    }
                }
            });
            Platform.runLater(textField::requestFocus);
            dialog.setResultConverter((ButtonType button) -> {
                if (button == ButtonType.OK) {
                    if (textField.getText().trim().isEmpty()) {
                        Alert error = new Alert(AlertType.ERROR, "Please fill the value\nTRY AGAIN!", ButtonType.OK);
                        error.showAndWait();
                    } else {
                        Tag check = new Tag(comboBox.getValue().toString(), textField.getText());
                        if (duplicationCheck(check))
                            return check;
                        else {
                            Alert error = new Alert(AlertType.ERROR, "Duplicate value is not allowed.", ButtonType.OK);
                            error.showAndWait();
                        }
                    }

                }
                return null;
            });
            if (searchResult) {
                tempAlbum = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(idx).getAlbumName());
                tempPhoto = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(idx).getAlbumName())
                        .getSpecificPhoto(currentPhoto);
            }
            Optional<Tag> optionalResult = dialog.showAndWait();
            optionalResult.ifPresent((Tag results) -> {
                currentPhoto.getSpecificTag(getTag).setkey(results.getkey());
                currentPhoto.getSpecificTag(getTag).setValue(results.getValue());

                obs = FXCollections.observableArrayList(currentPhoto.tags);
                setOnListView();
            });
            if (searchResult) {
                tempPhoto.tags.get(idxOfgetTag).setkey(currentPhoto.getSpecificTag(getTag).getkey());
                tempPhoto.tags.get(idxOfgetTag).setValue(currentPhoto.getSpecificTag(getTag).getValue());

            }

            Date time = new Date();
            currentPhoto.setDate(time);
            if (searchResult)
                tempPhoto.setDate(time);
            String time1 = FORMAT.format(currentPhoto.getDate());
            latestDate.setText(time1);
        }

    }

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void addTagBT_handler(ActionEvent e) throws Exception {
        Dialog<Tag> dialog = new Dialog<>();
        dialog.setTitle("Tag");
        dialog.setHeaderText("Tag");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField = new TextField("");

        ComboBox<String> comboBox = new ComboBox<>(options);
        final Label explain = new Label();
        comboBox.getSelectionModel().selectFirst();
        explain.setText("Please enter person.");
        explain.setTextFill(Color.web("#0000FF"));
        dialogPane.setContent(new VBox(8, comboBox, textField, explain));

        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (comboBox.getValue().equals(comboBox.getValue())) {
                    explain.setText("Please enter " + comboBox.getValue());
                    explain.setTextFill(Color.web("#0000FF"));
                }
            }
        });
        Platform.runLater(textField::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                if (textField.getText().trim().isEmpty()) {
                    Alert error = new Alert(AlertType.ERROR, "Please fill the value\nTRY AGAIN!", ButtonType.OK);
                    error.showAndWait();
                } else {
                    Tag check = new Tag(comboBox.getValue().toString(), textField.getText());
                    if (duplicationCheck(check))
                        return check;
                    else {
                        Alert error = new Alert(AlertType.ERROR, "Duplicate value is not allowed.", ButtonType.OK);
                        error.showAndWait();
                    }
                }

            }
            return null;
        });

        Optional<Tag> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Tag results) -> {
            addToArrayList(results);

        });

        Date time = new Date();
        currentPhoto.setDate(time);
        if (searchResult)
            tempPhoto.setDate(time);
        String time1 = FORMAT.format(currentPhoto.getDate());
        latestDate.setText(time1);
    }

    /**
     * @param e
     */
    @FXML
    private void deleteTagBT_handler(ActionEvent e) {
        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly", ButtonType.OK);
            error.showAndWait();
        } else {
            Alert warning = new Alert(AlertType.WARNING, "Delete this Tag from list?", ButtonType.YES, ButtonType.NO);
            warning.showAndWait();
            if (warning.getResult() == ButtonType.NO) {
                return;
            }

            int idx = list.getSelectionModel().getSelectedIndex();
            obs.remove(idx);
            currentPhoto.tags.remove(idx);

            Date time = new Date();
            if (searchResult) {
                tempPhoto.tags.remove(idx);
                tempPhoto.setDate(time);
            }
            currentPhoto.setDate(time);
            String time1 = FORMAT.format(currentPhoto.getDate());
            latestDate.setText(time1);
        }
    }

    /**
     * @param e
     */
    @FXML
    private void rightNextBT_handler(ActionEvent e) {
        int indexforRight = currentAlbum.getIndex(currentPhoto);

        if (indexforRight < currentAlbum.getIdxPhotos() - 1) {
            currentPhoto = currentAlbum.getPhotoWithIndex(indexforRight + 1);
        }

        image = new Image(currentPhoto.getUrl());
        iv.setImage(image);

        captionTA.setText(currentPhoto.getCaption());
        obs = FXCollections.observableArrayList(currentPhoto.tags);
        setOnListView();
        String time1 = FORMAT.format(currentPhoto.getDate());
        latestDate.setText(time1);
    }

    /**
     * @param e
     */
    @FXML
    private void leftPrevBT_handler(ActionEvent e) {
        int indexforleft = currentAlbum.getIndex(currentPhoto);

        if (indexforleft > 0) {
            currentPhoto = currentAlbum.getPhotoWithIndex(indexforleft - 1);
        }

        image = new Image(currentPhoto.getUrl());
        iv.setImage(image);

        captionTA.setText(currentPhoto.getCaption());
        obs = FXCollections.observableArrayList(currentPhoto.tags);
        setOnListView();
        String time1 = FORMAT.format(currentPhoto.getDate());
        latestDate.setText(time1);
    }

    /**
     * @param e
     */
    @FXML
    private void captionBT_handler(ActionEvent e) {
        TextInputDialog dialog = new TextInputDialog(currentPhoto.getCaption());
        dialog.initOwner(tmpStage);
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
        dialog.setTitle("Edit photo caption");
        dialog.setContentText("Caption: ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            currentPhoto.setCaption(result.get());
            if (searchResult) {
                tempPhoto.setCaption(result.get());
            }
        } else
            return;
        Date time = new Date();
        if (searchResult)
            tempPhoto.setDate(time);
        currentPhoto.setDate(time);
        String time1 = FORMAT.format(currentPhoto.getDate());
        latestDate.setText(time1);

        captionTA.setText(currentPhoto.getCaption());
    }

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void backBT_handler(ActionEvent e) throws Exception {
        mgUsr.conductSerializing();

        FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
        Parent parent = (Parent) photoAlbumScene.load();
        PhotoAlbumController photoAlbum = photoAlbumScene.getController();
        Scene photoAlbumControllerScene = new Scene(parent);
        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        photoAlbum.start(window, currentAlbum, currentUser.getUserName(), searchResult);
        window.setScene(photoAlbumControllerScene);
        window.show();
    }

    /**
     * @param newTag
     */
    private void addToArrayList(Tag newTag) {
        if (searchResult) {
            tempPhoto.tags.add(newTag);
        }

        currentPhoto.tags.add(newTag);
        obs = FXCollections.observableArrayList(currentPhoto.tags);

        setOnListView();
    }

    /**
     * 
     */
    private void setOnListView() {
        list.setItems(obs);
        list.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {

            @Override
            public ListCell<Tag> call(ListView<Tag> p) {

                ListCell<Tag> cell = new ListCell<Tag>() {
                    @Override
                    protected void updateItem(Tag s, boolean bln) {
                        super.updateItem(s, bln);
                        if (s != null) {
                            setText(s.getkey() + ": " + s.getValue());
                        } else
                            setText("");
                    }
                };
                return cell;
            }

        });
    }

    /**
     * @param tag
     * @return
     */
    private boolean duplicationCheck(Tag tag) {
        boolean ret = true;
        if (tag == null) {
            ret = true;
        } else {
            for (Tag t : currentPhoto.tags) {
                if (t.getkey().toLowerCase().equals(tag.getkey().toLowerCase())
                        && t.getValue().toLowerCase().equals(tag.getValue().toLowerCase())) {
                    ret = false;
                    break;
                } else
                    ret = true;
            }
        }
        return ret;
    }

    /**
     * @param str
     * @return
     */
    private boolean duplicationCheck(String str) {
        boolean ret = true;
        if (str == null) {
            ret = true;
        } else {
            for (String s : currentUser.tagType) {
                if (s.equals(str)) {
                    ret = false;
                    break;
                } else
                    ret = true;
            }
        }
        return ret;
    }

    /**
     * @param mainStage
     * @param album
     * @param photo
     * @param user
     * @param search
     */
    public void start(Stage mainStage, Album album, Photo photo, String user, boolean search) {
        tmpStage = mainStage;

        mgUsr = mgUsr.getInstance();
        mgUsr.conductDeserializing();
        currentUser = mgUsr.getUser(user);
        searchResult = search;

        if (!search) {
            currentAlbum = mgUsr.getUser(user).getSpecificAlbum(album.getAlbumName());
            currentPhoto = mgUsr.getUser(user).getSpecificAlbum(album.getAlbumName()).getSpecificPhoto(photo);
        } else {
            currentAlbum = album;
            currentPhoto = album.getSpecificPhoto(photo);
        }

        idx = currentAlbum.getIndex(photo);
        if (searchResult) {
            tempAlbum = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(idx).getAlbumName());
            tempPhoto = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(idx).getAlbumName())
                    .getSpecificPhoto(currentPhoto);
        }

        image = new Image(currentPhoto.getUrl());
        iv.setImage(image);

        captionTA.setText(photo.getCaption());

        obs = FXCollections.observableArrayList(currentPhoto.tags);
        setOnListView();

        String time1 = FORMAT.format(currentPhoto.getDate());
        latestDate.setText(time1);

        options = FXCollections.observableArrayList(currentUser.tagType);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            /**
             *
             */
            public void run() {
                mgUsr.conductSerializing();
            }
        });
    }
}
