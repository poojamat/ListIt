package com.mathur.android.listit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Pooja Mathur on 11/9/2015.
 */
public class RVListAdapter extends RecyclerView.Adapter<RVListAdapter.ItemViewHolder>

{
    private static MyClickListener myCliCkListner;
    private List<ToDoListItems> mDatasetToDos;
    private int mlistId;
    private Context mContext;
    private boolean mOnBind;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder


    // Provide a suitable constructor (depends on the kind of dataset)
    public RVListAdapter(List<ToDoListItems> toDoListItems, int listId) {

        mDatasetToDos = toDoListItems;
        mlistId = listId;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_view_list, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder itemViewHolder, final int i) {

        itemViewHolder.itemDescription.setText(String.valueOf(mDatasetToDos.get(i).getDescription()));
        final String status = String.valueOf(mDatasetToDos.get(i).getStatus());

        itemViewHolder.itemDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateDialog(i, itemViewHolder, status);
            }
        });

        itemViewHolder.itemRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(i, String.valueOf(mDatasetToDos.get(i).getDescription()), String.valueOf(mDatasetToDos.get(i).getStatus()));
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
                updateStatus(i, String.valueOf(mDatasetToDos.get(i).getDescription()), String.valueOf(isChecked));
            }
        });

        itemViewHolder.itemAmazon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToAmazon(String.valueOf(mDatasetToDos.get(i).getDescription()));
            }
        });

        itemViewHolder.itemCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToCalender(String.valueOf(mDatasetToDos.get(i).getDescription()));
            }
        });

    }

    private void UpdateDialog(final int i, final ItemViewHolder itemViewHolder, final String status) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("List");
        //builder.setIcon(R.drawable.icon);
        builder
                .setMessage("Edit Item");
        final MySQLiteHelper db = new MySQLiteHelper(mContext);
        final EditText textView = new EditText(mContext);
        textView.setText(mDatasetToDos.get(i).getDescription());
        builder.setView(textView);
        builder.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String desc = textView.getText().toString();
                        if (desc != null && !desc.isEmpty()) {
                            ToDoListItems toDoListItems = new ToDoListItems(mDatasetToDos.get(i).getId(), desc, status, mlistId);
                            itemViewHolder.itemDescription.setText(desc);
                            toDoListItems.SetSyncStatus(0);
                            db.updateToDoListItem(toDoListItems,Constants.KEY_Update);
                            updateItem(db.getToDoListItemByListId(String.valueOf(mlistId)));
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

    private void GoToAmazon(String itemDescription) {
        String url = "http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=" + itemDescription;
        url="http://www.amazon.com/gp/search?ie=UTF8&camp=1789&creative=9325&index=aps&keywords="+itemDescription+"&linkCode=ur2&tag=mylistapp-20&linkId=TA3FXTICIPQLBCK2";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mContext.startActivity(intent);
    }

    private void GoToCalender(String itemDescription) {
        Calendar beginTime = Calendar.getInstance();
        // beginTime.set(2012, 0, 19, 7, 30);
        Calendar endTime = Calendar.getInstance();
        //endTime.set(2012, 0, 19, 8, 30);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(Events.TITLE, itemDescription)
                .putExtra(Events.DESCRIPTION, "To Do" + itemDescription)
                        //.putExtra(Events.EVENT_LOCATION, "The gym")
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        //.putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
        mContext.startActivity(intent);

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


    public void addItem(ToDoListItems toDoListItems) {
        mDatasetToDos.add(toDoListItems);
        int index = getItemCount() + 1;
        notifyItemInserted(index);
    }

    public void updateItem(List<ToDoListItems> updatedItems) {
        mDatasetToDos = updatedItems;
        //notifyDataSetChanged();
    }

    public void deleteItem(int index, String desc, String status) {

        final MySQLiteHelper db = new MySQLiteHelper(mContext);
        ToDoListItems toDoListItems = new ToDoListItems(mDatasetToDos.get(index).getId(), desc, status, mlistId);
        //db.deleteToDoListItems(toDoListItems);
        toDoListItems.SetSyncStatus(0);
        db.updateToDoListItem(toDoListItems, Constants.KEY_Delete);
        mDatasetToDos.remove(index);
        notifyItemRemoved(index);
    }

    public void updateStatus(int index, String desc, String status) {
        if (!mOnBind) {
            final MySQLiteHelper db = new MySQLiteHelper(mContext);
            //TODO status needs to be converted to bool
            ToDoListItems toDoListItems = new ToDoListItems(mDatasetToDos.get(index).getId(), desc, status, mlistId);
            toDoListItems.SetSyncStatus(0);
            db.updateToDoListItem(toDoListItems,Constants.KEY_Update);
            updateItem(db.getToDoListItemByListId(String.valueOf(mlistId)));
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
        ImageButton itemAmazon;
        ImageButton itemCalendar;

        ItemViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            //itemId = (TextView) itemView.findViewById(R.id.item_id);
            itemDescription = (TextView) itemView.findViewById(R.id.item_description);
            itemStatus = (CheckBox) itemView.findViewById(R.id.item_status);
            itemRemove = (Button) itemView.findViewById(R.id.item_Remove);
            itemAmazon = (ImageButton) itemView.findViewById(R.id.item_Amazon);
            itemCalendar = (ImageButton) itemView.findViewById(R.id.item_Calender);
        }

        @Override
        public void onClick(View v) {
            myCliCkListner.onItemClick(getPosition(), v);
        }
    }


}