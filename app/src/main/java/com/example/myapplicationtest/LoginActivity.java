package com.example.myapplicationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

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
                                menssagemErro = "Digite um email válido!";
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
            startActivity(intent);
        }
    }

}