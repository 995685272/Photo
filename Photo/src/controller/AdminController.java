package controller;

import java.net.URL;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import model.ManageUser;
import model.User;

/**
 * @author xudong jiang
 * @author yuting chen
 *
 */
public class AdminController implements Initializable {
    private ManageUser mgUsr;
    @FXML
    private Button createBT;
    @FXML
    private Button deleteBT;
    @FXML
    private Button logoutBT;
    @FXML
    private Button clearBT;
    @FXML
    private TextField userNameTF;
    @FXML
    private ListView<User> list;

    private ObservableList<User> obs;

    @FXML
    void clearBT_handler(ActionEvent e) {
        userNameTF.clear();
    }

    /**
     * @param e
     */
    @FXML
    public void createBT_handler(ActionEvent e) {
        String getUser = userNameTF.getText();

        if (getUser.trim().isEmpty()) {
            Alert error = new Alert(AlertType.ERROR, "Please enter userName.", ButtonType.OK);
            error.showAndWait();
            return;
        }

        if (mgUsr.arrList.isEmpty()) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to add new user?", ButtonType.YES,
                    ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.NO) {
                return;
            }

            addToArrayList(getUser);
            userNameTF.clear();
        } else if ((duplicationCheck(getUser)) == true) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to add new user?", ButtonType.YES,
                    ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.NO) {
                return;
            }

            addToArrayList(getUser);
            userNameTF.clear();
        } else if ((duplicationCheck(getUser)) == false) {
            Alert error = new Alert(AlertType.ERROR, "Input userName is already in the list", ButtonType.OK);
            error.showAndWait();

            userNameTF.clear();
            return;
        }
    }

    /**
     * @param e
     */
    @FXML
    public void deleteBT_handler(ActionEvent e) {
        if (list.getSelectionModel().getSelectedIndex() == -1) {
            Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new User",
                    ButtonType.OK);
            error.showAndWait();
        } else {
            Alert warning = new Alert(AlertType.WARNING, "Delete this User from list?", ButtonType.YES, ButtonType.NO);
            warning.showAndWait();
            if (warning.getResult() == ButtonType.NO) {
                return;
            }

            int idx = list.getSelectionModel().getSelectedIndex();
            if (list.getSelectionModel().getSelectedItem().getUserName().equals("stock")) {
                Alert error = new Alert(AlertType.ERROR, "You can't delete stock username", ButtonType.OK);
                error.showAndWait();
                return;
            }
            obs.remove(idx);
            mgUsr.arrList.remove(idx);
        }
    }

    /**
     * @param e
     * @throws Exception
     */
    @FXML
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
        User newUser = new User(getName);
        mgUsr.arrList.add(newUser);
        obs = FXCollections.observableArrayList(mgUsr.arrList);

        setOnListView();
    }

    /**
     * 
     */
    private void setOnListView() {
        list.setItems(obs);
        list.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> p) {
                ListCell<User> cell = new ListCell<User>() {
                    @Override
                    protected void updateItem(User s, boolean bln) {
                        super.updateItem(s, bln);
                        if (s != null) {
                            setText(s.getUserName());
                        } else
                            setText("");
                    }
                };
                return cell;
            }
        });
    }

    /**
     * @param userName
     * @return
     */
    private boolean duplicationCheck(String userName) {
        boolean ret = true;
        if (obs.isEmpty()) {
            ret = true;
        } else {
            for (int i = 0; i < obs.size(); i++) {
                if (obs.get(i).getUserName().compareTo(userName) == 0) {
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
        mgUsr = ManageUser.getInstance();
        mgUsr.conductDeserializing();
        obs = FXCollections.observableArrayList(mgUsr.arrList);
        setOnListView();

        if (mgUsr.isListEmpty() || mgUsr.checkUserToLogin("stock") == false) {
            addToArrayList("stock");
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                mgUsr.conductSerializing();
            }
        });
    }

    /**
     * @param mainStage
     */
    public void start(Stage mainStage) {

    }
}
