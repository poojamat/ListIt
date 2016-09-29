package com.mathur.android.listit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by neerajpooja on 6/26/2016.
 */
public final class Common {
    public String GetUTCDate(Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateUpdated = dateFormatter.format(date);
        return dateUpdated;
    }

    public String ParseJsonFromHttpResponse(String resultString, String keyToBeParsed, String jsonObject) throws JSONException {
        JSONObject resultJson = new JSONObject(resultString);
        JSONObject list = resultJson.getJSONObject(jsonObject);
        return list.getString(keyToBeParsed);
    }

    public ToDoLists ParseJsonTodoListFromHttpResponse(String resultString) throws JSONException {
        JSONObject resultJson = new JSONObject(resultString);
        ToDoLists toDoLists = new ToDoLists();
        ;
        toDoLists.ListToDoList = new ArrayList<>();
        JSONArray lists = resultJson.getJSONArray("lists");
        for (int i = 0; i < lists.length(); i++) {

            ToDoList toDoList = new ToDoList();
            JSONObject list = lists.getJSONObject(i);
            toDoList.SetServerKey(list.getInt("id"));
            toDoList.SetSyncStatus(list.getInt("syncStatus"));
            toDoList.setName(list.getString("name"));
            toDoList.setStatus(list.getString("status"));
            toDoList.SetOperationType(list.getString("operationType"));
            //toDoList.setLastUpdated(list.getString("operationType"));
            toDoLists.ListToDoList.add(toDoList);
        }
        return toDoLists;
    }

    public ToDoItemsLists ParseJsonTodoListItemsFromHttpResponse(String resultString) throws JSONException {
        JSONObject resultJson = new JSONObject(resultString);
        ToDoItemsLists toDoListItems = new ToDoItemsLists();
        toDoListItems.ListToDoList = new ArrayList<>();
        JSONArray lists = resultJson.getJSONArray("listItems");
        for (int i = 0; i < lists.length(); i++) {

            ToDoListItems toDoList = new ToDoListItems();
            JSONObject list = lists.getJSONObject(i);
            toDoList.SetServerKey(list.getInt("id"));
            toDoList.SetSyncStatus(list.getInt("syncStatus"));
            toDoList.setDescription(list.getString("description"));
            toDoList.setStatus(list.getString("status"));
            toDoList.setListId(list.getInt("listId"));
            toDoList.SetOperationType(list.getString("operationType"));
            toDoListItems.ListToDoList.add(toDoList);
        }
        return toDoListItems;
    }
}
