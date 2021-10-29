package controller;

import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.ManageUser;
import model.User;

/**
 * @author xudong jiang
 * @author yuting chen
 *
 */
public class LoginController implements Initializable {

    ManageUser mgUsr = null;
    @FXML
    TextField gettingUserName;
    @FXML
    Button loginButton;

    @FXML
    public void login_button_handler(ActionEvent e) throws Exception {
        Button b = (Button) e.getSource();
        if (b == loginButton) {
            String getUser = gettingUserName.getText();

            if (getUser.trim().isEmpty()) {
                Alert error = new Alert(AlertType.ERROR, "Please enter userName.", ButtonType.OK);
                error.showAndWait();
            } else if (getUser.toLowerCase().equals("admin")) {
                FXMLLoader adminScene = new FXMLLoader(getClass().getResource("/view/admin.fxml"));
                Parent parent = (Parent) adminScene.load();
                AdminController admin = adminScene.getController();
                Scene adminControllerScene = new Scene(parent);
                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                admin.start(window);
                window.setScene(adminControllerScene);
                window.show();
            } else {
                if (mgUsr.checkUserToLogin(getUser)) {
                    FXMLLoader albumScene = new FXMLLoader(getClass().getResource("/view/album.fxml"));
                    Parent parent = (Parent) albumScene.load();
                    AlbumController album = albumScene.getController();
                    Scene albumControllerScene = new Scene(parent);
                    Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    album.start(window, getUser);
                    window.setScene(albumControllerScene);
                    window.show();
                } else {
                    Alert error = new Alert(AlertType.ERROR, "No Matching ID", ButtonType.OK);
                    error.showAndWait();
                }
            }

        }
    }

    /**
     * @param window
     */
    public void start(Stage window) {

    }

    /**
     *
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        mgUsr = ManageUser.getInstance();
        mgUsr.conductDeserializing();
        if (mgUsr.isListEmpty() || mgUsr.checkUserToLogin("stock") == false) {
            User stock = new User("stock");
            mgUsr.arrList.add(stock);
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                mgUsr.conductSerializing();
            }
        });
    }
}
