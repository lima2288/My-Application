package com.myapplication.postexpandedlist;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private String userid;
    private String title;
    private String id;
    private String body;
    private String postid;
    private String cid;
    private String name;
    private String email;
    private String cemail;
    private List<Item> item = new ArrayList<>();
    private boolean isExpanded = false;             //Is this item expanded?
    private boolean isSelected = false;             //Is selected item?
    private int hierarchy = 0;                      //Used for deciding indent by rank of item

    public String getId() {
        return id;
    }
    public String getUserid() {
        return userid;
    }
    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public List<Item> getItemList() {
        return item;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
