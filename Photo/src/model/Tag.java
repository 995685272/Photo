package model;

import java.io.Serializable;

public class Tag implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    private String key;
    private String value;

    public Tag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Tag(Tag that) {
        this(that.key, that.value);
    }

    public String getkey() {
        return key;
    }

    public void setkey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) o;

        if (this.key.compareTo(other.getkey()) == 0 && this.value.compareTo(other.getValue()) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
