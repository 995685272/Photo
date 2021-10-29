package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class ManageUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String USERINFO_SER = "src/data/database.bin";
    private static ManageUser instance;
    private User user;
    public ArrayList<User> arrList = new ArrayList<>();

    private ManageUser() {
    }

    public static ManageUser getInstance() {
        if (instance == null) {
            instance = new ManageUser();
        }
        return instance;
    }

    public ArrayList<User> getUserList() {
        return arrList;
    }

    public void setUserList(ArrayList<User> user) {
        arrList = user;
    }

    public void addUserList(User user) {
        arrList.add(user);
    }

    public boolean isListEmpty() {
        if (arrList.isEmpty())
            return true;
        else
            return false;
    }

    public User getUser(String getUser) {
        for (User u : arrList) {
            if (u.getUserName().equals(getUser)) {
                user = u;
            }
        }
        return user;
    }

    public boolean checkUserToLogin(String getUserName) {
        for (User u : arrList) {
            if (u.getUserName().equals(getUserName)) {
                user = u;
                return true;
            }
        }
        return false;
    }

    public void conductSerializing() {

        try {
            FileOutputStream fos = new FileOutputStream(USERINFO_SER);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(arrList);

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({ "unused", "unchecked", "resource" })
    public void conductDeserializing() {

        try {
            FileInputStream fis = new FileInputStream(USERINFO_SER);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(bis);
            arrList = (ArrayList<User>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
