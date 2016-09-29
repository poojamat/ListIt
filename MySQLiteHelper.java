package com.mathur.android.listit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "ToDoListDB";

    private static final String TABLE_TODOLISTITEMS = "ToDoListItems";
    private static final String TABLE_TODOLISTS = "ToDoLists";
    private static final String TABLE_USER = "User";

    private static final String KEY_ID = "id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LISTID = "listId";
    private static final String KEY_OPERATION = "operationType";
    private static final String KEY_SERVERKEY = "serverKey";
    private static final String KEY_TIMESTAMP = "timeStamp";
    private static final String KEY_LASTUPDATED = "lastUpdated";
    private static final String KEY_SYNCSTATUS = "syncStatus";
    private static final String[] COLUMNS = {KEY_ID, KEY_DESCRIPTION, KEY_STATUS, KEY_LISTID, KEY_OPERATION, KEY_SERVERKEY, KEY_TIMESTAMP, KEY_SYNCSTATUS};
    private static final String[] TODOCOLUMNS = {KEY_ID, KEY_NAME, KEY_STATUS, KEY_OPERATION, KEY_SERVERKEY, KEY_TIMESTAMP, KEY_SYNCSTATUS};


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create todolistItems table
        String CREATE_TODOLISTITEMS_TABLE = "CREATE TABLE ToDoListItems ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "description TEXT, " +
                "status TEXT, " +
                KEY_LISTID + " INTEGER," +
                KEY_OPERATION + " TEXT," +
                KEY_SERVERKEY + " INTEGER," +
                KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                KEY_SYNCSTATUS + " INTEGER)";

        // create todolistItems table
        db.execSQL(CREATE_TODOLISTITEMS_TABLE);

        String CREATE_INDEX_TODOLOSTITEMS = "CREATE UNIQUE INDEX UNIQUE_DESCRIPTION_ToDOLISTITEMS\n" +
                "ON ToDoListItems (description)";
        db.execSQL(CREATE_INDEX_TODOLOSTITEMS);

        String CREATE_TODOLISTS_TABLE = "CREATE TABLE ToDoLists ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "status TEXT, " +
                KEY_OPERATION + " TEXT," +
                //"operation TEXT, " +
                KEY_SERVERKEY + " INTEGER, " +
                KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                KEY_SYNCSTATUS + " INTEGER)";

        // create TodoLists table
        db.execSQL(CREATE_TODOLISTS_TABLE);

        String CREATE_INDEX_TODOLIST = "CREATE UNIQUE INDEX UNIQUE_name_ToDOLIST\n" +
                "ON ToDoLists (name)";
        db.execSQL(CREATE_INDEX_TODOLIST);

        String CREATE_USER_TABLE = "CREATE TABLE User ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "password TEXT, " +
                "token TEXT," +
                KEY_LASTUPDATED + " DATETIME)";

        // create user table
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ToDoListItems");
        db.execSQL("DROP TABLE IF EXISTS ToDoLists");
        db.execSQL("DROP TABLE IF EXISTS User");
        this.onCreate(db);
    }

    public void addToDoListItems(ToDoListItems toDoListItems) {
        //for logging
        Log.d("addToDoListItems", toDoListItems.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, toDoListItems.getDescription());
        values.put(KEY_STATUS, toDoListItems.getStatus());
        values.put(KEY_LISTID, toDoListItems.getListId());
        values.put(KEY_OPERATION, Constants.KEY_Insert);
        Date lastUpdated = new Date(0);
        values.put(KEY_TIMESTAMP, String.valueOf(lastUpdated));
        values.put(KEY_SYNCSTATUS, toDoListItems.GetSyncStatus());
        values.put(KEY_SERVERKEY, toDoListItems.GetServerKey());
        // 3. insert
        db.insert(TABLE_TODOLISTITEMS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public long addToDoLists(ToDoList toDoLists) {
        //for logging
        Log.d("addToDoLists", toDoLists.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, toDoLists.getName());
        values.put(KEY_STATUS, toDoLists.getStatus());
        values.put(KEY_OPERATION, toDoLists.GetOperationType());
        Date lastUpdated = new Date(0);
        values.put(KEY_TIMESTAMP, String.valueOf(lastUpdated));
        values.put(KEY_SYNCSTATUS, toDoLists.GetSyncStatus());
        values.put(KEY_SERVERKEY, toDoLists.GetServerKey());
        // 3. insert
        long id = db.insert(TABLE_TODOLISTS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
        return id;
    }

    public ToDoList getToDoList(int id) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_TODOLISTS, // a. table
                        TODOCOLUMNS, // b. column names
                        " id = ? ", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        ToDoList toDoList = new ToDoList();
        toDoList.setId(Integer.parseInt(cursor.getString(0)));
        toDoList.setName(cursor.getString(1));
        toDoList.setStatus(cursor.getString(2));
        toDoList.SetServerKey(cursor.getInt(4));

        //log
        Log.d("getToDoListItem(" + id + ")", toDoList.toString());

        // 5. return book
        return toDoList;
    }


    public ToDoList getToDoListByServerKey(int serverKey) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_TODOLISTS, // a. table
                        TODOCOLUMNS, // b. column names
                        "serverKey = ? ", // c. selections
                        new String[]{String.valueOf(serverKey)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        ToDoList toDoList = new ToDoList();
        // 3. if we got results get the first one
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    // 4. build book object

                    toDoList.setId(Integer.parseInt(cursor.getString(0)));
                    toDoList.setName(cursor.getString(1));
                    toDoList.setStatus(cursor.getString(2));
                    toDoList.SetServerKey(cursor.getInt(4));
                } while (cursor.moveToNext());
            }
        //log
        Log.d("getToDoList(" + serverKey + ")", toDoList.toString());

        // 5. return book
        return toDoList;
    }


    public ToDoListItems getToDoListItem(int id) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_TODOLISTITEMS, // a. table
                        COLUMNS, // b. column names
                        " id = ? AND "+KEY_OPERATION +"!=?", // c. selections
                        new String[]{String.valueOf(id),Constants.KEY_Update}, // d. selections args
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

    public List<ToDoListItems> getToDoListItemByListId(String listId) {
        List<ToDoListItems> toDoListItems = new LinkedList<ToDoListItems>();
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_TODOLISTITEMS, // a. table
                        COLUMNS, // b. column names
                        " listId = ? AND "+KEY_OPERATION +"!=?", // c. selections
                        new String[]{String.valueOf(listId),Constants.KEY_Delete}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one

        ToDoListItems toDoListItem = null;
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
        db.close();
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
        ToDoListItems toDoListItem = null;
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

    public List<ToDoList> getAllToDoListsByOperation() {
        List<ToDoList> toDoLists = new LinkedList<ToDoList>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor =
                db.query(TABLE_TODOLISTS, // a. table
                        TODOCOLUMNS, // b. column names
                        KEY_OPERATION + " != ?", // c. selections
                        new String[]{Constants.KEY_Delete}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. go over each row, build book and add it to list
        ToDoList toDoList = null;
        if (cursor.moveToFirst()) {
            do {
                toDoList = new ToDoList();

                toDoList.setId(Integer.parseInt(cursor.getString(0)));
                toDoList.setName(cursor.getString(1));
                toDoList.setStatus(cursor.getString(2));

                toDoLists.add(toDoList);
            } while (cursor.moveToNext());
        }

        Log.d("getAllToDoListItems()", toDoLists.toString());
        db.close();
        return toDoLists;
    }

    public List<ToDoList> getAllToDoLists() {
        List<ToDoList> toDoLists = new LinkedList<ToDoList>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_TODOLISTS;//+ " WHERE "+KEY_OPERATION+"!=?";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // Cursor cursor = db.rawQuery(query,new String[]{Constants.KEY_Delete});
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        ToDoList toDoList = null;
        if (cursor.moveToFirst()) {
            do {
                toDoList = new ToDoList();

                toDoList.setId(Integer.parseInt(cursor.getString(0)));
                toDoList.setName(cursor.getString(1));
                toDoList.setStatus(cursor.getString(2));

                toDoLists.add(toDoList);
            } while (cursor.moveToNext());
        }

        Log.d("getAllToDoListItems()", toDoLists.toString());
        db.close();
        return toDoLists;
    }

    public List<ToDoList> getAllUnSyncedToDoLists() throws ParseException {
        List<ToDoList> toDoLists = new LinkedList<ToDoList>();


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =
                db.query(TABLE_TODOLISTS, // a. table
                        TODOCOLUMNS, // b. column names
                        KEY_SYNCSTATUS + "=0", // c. selections
                        // new String[]{String.valueOf(0)}, // d. selections args
                        null,
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. go over each row, build book and add it to list
        ToDoList toDoList = null;
        if (cursor.moveToFirst()) {
            do {
                toDoList = new ToDoList();

                toDoList.setId(Integer.parseInt(cursor.getString(0)));
                toDoList.setName(cursor.getString(1));
                toDoList.setStatus(cursor.getString(2));

                toDoList.SetOperationType(cursor.getString(3));
                if (cursor.getString(4) != null) {
                    toDoList.SetServerKey(Integer.parseInt(cursor.getString(4)));
                }
                Log.d("getAllToDoListToSync()", "Server Key:" + cursor.getString(4));
                Log.d("getAllToDoListToSync()", "Date:" + cursor.getString(5));
                DateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
                String date = cursor.getString(5);
                toDoList.setLastUpdated(df.parse(cursor.getString(5)));
                toDoList.SetSyncStatus(Integer.valueOf(cursor.getString(6)));
                toDoLists.add(toDoList);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("getAllUnSyncedToDoLists", toDoLists.toString());
        db.close();
        return toDoLists;
    }

    public List<ToDoListItems>
    getAllUnSyncedToDoListItems() throws ParseException {
        List<ToDoListItems> toDoLists = new LinkedList<ToDoListItems>();


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =
                db.query(TABLE_TODOLISTITEMS, // a. table
                        COLUMNS, // b. column names
                        KEY_SYNCSTATUS + " =0", // c. selections
                        null, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. go over each row, build book and add it to list
        ToDoListItems toDoListItems = null;
        if (cursor.moveToFirst()) {
            do {
                toDoListItems = new ToDoListItems();

                toDoListItems.setId(Integer.parseInt(cursor.getString(0)));
                toDoListItems.setDescription(cursor.getString(1));
                toDoListItems.setStatus(cursor.getString(2));
                if (cursor.getString(3) != null) {
                    toDoListItems.setListId(Integer.parseInt(cursor.getString(3)));
                }

                toDoListItems.SetOperationType(cursor.getString(4));
                if (cursor.getString(5) != null) {
                    toDoListItems.SetServerKey(Integer.parseInt(cursor.getString(5)));
                }
                Log.d("getAllToDoListToSync()", "Date:" + cursor.getString(5));
                DateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
                String date = cursor.getString(6);
                Log.d("date is:", date);
                toDoListItems.setLastUpdated(df.parse(cursor.getString(6)));
                toDoListItems.SetSyncStatus(cursor.getInt(7));
                toDoLists.add(toDoListItems);
            } while (cursor.moveToNext());
        }

        Log.d("getAllToDoListItems()", toDoLists.toString());
        db.close();
        return toDoLists;
    }

    public int updateToDoListItem(ToDoListItems toDoListItems, String operation) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, toDoListItems.getId());
        values.put(KEY_DESCRIPTION, toDoListItems.getDescription());
        values.put(KEY_STATUS, toDoListItems.getStatus());
        values.put(KEY_LISTID, toDoListItems.getListId());
        values.put(KEY_OPERATION, operation);
        values.put(KEY_TIMESTAMP, String.valueOf(new Date()));
        values.put(KEY_SYNCSTATUS, toDoListItems.GetSyncStatus());

        // 3. updating row
        int i = db.update(TABLE_TODOLISTITEMS, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(toDoListItems.getId())}); //selection args

        // 4. close
        db.close();

        return i;

    }

    public int updateToDoListItemByListId(String listId, String operation) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_OPERATION, operation);
        values.put(KEY_TIMESTAMP, "datetime()");
        values.put(KEY_SYNCSTATUS, 0);

        // 3. updating row
        int i = db.update(TABLE_TODOLISTITEMS, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(listId)});

        // 4. close
        db.close();

        return i;

    }

    public int updateToDoList(ToDoList toDoLists, String operation) {

        SQLiteDatabase db = this.getWritableDatabase();
        Date date = new Date();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, toDoLists.getId());
        values.put(KEY_NAME, toDoLists.getName());
        values.put(KEY_STATUS, toDoLists.getStatus());
        values.put(KEY_OPERATION, operation);
        values.put(KEY_TIMESTAMP, String.valueOf(date));
        values.put(KEY_SYNCSTATUS, toDoLists.GetSyncStatus());
        values.put(KEY_SERVERKEY, toDoLists.GetServerKey());

        // 3. updating row
        int i = db.update(TABLE_TODOLISTS, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(toDoLists.getId())}); //selection args

        // 4. close
        db.close();
        return i;
    }

    public void deleteToDoListItems(ToDoListItems toDoListItems) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_TODOLISTITEMS,
                KEY_ID + " = ?",
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

    public void deleteToDoLists(ToDoList toDoLists) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_TODOLISTS, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(toDoLists.getId())}
        );
        db.close();

        //log
        Log.d("deleteToDoLists", toDoLists.toString());

    }

    public User getUserById(int id) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_USER, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        User user = new User();
        user.setId(Integer.parseInt(cursor.getString(0)));
        user.setName(cursor.getString(1));
        user.setPassword(cursor.getString(2));

        //log
        Log.d("getUser(" + id + ")", user.toString());
        db.close();
        // 5. return book
        return user;
    }

    public User getUser() throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(query, null);
        User user = new User();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    user.setId(Integer.parseInt(cursor.getString(0)));
                    user.setName(cursor.getString(1));
                    user.setPassword(cursor.getString(2));
                    user.setToken(cursor.getString(3));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
                    if (cursor.getString(4) != null) {
                        user.setLastUpdated(simpleDateFormat.parse(cursor.getString(4)));
                    } else {
                        user.setLastUpdated(new Date(Long.MIN_VALUE));
                    }


                } while (cursor.moveToNext());
            }
        }
        Log.d("getUser", user.toString());
        db.close();
        return user;
    }


    public void addUser(User user) {
        //for logging
        Log.d("addUser", user.toString());

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        Date date = new Date(0);
        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.getId());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_LASTUPDATED, String.valueOf(date));

        // 3. insert
        db.insert(TABLE_USER, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_USER, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(user.getId())}
        );
        db.close();

        //log
        Log.d("deleteToDoLists", user.toString());

    }

    public int updateUser(User user) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.getId());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_LASTUPDATED, String.valueOf(user.getLastUpdated()));
        // 3. updating row
        int i = db.update(TABLE_USER, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(user.getId())}); //selection args

        // 4. close
        db.close();

        return i;

    }


}