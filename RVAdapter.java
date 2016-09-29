package com.mathur.android.listit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by us48114 on 11/9/2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder>

{
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public final static String ITEM_ID = "com.Pooja.ListIt.ITEMID";
    public final static String ITEM_NAME = "com.Pooja.ListIt.ITEMNAME";
    private static MyClickListener myCliCkListner;
    private List<ToDoList> mDatasetToDos;
    private Context mContext;
    private boolean mOnBind;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RVAdapter(List<ToDoList> toDoLists) {

        mDatasetToDos = toDoLists;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_view, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder itemViewHolder, final int i) {
        final int listId =(mDatasetToDos.get(i).getId());
        itemViewHolder.itemDescription.setText(String.valueOf(mDatasetToDos.get(i).getName()));
        String status = String.valueOf(mDatasetToDos.get(i).getStatus());

        itemViewHolder.itemDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UpdateDialog(i, itemViewHolder);
                ShowListIntent(listId, String.valueOf(mDatasetToDos.get(i).getName()));
            }
        });

        itemViewHolder.itemRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(i, String.valueOf(mDatasetToDos.get(i).getName()), String.valueOf(mDatasetToDos.get(i).getName()));
            }

        });
        //in some cases, it will prevent unwanted situations
        //itemViewHolder.itemStatus.setOnCheckedChangeListener(null);
        mOnBind = true;
        //if true, your checkbox will be selected, else unselected
        itemViewHolder.itemStatus.setChecked(mDatasetToDos.get(i).IsStatusChecked());
        mOnBind = false;
        itemViewHolder.itemStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            //TODO status needs to be converted to bool
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatus(i, String.valueOf(mDatasetToDos.get(i).getName()), String.valueOf(isChecked));
            }
        });

    }

    public void ShowListIntent(int listId, String desc) {
        Intent intent = new Intent(mContext, ListActivity.class);
        intent.putExtra(ITEM_ID, listId);
        intent.putExtra(ITEM_NAME, desc);
        mContext.startActivity(intent);
    }

    private void UpdateDialog(final int i, final ItemViewHolder itemViewHolder) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("List");
        //builder.setIcon(R.drawable.icon);
        builder
                .setMessage("Edit Item");
        final MySQLiteHelper db = new MySQLiteHelper(mContext);
        final EditText textView = new EditText(mContext);
        textView.setText(mDatasetToDos.get(i).getName());
        builder.setView(textView);
        builder.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String desc = textView.getText().toString();
                        if (desc != null && !desc.isEmpty()) {
                        //    ToDoListItems toDoListItems = new ToDoListItems(mDatasetToDos.get(i).getId(), desc, "Y");
                            //itemViewHolder.itemDescription.setText(desc);
                            //db.updateToDoListItem(toDoListItems);
                            //updateItem(db.getAllToDoLists());

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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetToDos.size();
    }


    public void addItem(ToDoList toDoLists) {
        mDatasetToDos.add(toDoLists);
        int index = getItemCount() + 1;
        notifyItemInserted(index);
    }

    public void updateItem(List<ToDoList> updatedItems) {
        mDatasetToDos = updatedItems;
        //notifyDataSetChanged();
    }

    public void deleteItem(int index, String desc, String status) {

        final MySQLiteHelper db = new MySQLiteHelper(mContext);
        ToDoList toDoLists = new ToDoList(mDatasetToDos.get(index).getId(), desc, status);
        toDoLists.SetSyncStatus(0);
//        db.deleteToDoLists(toDoLists);
        db.updateToDoList(toDoLists, Constants.KEY_Delete);
        //     db.deleteToDoListItemsByListId(String.valueOf(mDatasetToDos.get(index).getId()));
        db.updateToDoListItemByListId(String.valueOf(mDatasetToDos.get(index).getId()), Constants.KEY_Delete);
        mDatasetToDos.remove(index);
        notifyItemRemoved(index);
    }

    public void updateStatus(int index, String desc, String status) {
        if (!mOnBind) {
            final MySQLiteHelper db = new MySQLiteHelper(mContext);
//TODO status needs to be converted to bool
            // if (status != "Y") {
            //    status = "Y";
            // } else {
            // status = "N";
            //}
            ToDoList toDoLists = new ToDoList(mDatasetToDos.get(index).getId(), desc, status);
            toDoLists.SetSyncStatus(0);
            db.updateToDoList(toDoLists, Constants.KEY_Update);
            updateItem(db.getAllToDoLists());

            //notifyDataSetChanged();
        }
    }


    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        CardView cv;
        TextView itemId;
        TextView itemDescription;
        CheckBox itemStatus;
        Button itemRemove;

        ItemViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            //itemId = (TextView) itemView.findViewById(R.id.item_id);
            itemDescription = (TextView) itemView.findViewById(R.id.item_description);
            itemStatus = (CheckBox) itemView.findViewById(R.id.item_status);
            itemRemove = (Button) itemView.findViewById(R.id.item_Remove);
        }

        @Override
        public void onClick(View v) {
            myCliCkListner.onItemClick(getPosition(), v);
        }
    }


}