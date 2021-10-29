package model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    public ArrayList<String> tagType = new ArrayList<String>();
    private String userName;
    public ArrayList<Album> albums = new ArrayList<Album>();

    public User(String userName) {
        this.userName = userName;
        this.tagType.add("Person");
        this.tagType.add("Location");
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String toString() {
        return userName;
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public ArrayList<Album> getAlbum() {
        return this.albums;
    }

    public Album getSpecificAlbum(String albumName) {
        Album ret = null;
        for (Album a : albums) {
            if (a.getAlbumName().equals(albumName))
                ret = a;
        }

        return ret;
    }
}
