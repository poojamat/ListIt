package com.mathur.android.listit;

import java.util.Date;
/**
 * Created by us48114 on 11/13/2015.
 */
public class ToDoList {
    int id;
    String mName;
    String mStatus;
    String mOperationType;
    Integer mServerKey;
    Integer mSyncStatus;
    Date LastUpdated;

    ToDoList() {
    }

    ToDoList(int listId, String name, String status) {
        this.id = listId;
        this.mName = name;
        this.mStatus = status;
    }

    public Date getLastUpdated() {
        return LastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        LastUpdated = lastUpdated;
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

    public String getName() {
        return this.mName;
    }

    public void setName(String desc) {
        this.mName = desc;
    }

    public String getStatus() {
        return this.mStatus;
    }

    public void setStatus(String stat) {
        this.mStatus = stat;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean IsStatusChecked() {
        if (new String("true").equals(mStatus)) {
            return true;
        }
        return false;

    }


    @Override
    public String toString() {
        return "ToDoListItems [id=" + id + ", name=" + mName + ", status=" + mStatus
                + ", SyncStatus= " + mSyncStatus + "LastUpdated= " + LastUpdated + "]";
    }
}


