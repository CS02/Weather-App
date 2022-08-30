package com.example.getweather;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
* This class allows the recycler view to be populated with data from the database
* */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private Cursor cursor;

    //an interface allows the recyclerview to be clickable
    private final RecyclerViewInterface recyclerViewInterface;

    //constructor
    public MyAdapter(Context context, Cursor cursor, RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.cursor = cursor;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cityentry,parent,false);
        return new MyViewHolder(v, recyclerViewInterface);
    }
    //responsible for loading new data when scrolling
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(cursor.moveToPosition(position)){
            holder.city.setText(cursor.getString(0));
            holder.desc.setText(cursor.getString(1));
            holder.temp.setText(cursor.getString(2));
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
    //Adds text to each item
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView city, desc, temp;
        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            city = itemView.findViewById(R.id.city);
            desc = itemView.findViewById(R.id.desc);
            temp = itemView.findViewById(R.id.temp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        //gets the item position that was clicked on
                        int position = getAdapterPosition();
                        //moves cursor to the city that was clicked on
                        cursor.moveToPosition(position);
                        //gets city name
                        String name = cursor.getString(0);
                        if(position!= RecyclerView.NO_POSITION){
                            //confirms that the city was clicked on
                            //initiates the next screen
                            recyclerViewInterface.onItemClick(name);
                        }
                    }
                }
            });
        }
    }
}
