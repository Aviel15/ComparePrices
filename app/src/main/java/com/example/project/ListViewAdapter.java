package com.example.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * List view adapter to list view basket, represent the amount and product name
 */
public class ListViewAdapter extends BaseAdapter {
    //properties
    private final Context context;
    private final ArrayList<String> listName;
    private final ArrayList<Integer> listAmount;

    /**
     * constructor
     * @param context - context
     * @param listName - array of type string, contain all products names in basket list
     * @param listAmount - array of type integer, contain all amounts of products in basket list
     */
    public ListViewAdapter(Context context, ArrayList<String> listName, ArrayList<Integer> listAmount){
        this.context = context;
        this.listName = listName;
        this.listAmount = listAmount;
    }

    /**
     * return the listName size
     * @return - list size
     */
    @Override
    public int getCount() {
        return listName.size();
    }

    /**
     * @param arg0 - get argument, index
     * @return - return the index
     */
    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    /**
     * @param arg0 - get argument, index
     * @return - return the index
     */
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    /**
     * used to create a view for each item in a list of items. This method is called for each item in the list
     * @param position - the position of the item in the list for which the view is being created
     * @param convertView - a view at the given position
     * @param arg2 - the view that returned view will be added to him
     * @return - return the View rowView
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.listview_adapter, arg2, false);
        TextView textView = rowView.findViewById(R.id.name);                   //text view to product name
        EditText editText = rowView.findViewById(R.id.editText);               //edit text to amount

        textView.setTag(listName.get(position));
        textView.setText(listName.get(position));

        editText.setText(listAmount.get(position).toString());


        return rowView;
    }

}