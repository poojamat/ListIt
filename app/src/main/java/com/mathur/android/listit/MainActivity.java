package com.mathur.android.listit;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ToDoLists> mToDoLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        })*/
        ;
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.UserLists);


        mRecyclerView.setHasFixedSize(true);
        initializeData();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RVAdapter(mToDoLists);
        mRecyclerView.setAdapter(mAdapter);
        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });


    }

    public void addItem(View view) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("List");
        //builder.setIcon(R.drawable.icon);
        builder
                .setMessage("Add List");
        final MySQLiteHelper db = new MySQLiteHelper(this);
        final EditText textView = new EditText(this);
        builder.setView(textView);
        builder.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String desc = textView.getText().toString();
                        if (desc != null && !desc.isEmpty()) {
                            ToDoLists toDoLists = new ToDoLists(1,desc, "false");
                            int listId = (int) db.addToDoLists(toDoLists);
                           // ((RVAdapter) mAdapter).addItem(toDoLists);
                            ((RVAdapter) mAdapter).updateItem(db.getAllToDoLists());
                            ((RVAdapter) mAdapter).ShowListIntent(listId,desc);

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
        final MySQLiteHelper db = new MySQLiteHelper(this);
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
        String emailBody= "To Do Lists \n";
        MySQLiteHelper db = new MySQLiteHelper(this);
        Log.i("MainActivity", "Mailing to to ");
        for (ToDoLists i : mToDoLists) {
          //  if ((new String("true").equals(i.getStatus())))
           // {
                emailBody +=  i.getName() + "\n";
                List<ToDoListItems> list = db.getToDoListIemByListId(String.valueOf(i.getId()));
                for(ToDoListItems item: list) {
                    emailBody +=  " -"+item.getDescription() + "\n";
                }
            //}
            emailBody +=  "\n\n\n";
        }

        //String emailTo = "poojamat@gmail.com";
        String emailSubject = "To Do List Items";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        intent.putExtra(Intent.EXTRA_TEXT,emailBody);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void initializeData() {
        MySQLiteHelper db = new MySQLiteHelper(this);
        mToDoLists = new ArrayList<>();
        List<ToDoLists> list = db.getAllToDoLists();
        for (ToDoLists i : list) {
            mToDoLists.add(i);
        }

    }
}
