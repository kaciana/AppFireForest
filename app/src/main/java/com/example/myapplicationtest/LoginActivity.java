package com.example.myapplicationtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.example.myapplicationtest.databinding.ActivityCadastroBinding;
import com.example.myapplicationtest.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        createNotificationChannel();

        binding.textCriar.setOnClickListener(view -> {
            Intent intent = new Intent(this, CadastroActivity.class);
            startActivity(intent);
        });

        binding.btAcesso.setOnClickListener(view -> {
            String email = binding.editEmail.getText().toString();
            String senha = binding.editSenha.getText().toString();

            if(email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Campos em branco!", Toast.LENGTH_SHORT).show();
            }
            else {
                auth.signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(login -> {
                            if(login.isSuccessful()) {
                                Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, TelaPrincipalActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(exception ->{
                            String menssagemErro;
                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                menssagemErro = "Email ou senha incorretos!";
                            } else if (exception instanceof FirebaseNetworkException) {
                                menssagemErro = "Sem conexão com a Internet!";
                            } else {
                                menssagemErro = "Erro ao fazer login do usuário!";
                            }
                            Toast.makeText(this, menssagemErro, Toast.LENGTH_SHORT).show();
                        });
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = auth.getCurrentUser();
        if(usuarioAtual != null){
            Intent intent = new Intent(this, TelaPrincipalActivity.class);
            Toast.makeText(this, usuarioAtual.getEmail().toString(), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alerta";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel_id), name, importance);
            channel.enableVibration(true);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            notificationManager.cancelAll();

            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}