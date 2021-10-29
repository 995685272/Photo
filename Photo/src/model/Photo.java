package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Photo implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    private String url;
    private String caption;
    Date lastModifiedDate;
    public ArrayList<Tag> tags = new ArrayList<Tag>();

    public void addTags(Tag tag) {
        tags.add(tag);
    }

    public Photo(String url) {
        this.url = url;
    }

    public Photo(String url, String caption) {
        this.url = url;
        this.caption = caption;
    }

    public Photo(String url, String caption, Date lastModifiedDate) {
        this.url = url;
        this.caption = caption;
        this.lastModifiedDate = lastModifiedDate;
    }

    public Photo(Photo that) {
        this(that.getUrl(), that.getCaption(), that.getDate());
        for (Tag copyTag : that.tags) {
            tags.add(new Tag(copyTag.getkey(), copyTag.getValue()));
        }
    }

    public Tag getSpecificTag(Tag t) {
        Tag ret = null;
        for (Tag a : tags) {

            if (a.getkey().equals(t.getkey()) && a.getValue().equals(t.getValue())) {
                ret = a;
            }
        }

        return ret;
    }

    public String toString() {
        return this.caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Date getDate() {
        return lastModifiedDate;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}