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
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.myapplicationtest.databinding.ActivityTelaPrincipalBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.grpc.Status;

public class TelaPrincipalActivity extends AppCompatActivity {

    private ActivityTelaPrincipalBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference rt = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> leituras = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    static final String CHANNEL_ID = "fireforecast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelaPrincipalBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        createNotificationChannel();

        if (ActivityCompat.checkSelfPermission(TelaPrincipalActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TelaPrincipalActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }

        binding.button.setOnClickListener(view -> {
            auth.signOut();
            finish();
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, leituras);
        binding.lista.setAdapter(adapter);

        rt.child("Sensores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Object> sensores = (Map<String, Object>) snapshot.getValue();

                if(sensores != null) {

                    binding.textTemp.setText(sensores.get("bmp280").toString() + "°C");
                    String leitura = sensores.get("bmp280").toString() + "°C; ";

                    binding.textGas.setText(sensores.get("mq2").toString());
                    leitura += sensores.get("mq2").toString() + "; ";
                    int mq2 = Integer.parseInt(sensores.get("mq2").toString());

                    binding.textTime.setText(sensores.get("timestamp").toString());
                    leitura += sensores.get("timestamp").toString();

                    leituras.add(0, leitura);
                    adapter.notifyDataSetChanged();

                    if (mq2 > 2000) {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(TelaPrincipalActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.resource_super)
                                .setContentTitle("Alerta de incêndio!!!")
                                .setContentText("Risco de incêndio detectado. Vá para um local seguro!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        if (ActivityCompat.checkSelfPermission(TelaPrincipalActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                                == PackageManager.PERMISSION_GRANTED) {

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(TelaPrincipalActivity.this);
                            notificationManager.notify(100, builder.build());
                        } else {
                            ActivityCompat.requestPermissions(TelaPrincipalActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alerta";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.enableVibration(true);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
    }

}