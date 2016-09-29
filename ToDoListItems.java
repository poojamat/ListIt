package com.mathur.android.listit;

import java.util.Date;

/**
 * Created by us48114 on 11/13/2015.
 */
public class ToDoListItems {
    int id;
    String description;
    String status;
    int listId;
    String mOperationType;
    Integer mServerKey;
    Integer mSyncStatus;
    Date mLastUpdated;
    ToDoListItems() {
    }

    ToDoListItems(int id, String description, String status,int listId) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.listId = listId;
    }


    public Date getLastUpdated() {
        return mLastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        mLastUpdated = lastUpdated;
    }


    public Integer GetSyncStatus() {
        return mSyncStatus;
    }

    public void SetSyncStatus(Integer mSyncStatus) {
        this.mSyncStatus = mSyncStatus;
    }
    public Integer GetServerKey() {
        return mServerKey;
    }

    public void SetServerKey(Integer mServerKey) {
        this.mServerKey = mServerKey;
    }


    public String GetOperationType() {
        return mOperationType;
    }

    public void SetOperationType(String mOperationType) {
        this.mOperationType = mOperationType;
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


