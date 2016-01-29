package com.mathur.android.listit;

/**
 * Created by us48114 on 11/13/2015.
 */
public class ToDoLists {
    int id;
    String name;
    String status;


    ToDoLists() {
    }

    ToDoLists(int listId, String name, String status) {
        this.id=listId;
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String desc) {
        this.name = desc;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String stat) {
        this.status = stat;
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
        return "ToDoListItems [id=" + id + ", name=" + name + ", status=" + status
                + "]";
    }
}


