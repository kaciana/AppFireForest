package com.example.myapplicationtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibratorManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.myapplicationtest.databinding.ActivityTelaPrincipalBinding;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.Status;

public class TelaPrincipalActivity extends AppCompatActivity {

    private ActivityTelaPrincipalBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseMessaging messaging = FirebaseMessaging.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reference = db.collection("Leituras");
    private ArrayList<String> leituras = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelaPrincipalBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(view -> {
            auth.signOut();
            finish();
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, leituras);
        binding.lista.setAdapter(adapter);

        messaging.getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.d("TOKEN", token);

                Map<String, Object> data = new HashMap<>();
                data.put("token", token);

                db.collection("Clientes").document("token").set(data)
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.d("TOKEN", "DocumentSnapshot successfully updated!");
                    }
                });
            }
        });

        reference.addSnapshotListener((value, error) -> {
            if (error == null) {
                value.getQuery().orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String bmp280 = document.getData().get("bmp280").toString()+" °C";
                            String mq2 = document.getData().get("mq2").toString()+" ppm";
                            String ccs811 = document.getData().get("ccs811").toString()+" ppm";
                            String timestamp = document.getData().get("timestamp").toString();

                            binding.textTemp.setText(bmp280);
                            binding.textGas.setText(mq2);
                            binding.textCo2.setText(ccs811);
                            binding.textTime.setText(timestamp);

                            leituras.add(0, bmp280+"; "+mq2+"; "+ccs811+"\n"+timestamp);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
    }
}