package com.mathur.android.listit;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView.Adapter mAdapter;
    private List<ToDoListItems> toDoListItems;
    private int mItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_list);

        RecyclerView mRecyclerView;
        mRecyclerView = (RecyclerView) findViewById(R.id.UserLists);
        Intent intent = getIntent();
        String message = intent.getStringExtra(RVAdapter.ITEM_NAME);
        TextView nameText = (TextView) findViewById(R.id.ListName_text);
        nameText.setText(message);
        mRecyclerView.setHasFixedSize(true);
        mItemId=intent.getIntExtra(RVAdapter.ITEM_ID, 0);
        initializeData(String.valueOf(mItemId));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RVListAdapter(toDoListItems,mItemId );
        mRecyclerView.setAdapter(mAdapter);

    }

    public void addItem(View view) {
        ShowAddDialog();
    }

    private void ShowAddDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("List");
        //builder.setIcon(R.drawable.icon);
        builder
                .setMessage("Add Item");
        final MySQLiteHelper db = new MySQLiteHelper(this);
        final EditText textView = new EditText(this);
        builder.setView(textView);
        builder.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String desc = textView.getText().toString();
                        if (desc != null && !desc.isEmpty()) {
                            ToDoListItems toDoListItems = new ToDoListItems(1, desc, "false",mItemId);
                            toDoListItems.SetSyncStatus(0);
                            ((RVListAdapter) mAdapter).addItem(toDoListItems);
                            db.addToDoListItems(toDoListItems);
                        }
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        builder.show();
    }

    public void updateItem(View view) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("List");
        //builder.setIcon(R.drawable.icon);
        builder
                .setMessage("Update Item");
        final EditText textView = new EditText(this);
        // final RecyclerView recyclerView= (RecyclerView) view;
        builder.setView(textView);
        builder.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
//
//
//
//                        if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
//                            Drawer.closeDrawers();
//                            Toast.makeText(MainActivity.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
//
//                            return true;
//
//                        }
//
//                        return false;
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void emailToDoList(View view) {
        String emailBody = "To Do List \n";
        Log.i("MainActivity", "Mailing to to ");
        for (ToDoListItems i : toDoListItems) {
            emailBody += "-" + i.getDescription() + "\n";
        }
        emailBody += "-" +"\n created by List It App on android" ;
        //String emailTo = "poojamat@gmail.com";
        String emailSubject = "To Do List Items";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void initializeData(String listId) {
        MySQLiteHelper db;
        db = new MySQLiteHelper(this);
        toDoListItems = new ArrayList<>();
        List<ToDoListItems> list = db.getToDoListItemByListId(listId);
        for (ToDoListItems i : list) {
            toDoListItems.add(i);
        }

    }
}
