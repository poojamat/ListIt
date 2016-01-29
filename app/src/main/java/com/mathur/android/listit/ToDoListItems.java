package com.mathur.android.listit;

/**
 * Created by us48114 on 11/13/2015.
 */
public class ToDoListItems {
    int id;
    String description;
    String status;
    int listId;

    ToDoListItems() {
    }

    ToDoListItems(int id, String description, String status,int listId) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.listId = listId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String stat) {
        this.status = stat;
    }

    public int getListId() {
        return this.listId;
    }

    public void setListId(int list) {
        this.listId = list;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean IsStatusChecked() {
        if (new String("true").equals(status)){
            return true;
        }
        return false;

    }

    @Override
    public String toString() {
        return "ToDoListItems [id=" + id + ", description=" + description + ", status=" + status + ", listId =" + listId
                + "]";
    }
}


