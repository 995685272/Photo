package controller;

import java.io.Serializable;
import java.net.URL;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.ManageUser;
import model.User;

/**
 * @author xudong jiang 
 * @author Yuting Chen
 *
 */
public class AlbumController implements Serializable, Initializable {

    private ManageUser mgUsr;
    private User currentUser;
    private String userName;
    private Stage tmpStage;
    @FXML
    private Button creteBT;
    @FXML
    private Button removeBT;
    @FXML
    private Button openBT;
    @FXML
    private Button renameBT;
    @FXML
    private Button logoutBT;
    @FXML
    private Button clearBT;
    @FXML
    private Button searchBT;
    @FXML
    private TextField albumNameTF;
    @FXML
    private Text ID;
    @FXML
    private ListView<Album> list;
    private ObservableList<Album> obs;

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
        Album album = null;
        search.start(window, currentUser.getUserName(), album, true);
        window.setScene(photoControllerScene);
        window.show();
    }

    /**
     * @param e
     */
    @FXML
    private void clearBT_handler(ActionEvent e) {
        albumNameTF.clear();
    }

    /**
     * @param e
     */
    @FXML
    private void createBT_handler(ActionEvent e) {
        String getAlbum = albumNameTF.getText();
        if (getAlbum.trim().isEmpty()) {
            Alert error = new Alert(AlertType.ERROR, "Please enter album.", ButtonType.OK);
            error.showAndWait();
            return;
        }

        if (currentUser.albums.isEmpty()) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to add new album?", ButtonType.YES,
                    ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.NO) {
                return;
            }

            addToArrayList(getAlbum);
            albumNameTF.clear();
        } else if ((duplicationCheck(getAlbum)) == true) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to add new album?", ButtonType.YES,
                    ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.NO) {
                return;
            }

            addToArrayList(getAlbum);
            albumNameTF.clear();
        } else if ((duplicationCheck(getAlbum)) == false) {
            Alert error = new Alert(AlertType.ERROR, "Input album is already in the list", ButtonType.OK);
            error.showAndWait();

            albumNameTF.clear();
            return;
        }
    }

    /**
     * @param e
     */
    @FXML
    private void deleteBT_handler(ActionEvent e) {
        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Album",
                    ButtonType.OK);
            error.showAndWait();
        } else {
            Alert warning = new Alert(AlertType.WARNING, "Delete this Album from list?", ButtonType.YES, ButtonType.NO);
            warning.showAndWait();
            if (warning.getResult() == ButtonType.NO) {
                return;
            }

            int idx = list.getSelectionModel().getSelectedIndex();
            obs.remove(idx);
            currentUser.albums.remove(idx);
            mgUsr.conductSerializing();
        }
    }

    /**
     * @param e
     * @throws Exception
     */
    @FXML
    private void openBT_handler(ActionEvent e) throws Exception {
        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Album",
                    ButtonType.OK);
            error.showAndWait();
        } else {
            mgUsr.conductSerializing();

            Album selectedAlbum = list.getSelectionModel().getSelectedItem();
            String albumName = selectedAlbum.getAlbumName();
            FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
            Parent parent = (Parent) photoAlbumScene.load();
            PhotoAlbumController photoAlbum = photoAlbumScene.getController();
            Scene photoAlbumControllerScene = new Scene(parent);
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            photoAlbum.start(window, selectedAlbum, userName, false);
            window.setScene(photoAlbumControllerScene);
            window.show();
        }

    }

    /**
     * @param e
     */
    @FXML
    private void renameBT_handler(ActionEvent e) {
        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Album",
                    ButtonType.OK);
            error.showAndWait();
        } else {
            Album item = list.getSelectionModel().getSelectedItem();
            int index = list.getSelectionModel().getSelectedIndex();
            TextInputDialog dialog = new TextInputDialog(item.getAlbumName());
            dialog.initOwner(tmpStage);
            dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
            dialog.setTitle("List Item");
            dialog.setContentText("Enter name: ");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent())
                item.setAlbumName(result.get());
            else
                return;
            if (result.isPresent())
                obs.set(index, item);
        }
    }

    /**
     * @param e
     * @throws Exception
     */
    public void logoutBT_handler(ActionEvent e) throws Exception {
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
     * @param getName
     */
    private void addToArrayList(String getName) {
        Album newAlbum = new Album(getName);
        currentUser.albums.add(newAlbum);
        obs = FXCollections.observableArrayList(currentUser.albums);

        setOnListView();
    }

    /**
     * 
     */
    private void setOnListView() {
        list.setItems(obs);
        list.setCellFactory(new Callback<ListView<Album>, ListCell<Album>>() {
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
    }

    /**
     * @param albumName
     * @return
     */
    private boolean duplicationCheck(String albumName) {
        boolean ret = true;
        if (obs.isEmpty()) {
            ret = true;
        } else {
            for (int i = 0; i < obs.size(); i++) {
                if (obs.get(i).getAlbumName().compareTo(albumName) == 0) {
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
     * @param user
     */
    public void start(Stage mainStage, String user) {
        tmpStage = mainStage;
        userName = user;
        mgUsr = mgUsr.getInstance();
        mgUsr.conductDeserializing();

        currentUser = mgUsr.getUser(user);
        obs = FXCollections.observableArrayList(currentUser.albums);
        setOnListView();

        ID.setText(user);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                mgUsr.conductSerializing();
            }
        });
    }
}
