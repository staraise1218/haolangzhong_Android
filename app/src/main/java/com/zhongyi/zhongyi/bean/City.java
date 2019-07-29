package com.zhongyi.zhongyi.bean;

import java.util.List;

public class City {
     private boolean isNewRecord;
     private String id;
     private String name;
     private String arealevel;
     private int parentId;
     private List<City> child;

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArealevel() {
        return arealevel;
    }

    public void setArealevel(String arealevel) {
        this.arealevel = arealevel;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<City> getChild() {
        return child;
    }

    public void setChild(List<City> child) {
        this.child = child;
    }
}
