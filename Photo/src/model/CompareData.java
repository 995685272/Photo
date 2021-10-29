package model;

import java.util.Comparator;

public class CompareData implements Comparator<Tag> {
    @Override
    public int compare(Tag o1, Tag o2) {
        int ret = String.CASE_INSENSITIVE_ORDER.compare(o1.getkey(), o2.getkey());
        if (ret == 0) {
            ret = 0;
        }
        return ret;
    }

}
