package controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import model.Album;
import model.ManageUser;
import model.Photo;
import model.SearchHelper;
import model.Tag;
import model.User;

/**
 * @author xudong jiang
 * @author yuting chen
 */
public class SearchController implements Initializable {
    private Stage tmpStage;
    private ManageUser mgUsr;
    private User currentUser;
    private Album currentAlbum;
    private String userName;
    private boolean windowFromAlbum;
    @FXML
    private Button backBT;
    @FXML
    private Button tagSearchBT;
    @FXML
    private Button dateSearchBT;
    @FXML
    private RadioButton oneTypeRB;
    @FXML
    private RadioButton twoTypeRB;
    @FXML
    private RadioButton andRB;
    @FXML
    private RadioButton orRB;
    @FXML
    private RadioButton tagCreatetAlbumRB;
    @FXML
    private RadioButton dateCreatetAlbumRB;
    @FXML
    private RadioButton dateRB;
    @FXML
    private RadioButton tagRB;

    @FXML
    private Label fromLB;
    @FXML
    private Label toLB;
    @FXML
    private DatePicker fromDP;
    @FXML
    private DatePicker toDP;
    @FXML
    private TextField oneTF;
    @FXML
    private TextField twoTF;
    @FXML
    private ComboBox<String> oneCB;
    @FXML
    private ComboBox<String> twoCB;
    private ObservableList<String> options;
    ArrayList<Photo> searched;

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void dateSearchBT_handler(ActionEvent e) throws Exception {
        String s;
        Album searchAlbum = new Album("Search Result");
        try {
            s = fromDP.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception b) {
            Alert error = new Alert(AlertType.ERROR, "Please enter 'from' date with proper fromat", ButtonType.OK);
            error.showAndWait();
            return;
        }
        if (s.length() == 0) {
            Alert error = new Alert(AlertType.ERROR, "Please enter 'from' date", ButtonType.OK);
            error.showAndWait();
            return;
        }

        Calendar from = new GregorianCalendar(Integer.parseInt(s.substring(0, 4)),
                Integer.parseInt(s.substring(5, 7)) - 1, Integer.parseInt(s.substring(8, s.length())));

        try {
            s = toDP.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception b) {
            Alert error = new Alert(AlertType.ERROR, "Please enter 'to' date with proper fromat", ButtonType.OK);
            error.showAndWait();
            return;
        }
        if (s.length() == 0) {
            Alert error = new Alert(AlertType.ERROR, "Please enter 'to' date", ButtonType.OK);
            error.showAndWait();
            return;
        }
        Calendar to = new GregorianCalendar(Integer.parseInt(s.substring(0, 4)),
                Integer.parseInt(s.substring(5, 7)) - 1, Integer.parseInt(s.substring(8, s.length())), 24, 00, 00);

        if (windowFromAlbum == true) {
            int cnt = 0;
            for (Album a : currentUser.albums) {
                for (Photo p : a.photos) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(p.getDate());
                    if (c.compareTo(from) >= 0 && c.compareTo(to) <= 0) {
                        SearchHelper.getAlbum.add(a);
                        SearchHelper.getPhoto.add(p);
                        searchAlbum.photos.add(p);
                        cnt++;

                    }

                }
            }
            if (cnt == 0) {
                Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
                error.showAndWait();
                return;
            } else {
                String name = null;
                if (dateCreatetAlbumRB.isSelected()) {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.initOwner(tmpStage);
                    dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
                    dialog.setTitle("Add Album");
                    dialog.setContentText("Name: ");
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent())
                        name = result.get();
                    else
                        return;
                    searchAlbum.setAlbumName(name);
                    currentUser.albums.add(searchAlbum);
                    mgUsr.conductSerializing();

                    FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
                    Parent parent = (Parent) photoAlbumScene.load();
                    PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                    Scene photoAlbumControllerScene = new Scene(parent);
                    Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    photoAlbum.start(window, searchAlbum, userName, false);
                    window.setScene(photoAlbumControllerScene);
                    window.show();
                } else {
                    FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
                    Parent parent = (Parent) photoAlbumScene.load();
                    PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                    Scene photoAlbumControllerScene = new Scene(parent);
                    Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    photoAlbum.start(window, searchAlbum, userName, true);
                    window.setScene(photoAlbumControllerScene);
                    window.show();
                }
            }
        } else {
            int cnt = 0;
            for (Photo p : currentAlbum.photos) {
                Calendar c = Calendar.getInstance();
                c.setTime(p.getDate());
                if (c.compareTo(from) >= 0 && c.compareTo(to) <= 0) {
                    SearchHelper.getAlbum.add(currentAlbum);
                    SearchHelper.getPhoto.add(p);
                    searchAlbum.photos.add(p);
                    cnt++;
                }

            }
            if (cnt == 0) {
                Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
                error.showAndWait();
                return;
            } else {
                String name = null;
                if (dateCreatetAlbumRB.isSelected()) {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.initOwner(tmpStage);
                    dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
                    dialog.setTitle("Add Album");
                    dialog.setContentText("Name: ");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent())
                        name = result.get();
                    else
                        return;
                    searchAlbum.setAlbumName(name);
                    currentUser.albums.add(searchAlbum);
                    mgUsr.conductSerializing();

                    FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
                    Parent parent = (Parent) photoAlbumScene.load();
                    PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                    Scene photoAlbumControllerScene = new Scene(parent);
                    Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    photoAlbum.start(window, searchAlbum, userName, false);
                    window.setScene(photoAlbumControllerScene);
                    window.show();
                } else {
                    FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
                    Parent parent = (Parent) photoAlbumScene.load();
                    PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                    Scene photoAlbumControllerScene = new Scene(parent);
                    Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    photoAlbum.start(window, searchAlbum, userName, true);
                    window.setScene(photoAlbumControllerScene);
                    window.show();
                }
            }
        }
    }

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void tagSearchBT_handler(ActionEvent e) throws Exception {
        SearchHelper.getAlbum.clear();
        SearchHelper.getPhoto.clear();
        if (tagRB.isSelected()) {
            Album searchAlbum = new Album("search");
            if (oneTypeRB.isSelected()) {
                String getValue = oneTF.getText();
                if (getValue.trim().isEmpty()) {
                    Alert error = new Alert(AlertType.ERROR, "Please enter first value", ButtonType.OK);
                    error.showAndWait();
                    return;
                }
                Tag oneCmpTag = new Tag(oneCB.getValue(), oneTF.getText());
                if (windowFromAlbum == true) {
                    int cnt = 0;
                    for (Album a : currentUser.albums) {
                        for (Photo p : a.photos) {
                            if (p.tags.contains(oneCmpTag)) {
                                Photo copy = new Photo(p);
                                SearchHelper.getAlbum.add(a);
                                SearchHelper.getPhoto.add(p);
                                searchAlbum.photos.add(copy);
                                cnt++;
                            }
                        }
                    }
                    if (cnt == 0) {
                        Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
                        error.showAndWait();
                        return;
                    } else {
                        String name = null;
                        if (tagCreatetAlbumRB.isSelected()) {
                            TextInputDialog dialog = new TextInputDialog("");
                            dialog.initOwner(tmpStage);
                            dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
                            dialog.setTitle("Add Album");
                            dialog.setContentText("Name: ");

                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent())
                                name = result.get();
                            else
                                return;
                            searchAlbum.setAlbumName(name);
                            currentUser.albums.add(searchAlbum);
                            mgUsr.conductSerializing();

                            FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
                            Parent parent = (Parent) photoAlbumScene.load();
                            PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                            Scene photoAlbumControllerScene = new Scene(parent);
                            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            photoAlbum.start(window, searchAlbum, userName, false);
                            window.setScene(photoAlbumControllerScene);
                            window.show();
                        } else {
                            FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
                            Parent parent = (Parent) photoAlbumScene.load();
                            PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                            Scene photoAlbumControllerScene = new Scene(parent);
                            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            photoAlbum.start(window, searchAlbum, userName, true);
                            window.setScene(photoAlbumControllerScene);
                            window.show();
                        }

                    }
                } else {
                    int cnt = 0;
                    for (Photo p : currentAlbum.photos) {
                        if (p.tags.contains(oneCmpTag)) {
                            SearchHelper.getAlbum.add(currentAlbum);
                            SearchHelper.getPhoto.add(p);
                            Photo copy = new Photo(p);
                            searchAlbum.photos.add(copy);
                            cnt++;
                        }
                    }
                    if (cnt == 0) {
                        Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
                        error.showAndWait();
                        return;
                    } else {
                        String name = null;
                        if (tagCreatetAlbumRB.isSelected()) {
                            TextInputDialog dialog = new TextInputDialog("");
                            dialog.initOwner(tmpStage);
                            dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
                            dialog.setTitle("Add Album");
                            dialog.setContentText("Name: ");

                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent())
                                name = result.get();
                            else
                                return;
                            searchAlbum.setAlbumName(name);
                            currentUser.albums.add(searchAlbum);
                            mgUsr.conductSerializing();

                            FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
                            Parent parent = (Parent) photoAlbumScene.load();
                            PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                            Scene photoAlbumControllerScene = new Scene(parent);
                            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            photoAlbum.start(window, searchAlbum, userName, false);
                            window.setScene(photoAlbumControllerScene);
                            window.show();
                        } else {
                            FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
                            Parent parent = (Parent) photoAlbumScene.load();
                            PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                            Scene photoAlbumControllerScene = new Scene(parent);
                            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            photoAlbum.start(window, searchAlbum, userName, true);
                            window.setScene(photoAlbumControllerScene);
                            window.show();
                        }
                    }
                }

            } else if (twoTypeRB.isSelected()) {
                String getValue = twoTF.getText();
                String getValue1 = oneTF.getText();
                if (getValue.trim().isEmpty() && getValue1.trim().isEmpty()) {
                    Alert error = new Alert(AlertType.ERROR, "Please enter second value", ButtonType.OK);
                    error.showAndWait();
                    return;
                }
                Tag oneCmpTag = new Tag(oneCB.getValue(), oneTF.getText());
                Tag twoCmpTag = new Tag(twoCB.getValue(), twoTF.getText());

                if (windowFromAlbum == true) { // from album
                    if (andRB.isSelected()) {
                        int cnt = 0;
                        for (Album a : currentUser.albums) {
                            for (Photo p : a.photos) {
                                if (p.tags.contains(oneCmpTag) && p.tags.contains(twoCmpTag)) {
                                    SearchHelper.getAlbum.add(a);
                                    SearchHelper.getPhoto.add(p);
                                    Photo copy = new Photo(p);
                                    searchAlbum.photos.add(copy);
                                    cnt++;
                                }
                            }
                        }
                        if (cnt == 0) {
                            Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
                            error.showAndWait();
                            return;
                        } else {
                            String name = null;
                            if (tagCreatetAlbumRB.isSelected()) {
                                TextInputDialog dialog = new TextInputDialog("");
                                dialog.initOwner(tmpStage);
                                dialog.getDialogPane().getScene().getWindow()
                                        .setOnCloseRequest(event -> event.consume());
                                dialog.setTitle("Add Album");
                                dialog.setContentText("Name: ");

                                Optional<String> result = dialog.showAndWait();
                                if (result.isPresent())
                                    name = result.get();
                                else
                                    return;
                                searchAlbum.setAlbumName(name);
                                currentUser.albums.add(searchAlbum);
                                mgUsr.conductSerializing();

                                FXMLLoader photoAlbumScene = new FXMLLoader(
                                        getClass().getResource("/view/openAlbum.fxml"));
                                Parent parent = (Parent) photoAlbumScene.load();
                                PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                                Scene photoAlbumControllerScene = new Scene(parent);
                                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                photoAlbum.start(window, searchAlbum, userName, false);
                                window.setScene(photoAlbumControllerScene);
                                window.show();
                            } else {
                                FXMLLoader photoAlbumScene = new FXMLLoader(
                                        getClass().getResource("/view/openAlbum.fxml"));
                                Parent parent = (Parent) photoAlbumScene.load();
                                PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                                Scene photoAlbumControllerScene = new Scene(parent);
                                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                photoAlbum.start(window, searchAlbum, userName, true);
                                window.setScene(photoAlbumControllerScene);
                                window.show();
                            }
                        }
                    } else if (orRB.isSelected()) {
                        int cnt = 0;
                        for (Album a : currentUser.albums) {
                            for (Photo p : a.photos) {
                                if (p.tags.contains(oneCmpTag) || p.tags.contains(twoCmpTag)) {
                                    SearchHelper.getAlbum.add(a);
                                    SearchHelper.getPhoto.add(p);
                                    Photo copy = new Photo(p);
                                    searchAlbum.photos.add(copy);
                                    cnt++;
                                }
                            }
                        }
                        if (cnt == 0) {
                            Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
                            error.showAndWait();
                            return;
                        } else {
                            String name = null;
                            if (tagCreatetAlbumRB.isSelected()) {
                                TextInputDialog dialog = new TextInputDialog("");
                                dialog.initOwner(tmpStage);
                                dialog.getDialogPane().getScene().getWindow()
                                        .setOnCloseRequest(event -> event.consume());
                                dialog.setTitle("Add Album");
                                dialog.setContentText("Name: ");

                                Optional<String> result = dialog.showAndWait();
                                if (result.isPresent())
                                    name = result.get();
                                else
                                    return;
                                searchAlbum.setAlbumName(name);
                                currentUser.albums.add(searchAlbum);
                                mgUsr.conductSerializing();

                                FXMLLoader photoAlbumScene = new FXMLLoader(
                                        getClass().getResource("/view/openAlbum.fxml"));
                                Parent parent = (Parent) photoAlbumScene.load();
                                PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                                Scene photoAlbumControllerScene = new Scene(parent);
                                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                photoAlbum.start(window, searchAlbum, userName, false);
                                window.setScene(photoAlbumControllerScene);
                                window.show();
                            } else {
                                FXMLLoader photoAlbumScene = new FXMLLoader(
                                        getClass().getResource("/view/openAlbum.fxml"));
                                Parent parent = (Parent) photoAlbumScene.load();
                                PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                                Scene photoAlbumControllerScene = new Scene(parent);
                                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                photoAlbum.start(window, searchAlbum, userName, true);
                                window.setScene(photoAlbumControllerScene);
                                window.show();
                            }
                        }
                    } else {
                        Alert error = new Alert(AlertType.ERROR, "Please select one of and / or", ButtonType.OK);
                        error.showAndWait();
                        return;
                    }
                } else {
                    if (andRB.isSelected()) {
                        int cnt = 0;
                        for (Photo p : currentAlbum.photos) {
                            if (p.tags.contains(oneCmpTag) && p.tags.contains(twoCmpTag)) {
                                SearchHelper.getAlbum.add(currentAlbum);
                                SearchHelper.getPhoto.add(p);

                                Photo copy = new Photo(p);
                                searchAlbum.photos.add(copy);
                                cnt++;
                            }
                        }
                        if (cnt == 0) {
                            Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
                            error.showAndWait();
                            return;
                        } else {
                            String name = null;
                            if (tagCreatetAlbumRB.isSelected()) {
                                TextInputDialog dialog = new TextInputDialog("");
                                dialog.initOwner(tmpStage);
                                dialog.getDialogPane().getScene().getWindow()
                                        .setOnCloseRequest(event -> event.consume());
                                dialog.setTitle("Add Album");
                                dialog.setContentText("Name: ");

                                Optional<String> result = dialog.showAndWait();
                                if (result.isPresent())
                                    name = result.get();
                                else
                                    return;
                                searchAlbum.setAlbumName(name);
                                currentUser.albums.add(searchAlbum);
                                mgUsr.conductSerializing();

                                FXMLLoader photoAlbumScene = new FXMLLoader(
                                        getClass().getResource("/view/openAlbum.fxml"));
                                Parent parent = (Parent) photoAlbumScene.load();
                                PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                                Scene photoAlbumControllerScene = new Scene(parent);
                                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                photoAlbum.start(window, searchAlbum, userName, false);
                                window.setScene(photoAlbumControllerScene);
                                window.show();
                            } else {
                                FXMLLoader photoAlbumScene = new FXMLLoader(
                                        getClass().getResource("/view/openAlbum.fxml"));
                                Parent parent = (Parent) photoAlbumScene.load();
                                PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                                Scene photoAlbumControllerScene = new Scene(parent);
                                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                photoAlbum.start(window, searchAlbum, userName, true);
                                window.setScene(photoAlbumControllerScene);
                                window.show();
                            }
                        }
                    } else if (orRB.isSelected()) {
                        int cnt = 0;
                        for (Photo p : currentAlbum.photos) {
                            if (p.tags.contains(oneCmpTag) || p.tags.contains(twoCmpTag)) {
                                SearchHelper.getAlbum.add(currentAlbum);
                                SearchHelper.getPhoto.add(p);
                                Photo copy = new Photo(p);
                                searchAlbum.photos.add(copy);
                                cnt++;
                            }
                        }

                        if (cnt == 0) {
                            Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
                            error.showAndWait();
                            return;
                        } else {
                            String name = null;
                            if (tagCreatetAlbumRB.isSelected()) {
                                TextInputDialog dialog = new TextInputDialog("");
                                dialog.initOwner(tmpStage);
                                dialog.getDialogPane().getScene().getWindow()
                                        .setOnCloseRequest(event -> event.consume());
                                dialog.setTitle("Add Album");
                                dialog.setContentText("Name: ");

                                Optional<String> result = dialog.showAndWait();
                                if (result.isPresent())
                                    name = result.get();
                                else
                                    return;
                                searchAlbum.setAlbumName(name);
                                currentUser.albums.add(searchAlbum);
                                mgUsr.conductSerializing();

                                FXMLLoader photoAlbumScene = new FXMLLoader(
                                        getClass().getResource("/view/openAlbum.fxml"));
                                Parent parent = (Parent) photoAlbumScene.load();
                                PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                                Scene photoAlbumControllerScene = new Scene(parent);
                                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                photoAlbum.start(window, searchAlbum, userName, false);
                                window.setScene(photoAlbumControllerScene);
                                window.show();
                            } else {
                                FXMLLoader photoAlbumScene = new FXMLLoader(
                                        getClass().getResource("/view/openAlbum.fxml"));
                                Parent parent = (Parent) photoAlbumScene.load();
                                PhotoAlbumController photoAlbum = photoAlbumScene.getController();
                                Scene photoAlbumControllerScene = new Scene(parent);
                                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                photoAlbum.start(window, searchAlbum, userName, true);
                                window.setScene(photoAlbumControllerScene);
                                window.show();
                            }
                        }
                    } else {
                        Alert error = new Alert(AlertType.ERROR, "Please select one of and / or", ButtonType.OK);
                        error.showAndWait();
                        return;
                    }
                }

            } else {
                Alert error = new Alert(AlertType.ERROR, "Please select one type or two type", ButtonType.OK);
                error.showAndWait();
                return;
            }
        } else {
            Alert error = new Alert(AlertType.ERROR, "Please select Tag or Date", ButtonType.OK);
            error.showAndWait();
            return;
        }

    }

    /**
     * 
     */
    @FXML
    private void dateSearch_handler() {
        if (dateRB.isSelected()) {
            selectdateSMethod(false);
            selectSearchMethod(true);
        }
    }

    /**
     * 
     */
    @FXML
    private void tagSearch_handler() {
        if (tagRB.isSelected()) {
            selectSearchMethod(false);
            selectdateSMethod(true);
        }
    }

    /**
     * 
     */
    @FXML
    private void oneTypeRB_handler() {
        if (oneTypeRB.isSelected()) {
            setDisableMethod(true);
        }
    }

    /**
     * 
     */
    @FXML
    private void twoTypeRB_handler() {
        if (twoTypeRB.isSelected()) {
            setDisableMethod(false);
        }
    }

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void backBT_handler(ActionEvent e) throws Exception {
        mgUsr.conductSerializing();
        if (windowFromAlbum == true) {
            FXMLLoader albumScene = new FXMLLoader(getClass().getResource("/view/album.fxml"));
            Parent parent = (Parent) albumScene.load();
            AlbumController album = albumScene.getController();
            Scene albumControllerScene = new Scene(parent);
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            album.start(window, userName);
            window.setScene(albumControllerScene);
            window.show();
        } else {
            FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
            Parent parent = (Parent) photoAlbumScene.load();
            PhotoAlbumController photoAlbum = photoAlbumScene.getController();
            Scene photoAlbumControllerScene = new Scene(parent);
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            photoAlbum.start(window, currentAlbum, userName, false);
            window.setScene(photoAlbumControllerScene);
            window.show();
        }

    }

    /**
     * @param tf
     */
    private void selectdateSMethod(boolean tf) {
        fromLB.setDisable(tf);
        fromDP.setDisable(tf);
        toLB.setDisable(tf);
        toDP.setDisable(tf);
        dateCreatetAlbumRB.setDisable(tf);
        dateSearchBT.setDisable(tf);
    }

    /**
     * @param tf
     */
    private void selectSearchMethod(boolean tf) {
        setDisableMethod(tf);
        oneTF.setDisable(tf);
        oneCB.setDisable(tf);
        tagCreatetAlbumRB.setDisable(tf);
        oneTypeRB.setDisable(tf);
        twoTypeRB.setDisable(tf);
        tagSearchBT.setDisable(tf);
    }

    /**
     * @param tf
     */
    private void setDisableMethod(boolean tf) {
        andRB.setDisable(tf);
        orRB.setDisable(tf);
        twoTF.setDisable(tf);
        twoCB.setDisable(tf);
    }

    /**
     * @param mainStage
     * @param user
     * @param album
     * @param windowFromAlbum
     */
    public void start(Stage mainStage, String user, Album album, boolean windowFromAlbum) {
        tmpStage = mainStage;
        this.windowFromAlbum = windowFromAlbum;
        mgUsr = mgUsr.getInstance();
        mgUsr.conductDeserializing();
        userName = user;
        currentUser = mgUsr.getUser(user);

        if (album != null) {
            currentAlbum = mgUsr.getUser(user).getSpecificAlbum(album.getAlbumName());
        }
        options = FXCollections.observableArrayList(currentUser.tagType);
        oneCB.setItems(options);
        twoCB.setItems(options);
        oneCB.getSelectionModel().selectFirst();
        twoCB.getSelectionModel().selectFirst();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                mgUsr.conductSerializing();
            }
        });
    }

    /**
     *
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }
}
