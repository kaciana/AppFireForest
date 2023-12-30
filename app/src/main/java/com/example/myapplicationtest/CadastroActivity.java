package com.example.myapplicationtest;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.example.myapplicationtest.databinding.ActivityCadastroBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.btAcesso.setOnClickListener(view -> {
            String email = binding.editEmail.getText().toString();
            String senha = binding.editSenha.getText().toString();
            String endereco = binding.editEndereco.getText().toString();
            String telefone = binding.editTelefone.getText().toString();

            if(email.isEmpty() || senha.isEmpty() || endereco.isEmpty() || telefone.isEmpty()) {
                Toast.makeText(this, "Campos em branco!", Toast.LENGTH_SHORT).show();
            }
            else {
                auth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(cadastro -> {
                            if(cadastro.isSuccessful()) {

                                Map<String, Object> cliente = new HashMap<>();
                                cliente.put("endereço", endereco);
                                cliente.put("telefone", telefone);

                                db.collection("Clientes").document(email).set(cliente);

                                Toast.makeText(this, "Cliente cadastrado com sucessso!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(exception -> {
                            String menssagemErro;
                            if(exception instanceof FirebaseAuthWeakPasswordException){
                                menssagemErro = "Senha muito curta!";
                            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                menssagemErro = "Digite um email válido!";
                            } else if (exception instanceof FirebaseAuthUserCollisionException) {
                                menssagemErro = "Esta conta já foi cadastrada!";
                            } else if (exception instanceof FirebaseNetworkException) {
                                menssagemErro = "Sem conexão com a Internet!";
                            } else {
                                menssagemErro = "Erro ao cadastrar usuário!";
                            }

                            Toast.makeText(this, menssagemErro, Toast.LENGTH_SHORT).show();
                        });
            }

        });
    }
}