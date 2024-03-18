package com.example.eulapticketscanner_dnd.text_reco;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eulapticketscanner_dnd.R;

import java.util.ArrayList;

public class TicketNumberRecycleviewAdapter extends RecyclerView.Adapter<TicketNumberRecycleviewAdapter.MyViewHolder> {
    Context context;
    ArrayList<TicketNumberModel> ticketNumberModels;

    public TicketNumberRecycleviewAdapter(Context context, ArrayList<TicketNumberModel> ticketNumberModels){
        this.context = context;
        this.ticketNumberModels = ticketNumberModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ticker_numbers_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.ticketNumber.setText(ticketNumberModels.get(position).getTicketNumber());
    }

    @Override
    public int getItemCount() {
        return ticketNumberModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView ticketNumber;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ticketNumber = itemView.findViewById(R.id.ticketNumber);
        }
    }
}
