package com.mathur.android.listit;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by neerajpooja on 5/31/2016.
 */
public class SyncServerDataTask extends AsyncTask {

    MySQLiteHelper mDb;
    private Context mContext;

    SyncServerDataTask(Context context) {

        this.mContext = context;
        mDb = new MySQLiteHelper(mContext);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        URL url = null;
        String result;

        try {

            User user = mDb.getUser();
            if (user.getName() != null) {
                ToDoLists toDoLists = GetToDoListsToSyncFromServer(user);
                SyncServerToDosToClient(toDoLists);
                ToDoLists clientToDoListsToSyncOnServer = GetClientToDoListsToSync();
                SyncClientToDoListsToServer(clientToDoListsToSyncOnServer, user);
                ToDoItemsLists toDoItemsLists = GetToDoListItemsToSyncFromServer(user);
                SyncServerToDoItemsToClient(toDoItemsLists);
                ToDoItemsLists ClienttoDoItemsLists = GetClientToDoListItemsToSync();
                SyncClientToDoListItemsToServer(ClienttoDoItemsLists, user);
                user.lastUpdated = new Date();
                mDb.updateUser(user);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ToDoLists GetToDoListsToSyncFromServer(User user) throws MalformedURLException, UnsupportedEncodingException, JSONException {
        URL url;
        String result;
        Common common = new Common();
        if (user.getName() != null) {
            String lastUpdatedDate = common.GetUTCDate(user.lastUpdated);//.replace(' ','%');
            url = new URL("http://52.38.93.60/todolists/sync/" + user.getId() + "?lastSyncDate=" + URLEncoder.encode(lastUpdatedDate, "UTF-8"));
            String token = ((Global) mContext.getApplicationContext()).get_Token();
            RestClient restClient = new RestClient();
            result = restClient.getClient(url, user.getName(), user.getPassword());
            ToDoLists toDoListsToSyncOnClient = common.ParseJsonTodoListFromHttpResponse(result);
            return toDoListsToSyncOnClient;
        }
        return null;
    }

    private ToDoItemsLists GetToDoListItemsToSyncFromServer(User user) throws MalformedURLException, UnsupportedEncodingException, JSONException {
        URL url;
        String result;
        Common common = new Common();
        String lastUpdatedDate = common.GetUTCDate(user.lastUpdated);//.replace(' ','%');
        url = new URL("http://52.38.93.60/todolistItems/sync/" + user.getId() + "?lastSyncDate=" + URLEncoder.encode(lastUpdatedDate, "UTF-8"));
        String token = ((Global) mContext.getApplicationContext()).get_Token();
        RestClient restClient = new RestClient();
        result = restClient.getClient(url, user.getName(), user.getPassword());
        ToDoItemsLists toDoListItemsToSyncOnClient = common.ParseJsonTodoListItemsFromHttpResponse(result);
        return toDoListItemsToSyncOnClient;
    }
    private void SyncServerToDosToClient(ToDoLists toDoLists) {
        if (toDoLists != null && toDoLists.ListToDoList != null && toDoLists.ListToDoList.size() > 0) {
            for (ToDoList toDoList : toDoLists.ListToDoList) {
                toDoList.SetSyncStatus(1);
                if (toDoList.GetOperationType().equals(Constants.KEY_Delete)) {
                    mDb.deleteToDoLists(toDoList);
                }
                if (toDoList.GetOperationType().equals(Constants.KEY_Update)) {
                    mDb.updateToDoList(toDoList, Constants.KEY_Update);
                } else {
                    mDb.addToDoLists(toDoList);
                }
            }
        }
    }

    private void SyncServerToDoItemsToClient(ToDoItemsLists toDoListItems) {
        if (toDoListItems != null && toDoListItems.ListToDoList != null && toDoListItems.ListToDoList.size() > 0) {
            for (ToDoListItems toDoListItem : toDoListItems.ListToDoList) {
                toDoListItem.SetSyncStatus(1);
                ToDoList toDoList = mDb.getToDoListByServerKey(toDoListItem.getListId());
                toDoListItem.setListId(toDoList.getId());
                if (toDoListItem.GetOperationType().equals(Constants.KEY_Delete)) {
                    mDb.deleteToDoListItems(toDoListItem);
                }
                if (toDoListItem.GetOperationType().equals(Constants.KEY_Update)) {

                    mDb.updateToDoListItem(toDoListItem, Constants.KEY_Update);
                }
                mDb.addToDoListItems(toDoListItem);
            }
        }
    }

    private ToDoLists GetClientToDoListsToSync() throws ParseException {
        ToDoLists toDoLists = new ToDoLists();
        toDoLists.ListToDoList = mDb.getAllUnSyncedToDoLists();
        return toDoLists;
    }

    private ToDoItemsLists GetClientToDoListItemsToSync() throws ParseException {
        ToDoItemsLists toDoItemsLists = new ToDoItemsLists();
        toDoItemsLists.ListToDoList = mDb.getAllUnSyncedToDoListItems();
        return toDoItemsLists;
    }


    private void SyncClientToDoListsToServer(ToDoLists todoLists, User user) throws IOException, JSONException, ParseException {
        if (todoLists != null) {
            URL url;
            String result;

            String token = ((Global) mContext.getApplicationContext()).get_Token();
            RestClient restClient = new RestClient();
            for (ToDoList toDoList : todoLists.ListToDoList) {
                toDoList.SetSyncStatus(1);
                if (toDoList.GetOperationType().equals(Constants.KEY_Delete)) {
                    url = new URL("http://52.38.93.60/todolists/delete/" + toDoList.GetServerKey());
                    restClient.deleteClient(url, user.getName(), user.getPassword());
                    mDb.deleteToDoLists(toDoList);
                }
                if (toDoList.GetOperationType().equals(Constants.KEY_Insert)) {

                    Common common = new Common();
                    String lastUpdated = common.GetUTCDate(toDoList.getLastUpdated());
                    url = new URL("http://52.38.93.60/todolists/sync/" + user.getId() + "?lastSyncDate=" + URLEncoder.encode(lastUpdated, "UTF-8")
                            + "&name=" + URLEncoder.encode(toDoList.getName(), "UTF-8") + "&operationType=" + URLEncoder.encode(Constants.KEY_Insert, "UTF-8")
                            + "&status=" + URLEncoder.encode(toDoList.getStatus(), "UTF-8") + "&public=" + URLEncoder.encode("1", "UTF-8"));
                    result = restClient.postRequest(url, user.getName(), user.getPassword(), toDoList.getLastUpdated());

                    String id = common.ParseJsonFromHttpResponse(result, "id", "List");
                    Log.d("server key", String.valueOf(id));
                    toDoList.SetServerKey(Integer.valueOf(id));
                    toDoList.SetSyncStatus(1);
                    mDb.updateToDoList(toDoList, Constants.KEY_Insert);
                }
            }
        }
    }

    private void SyncClientToDoListItemsToServer(ToDoItemsLists todoLists, User user) throws IOException, JSONException {
        if (todoLists != null) {
            URL url;
            String result;
            Common common = new Common();
            String token = ((Global) mContext.getApplicationContext()).get_Token();
            RestClient restClient = new RestClient();
            for (ToDoListItems toDoListItems : todoLists.ListToDoList) {
                toDoListItems.SetSyncStatus(1);

                if (toDoListItems.GetOperationType().equals(Constants.KEY_Delete)) {
                    mDb.deleteToDoListItems(toDoListItems);
                    //TO DO Add server call to delete
                    url = new URL("http://52.38.93.60/todolistItems/delete/" + toDoListItems.GetServerKey());
                    restClient.deleteClient(url, user.getName(), user.getPassword());

                }
                if (toDoListItems.GetOperationType().equals(Constants.KEY_Insert)) {
                    String lastUpdated = common.GetUTCDate(toDoListItems.getLastUpdated());
                    ToDoList toDoList = mDb.getToDoList(toDoListItems.getListId());
                    int serverKey = toDoList.GetServerKey();
                    String serverListKey = String.valueOf(serverKey);
                    Log.d("to do server key", serverListKey);

                    url = new URL("http://52.38.93.60/todolistItems/sync/" + user.getId() + "?lastSyncDate=" + URLEncoder.encode(lastUpdated.toString(), "UTF-8")
                            + "&description=" + URLEncoder.encode(toDoListItems.getDescription(), "UTF-8") + "&operationType=" + URLEncoder.encode(Constants.KEY_Insert, "UTF-8")
                            + "&status=" + URLEncoder.encode(toDoListItems.getStatus(), "UTF-8") + "&listId=" + URLEncoder.encode(serverListKey, "UTF-8") + "&public=" + URLEncoder.encode("1", "UTF-8"));
                    result = restClient.postRequest(url, user.getName(), user.getPassword(), toDoListItems.getLastUpdated());
                    String id = common.ParseJsonFromHttpResponse(result, "id", "List");
                    //toDoListItems.setListId(toDoList.GetServerKey());
                    toDoListItems.SetServerKey(Integer.valueOf(id));
                    mDb.updateToDoListItem(toDoListItems, Constants.KEY_Insert);
                }
            }
        }
    }
}
