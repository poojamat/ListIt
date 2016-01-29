package com.mathur.android.listit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ToDoListDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_TODOLISTITEMS_TABLE = "CREATE TABLE ToDoListItems ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "description TEXT, "+
                "status TEXT, " +
                "listId INTEGER)";

        // create books table
        db.execSQL(CREATE_TODOLISTITEMS_TABLE);

        String CREATE_TODOLISTS_TABLE = "CREATE TABLE ToDoLists ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, "+
                "status TEXT)";

        // create books table
        db.execSQL(CREATE_TODOLISTS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ToDoListItems");
        db.execSQL("DROP TABLE IF EXISTS ToDoLists");

        this.onCreate(db);
    }
    // Books table name
    private static final String TABLE_TODOLISTITEMS = "ToDoListItems";
    private static final String TABLE_TODOLISTS = "ToDoLists";
    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATUS = "status";
    private static final String KEY_LISTID = "listId";

    private static final String[] COLUMNS = {KEY_ID,KEY_DESCRIPTION,KEY_STATUS, KEY_LISTID};
    public void addToDoListItems(ToDoListItems toDoListItems){
        //for logging
        Log.d("addToDoListItems", toDoListItems.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, toDoListItems.getDescription()); // get Description
        values.put(KEY_STATUS, toDoListItems.getStatus()); // get Status
        values.put(KEY_LISTID, toDoListItems.getListId()); // get Status

        // 3. insert
        db.insert(TABLE_TODOLISTITEMS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public long addToDoLists(ToDoLists toDoLists){
        //for logging
        Log.d("addToDoLists", toDoLists.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, toDoLists.getName()); // get Description
        values.put(KEY_STATUS, toDoLists.getStatus()); // get Status

        // 3. insert
       long id= db.insert(TABLE_TODOLISTS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
        return id;
    }

    public ToDoListItems getToDoListIem(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_TODOLISTITEMS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        ToDoListItems toDoListItems = new ToDoListItems();
        toDoListItems.setId(Integer.parseInt(cursor.getString(0)));
        toDoListItems.setDescription(cursor.getString(1));
        toDoListItems.setStatus(cursor.getString(2));
        toDoListItems.setListId(Integer.parseInt(cursor.getString(3)));


        //log
        Log.d("getToDoListItem(" + id + ")", toDoListItems.toString());

        // 5. return book
        return toDoListItems;
    }

    public List<ToDoListItems> getToDoListIemByListId(String listId){
        List<ToDoListItems> toDoListItems = new LinkedList<ToDoListItems>();
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_TODOLISTITEMS, // a. table
                        COLUMNS, // b. column names
                        " listId = ?", // c. selections
                        new String[] { String.valueOf(listId) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one

            ToDoListItems toDoListItem= null;
        if (cursor.moveToFirst()) {
            do {
                toDoListItem = new ToDoListItems();

                toDoListItem.setId(Integer.parseInt(cursor.getString(0)));
                toDoListItem.setDescription(cursor.getString(1));
                toDoListItem.setStatus(cursor.getString(2));
                toDoListItem.setListId(Integer.parseInt(cursor.getString(3)));
                toDoListItems.add(toDoListItem);
            } while (cursor.moveToNext());
        }

        Log.d("getAllToDoListItems()", toDoListItems.toString());

        // return books
        return toDoListItems;
    }

    public List<ToDoListItems> getAllToDoListItems() {
        List<ToDoListItems> toDoListItems = new LinkedList<ToDoListItems>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_TODOLISTITEMS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        ToDoListItems toDoListItem= null;
        if (cursor.moveToFirst()) {
            do {
                toDoListItem = new ToDoListItems();

                toDoListItem.setId(Integer.parseInt(cursor.getString(0)));
                toDoListItem.setDescription(cursor.getString(1));
                toDoListItem.setStatus(cursor.getString(2));
                toDoListItem.setListId(Integer.parseInt(cursor.getString(3)));
                // Add book to books
                toDoListItems.add(toDoListItem);
            } while (cursor.moveToNext());
        }

        Log.d("getAllToDoListItems()", toDoListItems.toString());

        // return books
        return toDoListItems;
    }
    public List<ToDoLists> getAllToDoLists() {
        List<ToDoLists> toDoLists = new LinkedList<ToDoLists>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_TODOLISTS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        ToDoLists toDoList= null;
        if (cursor.moveToFirst()) {
            do {
                toDoList = new ToDoLists();

                toDoList.setId(Integer.parseInt(cursor.getString(0)));
                toDoList.setName(cursor.getString(1));
                toDoList.setStatus(cursor.getString(2));

                toDoLists.add(toDoList);
            } while (cursor.moveToNext());
        }

        Log.d("getAllToDoListItems()", toDoLists.toString());

        return toDoLists;
    }

    public int updateToDoListItem(ToDoListItems toDoListItems) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, toDoListItems.getId());
        values.put(KEY_DESCRIPTION, toDoListItems.getDescription()); // get Description
        values.put(KEY_STATUS, toDoListItems.getStatus()); // get Status
        values.put(KEY_LISTID, toDoListItems.getListId()); // get List Id

        // 3. updating row
        int i = db.update(TABLE_TODOLISTITEMS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(toDoListItems.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public int updateToDoList(ToDoLists toDoLists) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, toDoLists.getId());
        values.put(KEY_NAME, toDoLists.getName()); // get Name
        values.put(KEY_STATUS, toDoLists.getStatus()); // get Status

        // 3. updating row
        int i = db.update(TABLE_TODOLISTS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(toDoLists.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }
    public void deleteToDoListItems(ToDoListItems toDoListItems) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_TODOLISTITEMS, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(toDoListItems.getId())}
        ); //selections args
       // db.execSQL("TRUNCATE table" + TABLE_TODOLISTITEMS);
        // 3. close
        db.close();

        //log
        Log.d("deleteToDoListItems", toDoListItems.toString());

    }

    public void deleteToDoListItemsByListId(String listId) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_TODOLISTITEMS, //table name
                KEY_LISTID + " = ?",  // selections
                new String[]{String.valueOf(listId)}
        ); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteListItemsbyListId", listId);

    }

    public void deleteToDoLists(ToDoLists toDoLists) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_TODOLISTS, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(toDoLists.getId())}
        );
        db.close();

        //log
        Log.d("deleteToDoLists", toDoLists.toString());

    }


}