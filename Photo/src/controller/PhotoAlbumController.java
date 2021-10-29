package controller;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import model.Album;
import model.ManageUser;
import model.Photo;
import model.SearchHelper;
import model.User;

/**
 * @author xudong jiang
 * @author yuting chen
 *
 */
public class PhotoAlbumController implements Initializable {

    private ManageUser mgUsr;
    private Album currentAlbum;
    private User currentUser;
    private Photo tempPhoto;
    private Album tempAlbum;
    private String userName;
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Stage tmpStage;
    private boolean searchResult;
    @FXML
    private Button backBT;
    @FXML
    private Button addBT;
    @FXML
    private Button deleteBT;
    @FXML
    private Button openBT;
    @FXML
    private Button moveBT;
    @FXML
    private Button copyBT;
    @FXML
    private Button recaptionBT;
    @FXML
    private Button logoutBT;
    @FXML
    private Button searchBT;
    @FXML
    private Text AL;
    @FXML
    private ListView<Photo> list;
    ArrayList<Photo> arrlist;
    private ObservableList<Photo> obs;

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void searchBT_handler(ActionEvent e) throws Exception {
        FXMLLoader searchScene = new FXMLLoader(getClass().getResource("/view/dateSearch.fxml"));
        Parent parent = (Parent) searchScene.load();
        SearchController search = searchScene.getController();
        Scene photoControllerScene = new Scene(parent);
        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        search.start(window, currentUser.getUserName(), currentAlbum, false);
        window.setScene(photoControllerScene);
        window.show();
    }

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void addBT_handler(ActionEvent e) throws Exception {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("add photo");
        File file = chooser.showOpenDialog(null);
        chooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.png", "*.jpeg", "jpg"));

        if (file != null) {
            String imagepath = file.toURI().toURL().toString();
            for (Photo a : currentAlbum.photos) {
                String[] splitUrl = imagepath.split("/");
                String[] splitUrlPhoto = a.getUrl().split("/");
                if (splitUrlPhoto[splitUrlPhoto.length - 1].equals(splitUrl[splitUrl.length - 1])) {
                    Alert warning = new Alert(AlertType.WARNING,
                            "Selected name of photo already exist in the list\nDo you want to add duplicate?",
                            ButtonType.YES, ButtonType.NO);
                    warning.showAndWait();
                    if (warning.getResult() == ButtonType.YES)
                        break;
                    if (warning.getResult() == ButtonType.NO) {
                        return;
                    }
                }
            }

            TextInputDialog dialog = new TextInputDialog("");
            dialog.initOwner(tmpStage);
            dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
            dialog.setTitle("Add Photo");
            dialog.setContentText("Caption: ");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent())
                addToArrayList(imagepath, result.get());
            else
                return;
        } else {
            Alert error = new Alert(AlertType.INFORMATION, "Nothing Selected.", ButtonType.OK);
            error.showAndWait();
        }
    }

    /**
     * @param e
     */
    @FXML
    private void moveBT_handler(ActionEvent e) {

        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly", ButtonType.OK);
            error.showAndWait();
        } else {
            ListView<Album> movinglistview = new ListView<Album>();
            Button okBT = new Button("Ok");
            ObservableList<Album> tmpObs = FXCollections.observableArrayList(currentUser.albums);

            movinglistview.setItems(tmpObs);
            movinglistview.setCellFactory(new Callback<ListView<Album>, ListCell<Album>>() {
                /**
                 *
                 */
                @Override
                public ListCell<Album> call(ListView<Album> p) {

                    ListCell<Album> cell = new ListCell<Album>() {

                        @Override
                        protected void updateItem(Album s, boolean bln) {
                            super.updateItem(s, bln);
                            if (s != null) {
                                setText(s.getAlbumName());
                            } else
                                setText("");
                        }
                    };
                    return cell;
                }
            });
            VBox root = new VBox(10, movinglistview, okBT);
            Stage newDialog = new Stage();
            newDialog.setTitle("Move Photo");
            Scene newDialogScene = new Scene(root);
            newDialog.setScene(newDialogScene);
            newDialog.initModality(Modality.WINDOW_MODAL);
            newDialog.initOwner(tmpStage);
            newDialog.show();
            okBT.setOnAction(new EventHandler<ActionEvent>() {
                /**
                 *
                 */
                @Override
                public void handle(ActionEvent e) {
                    if (movinglistview.getSelectionModel().getSelectedIndex() == -1) {
                        Alert error = new Alert(AlertType.ERROR,
                                "Nothing Selected.\nPlease select correctly or add new Album", ButtonType.OK);
                        error.showAndWait();
                    } else {
                        String getAlbumName = movinglistview.getSelectionModel().getSelectedItem().getAlbumName();
                        if (getAlbumName != null)
                            newDialog.close();

                        Album toMoveAlbum = currentUser.getSpecificAlbum(getAlbumName);
                        if (currentAlbum.getAlbumName()
                                .equals(movinglistview.getSelectionModel().getSelectedItem().getAlbumName())) {
                            Alert error = new Alert(AlertType.ERROR, "You can't move to current album", ButtonType.OK);
                            error.showAndWait();
                        } else {
                            Photo movePhoto = list.getSelectionModel().getSelectedItem();
                            Photo copyPhoto = null;
                            copyPhoto = new Photo(movePhoto);

                            int sidx = currentAlbum.getIndex(movePhoto);
                            if (searchResult) {
                                tempAlbum = currentUser
                                        .getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName());
                                tempPhoto = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName())
                                        .getSpecificPhoto(movePhoto);
                            }

                            Date time = new Date();
                            if (searchResult)
                                tempPhoto.setDate(time);
                            copyPhoto.setDate(time);

                            int idx = list.getSelectionModel().getSelectedIndex();
                            for (Photo a : toMoveAlbum.photos) {
                                String[] splitUrl = movePhoto.getUrl().split("/");
                                String[] splitUrlPhoto = a.getUrl().split("/");
                                if (splitUrlPhoto[splitUrlPhoto.length - 1].equals(splitUrl[splitUrl.length - 1])) {
                                    Alert warning = new Alert(AlertType.WARNING,
                                            "Selected name of photo already exist in the list\nDo you want to move anyway?",
                                            ButtonType.YES, ButtonType.NO);
                                    warning.showAndWait();
                                    if (warning.getResult() == ButtonType.YES)
                                        break;
                                    if (warning.getResult() == ButtonType.NO) {
                                        return;
                                    }
                                }
                            }

                            toMoveAlbum.photos.add(copyPhoto);
                            obs.remove(idx);
                            currentAlbum.photos.remove(idx);
                            if (searchResult) {
                                int ridx = tempAlbum.getIndex(tempPhoto);
                                tempAlbum.photos.remove(ridx);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * @param e
     */
    @FXML
    private void copyBT_handler(ActionEvent e) {
        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly", ButtonType.OK);
            error.showAndWait();
        } else {
            ListView<Album> movinglistview = new ListView<Album>();
            Button okBT = new Button("Ok");
            ObservableList<Album> tmpObs = FXCollections.observableArrayList(currentUser.albums);

            movinglistview.setItems(tmpObs);
            movinglistview.setCellFactory(new Callback<ListView<Album>, ListCell<Album>>() {

                @Override
                public ListCell<Album> call(ListView<Album> p) {

                    ListCell<Album> cell = new ListCell<Album>() {

                        @Override
                        protected void updateItem(Album s, boolean bln) {
                            super.updateItem(s, bln);
                            if (s != null) {
                                setText(s.getAlbumName());
                            } else
                                setText("");
                        }
                    };
                    return cell;
                }
            });
            VBox root = new VBox(10, movinglistview, okBT);
            Stage newDialog = new Stage();
            newDialog.setTitle("Copy Photo");
            Scene newDialogScene = new Scene(root);
            newDialog.setScene(newDialogScene);
            newDialog.initModality(Modality.WINDOW_MODAL);
            newDialog.initOwner(tmpStage);
            newDialog.show();

            okBT.setOnAction(new EventHandler<ActionEvent>() {

                /**
                 *
                 */
                @Override
                public void handle(ActionEvent e) {
                    if (movinglistview.getSelectionModel().getSelectedIndex() == -1) {
                        Alert error = new Alert(AlertType.ERROR,
                                "Nothing Selected.\nPlease select correctly or add new Album", ButtonType.OK);
                        error.showAndWait();
                    } else {
                        String getAlbumName = movinglistview.getSelectionModel().getSelectedItem().getAlbumName();
                        if (getAlbumName != null)
                            newDialog.close();

                        Album toMoveAlbum = currentUser.getSpecificAlbum(getAlbumName);
                        if (currentAlbum.getAlbumName()
                                .equals(movinglistview.getSelectionModel().getSelectedItem().getAlbumName())) {
                            Alert error = new Alert(AlertType.ERROR, "You can't copy to current album", ButtonType.OK);
                            error.showAndWait();
                        } else {
                            Photo movePhoto = list.getSelectionModel().getSelectedItem();
                            Photo copyPhoto = null;
                            copyPhoto = new Photo(movePhoto);
                            Date time = new Date();
                            copyPhoto.setDate(time);

                            for (Photo a : toMoveAlbum.photos) {
                                String[] splitUrl = copyPhoto.getUrl().split("/");
                                String[] splitUrlPhoto = a.getUrl().split("/");
                                if (splitUrlPhoto[splitUrlPhoto.length - 1].equals(splitUrl[splitUrl.length - 1])) {
                                    Alert warning = new Alert(AlertType.WARNING,
                                            "Selected name of photo already exist in the list\nDo you want to copy anyway?",
                                            ButtonType.YES, ButtonType.NO);
                                    warning.showAndWait();
                                    if (warning.getResult() == ButtonType.YES)
                                        break;
                                    if (warning.getResult() == ButtonType.NO) {
                                        return;
                                    }
                                }
                            }
                            toMoveAlbum.photos.add(copyPhoto);
                        }
                    }
                }
            });
        }
    }

    /**
     * @param e
     */
    @FXML
    private void recaptionBT_handler(ActionEvent e) {
        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Photo",
                    ButtonType.OK);
            error.showAndWait();
        } else {
            Photo item = list.getSelectionModel().getSelectedItem();
            currentAlbum.getSpecificPhoto(item);

            int index = list.getSelectionModel().getSelectedIndex();
            TextInputDialog dialog = new TextInputDialog(item.getCaption());
            dialog.initOwner(tmpStage);
            dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
            dialog.setTitle("Edit photo caption");
            dialog.setContentText("Caption: ");

            Photo recapPhoto = list.getSelectionModel().getSelectedItem();
            int sidx = currentAlbum.getIndex(recapPhoto);
            if (searchResult) {
                tempAlbum = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName());
                tempPhoto = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName())
                        .getSpecificPhoto(recapPhoto);
            }

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                currentAlbum.photos.get(index).setCaption(result.get());
                if (searchResult)
                    tempAlbum.photos.get(index).setCaption(result.get());
            } else
                return;

            Date time = new Date();
            currentAlbum.getSpecificPhoto(item).setDate(time);
            if (searchResult)
                tempAlbum.photos.get(index).setCaption(result.get());

            if (result.isPresent())
                obs.set(index, item);
        }
    }

    /**
     * @param e
     */
    @FXML
    private void deleteBT_handler(ActionEvent e) {
        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Photo",
                    ButtonType.OK);
            error.showAndWait();
        } else {
            Alert warning = new Alert(AlertType.WARNING, "Delete this Photo from list?", ButtonType.YES, ButtonType.NO);
            warning.showAndWait();
            if (warning.getResult() == ButtonType.NO) {
                return;
            }

            Photo deletePhoto = list.getSelectionModel().getSelectedItem();
            int sidx = currentAlbum.getIndex(deletePhoto); // need to know which photo is. (from search)
            if (searchResult) { // this is for changing all information when user search photo. Which means that
                                // if searched photos are changed, then originals are also changed.
                tempAlbum = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName());
                tempPhoto = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName())
                        .getSpecificPhoto(deletePhoto);
            }

            int idx = list.getSelectionModel().getSelectedIndex();
            obs.remove(idx);
            currentAlbum.photos.remove(idx);
            if (searchResult) {
                int ridx = tempAlbum.getIndex(tempPhoto);
                tempAlbum.photos.remove(ridx);
            }
        }
    }

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void logoutBT_handler(ActionEvent e) throws Exception {
        mgUsr.conductSerializing();

        FXMLLoader loginScene = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Parent parent = (Parent) loginScene.load();
        LoginController login = loginScene.getController();
        Scene loginControllerScene = new Scene(parent);
        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        login.start(window);
        window.setScene(loginControllerScene);
        window.show();
    }

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void backBT_handler(ActionEvent e) throws Exception {
        mgUsr.conductSerializing();

        FXMLLoader albumScene = new FXMLLoader(getClass().getResource("/view/album.fxml"));
        Parent parent = (Parent) albumScene.load();
        AlbumController album = albumScene.getController();
        Scene albumControllerScene = new Scene(parent);
        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        album.start(window, userName);
        window.setScene(albumControllerScene);
        window.show();
    }

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void openBT_handler(ActionEvent e) throws Exception {
        mgUsr.conductSerializing();

        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Photo",
                    ButtonType.OK);
            error.showAndWait();
        } else {
            Photo item = list.getSelectionModel().getSelectedItem();
            FXMLLoader photoScene = new FXMLLoader(getClass().getResource("/view/photo.fxml"));
            Parent parent = (Parent) photoScene.load();
            PhotoController photo = photoScene.getController();
            Scene photoControllerScene = new Scene(parent);
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            photo.start(window, currentAlbum, item, currentUser.getUserName(), searchResult);
            window.setScene(photoControllerScene);
            window.show();
        }
    }

    /**
     * @param url
     * @param caption
     */
    private void addToArrayList(String url, String caption) {
        Photo newPhoto = new Photo(url, caption);
        Date time = new Date();
        newPhoto.setDate(time);

        currentAlbum.photos.add(newPhoto);
        obs = FXCollections.observableArrayList(currentAlbum.photos);

        setOnListView();
    }

    /**
     * 
     */
    private void setOnListView() {
        list.setItems(obs);
        list.setCellFactory(param -> new ListCell<Photo>() {
            private ImageView imageView = new ImageView();
            VBox v = new VBox();
            Pane p = new Pane();
            Label l = new Label("");
            Image image;
            {
                v.getChildren().addAll(imageView, l, p);
            }

            @Override
            public void updateItem(Photo inn, boolean empty) {
                super.updateItem(inn, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    image = new Image(inn.getUrl());
                    imageView.setFitHeight(183);
                    imageView.setFitWidth(278);
                    imageView.setImage(image);
                    l.setText(inn.toString());
                    setGraphic(v);
                }
            }
        });
        mgUsr.conductSerializing();
    }

    /**
     * @param url
     * @return
     */
    private boolean duplicationCheck(String url) {
        boolean ret = true;
        if (obs.isEmpty()) {
            ret = true;
        } else {
            for (int i = 0; i < obs.size(); i++) {
                if (obs.get(i).getUrl().compareTo(url) == 0) {
                    ret = false;
                    break;
                } else
                    ret = true;
            }
        }
        return ret;
    }

    /**
     *
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

    /**
     * @param mainStage
     * @param album
     * @param user
     * @param search
     */
    public void start(Stage mainStage, Album album, String user, boolean search) {
        tmpStage = mainStage;
        mgUsr = mgUsr.getInstance();
        mgUsr.conductDeserializing();
        userName = user;
        searchResult = search;

        currentUser = mgUsr.getUser(user);
        if (!search) {
            currentAlbum = mgUsr.getUser(user).getSpecificAlbum(album.getAlbumName());

        } else {
            currentAlbum = album;
        }
        obs = FXCollections.observableArrayList(currentAlbum.photos);
        setOnListView();

        if (searchResult) { // this is for changing all information when user search photo. Which means that
                            // if searched photos are changed, then originals are also changed.
            tempAlbum = currentUser.getSpecificAlbum(currentAlbum.getAlbumName());
        }

        AL.setText(album.getAlbumName());
        if (search) {
            addBT.setDisable(true);
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                mgUsr.conductSerializing();
            }
        });
    }
}
