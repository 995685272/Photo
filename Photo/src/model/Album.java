package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    private String albumName;
    public ArrayList<Photo> photos = new ArrayList<>();

    public Album(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public ArrayList<Photo> getPhotos() {
        return this.photos;
    }

    public void setPhotos(Photo photo) {
        photos.add(photo);
    }

    public int getIdxPhotos() {
        return photos.size();
    }

    public Photo getPhotoWithIndex(int idx) {
        return photos.get(idx);
    }

    public int getIndex(Photo photo) {
        return photos.indexOf(photo);
    }

    public Photo getSpecificPhoto(Photo p) {
        Photo ret = null;
        for (Photo a : photos) {
            String[] splitUrl = p.getUrl().split("/");
            String[] splitUrlPhoto = a.getUrl().split("/");
            if (splitUrlPhoto[splitUrlPhoto.length - 1].equals(splitUrl[splitUrl.length - 1])
                    && a.getCaption().equals(p.getCaption())) {
                ret = a;
            }
        }
        return ret;
    }
}
