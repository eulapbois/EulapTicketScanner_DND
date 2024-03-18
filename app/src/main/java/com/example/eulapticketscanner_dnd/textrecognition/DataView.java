package com.example.eulapticketscanner_dnd.textrecognition;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eulapticketscanner_dnd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DataView extends AppCompatActivity {

    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    ArrayList<TicketNumberModel> ticketNumberModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_view);

        RecyclerView recyclerView1 = findViewById(R.id.ticketNumbeRecyclerView);

//        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Fetching Data..");
//        progressDialog.show();


//        ticketArrayList = new ArrayList<Ticket>();


        db = FirebaseFirestore.getInstance();
        db.collection("TVS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String ticketNumber = document.getString("TicketNumber");
                                Toast.makeText(DataView.this, "this " + ticketNumber , Toast.LENGTH_SHORT).show();
                                ticketNumberModels.add(new TicketNumberModel(ticketNumber));

                                list.add(document.getId());
                                list.add(ticketNumber);
                            }
                        } else {
                            Toast.makeText(DataView.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        TicketNumberRecycleviewAdapter adapter = new TicketNumberRecycleviewAdapter(this, ticketNumberModels);
        recyclerView1.setAdapter(adapter);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
    }
}
