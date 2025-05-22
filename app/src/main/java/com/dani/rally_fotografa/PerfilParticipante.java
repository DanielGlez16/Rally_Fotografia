package com.dani.rally_fotografa;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilParticipante extends AppCompatActivity {

    private TextView textEmail;
    private EditText etNombre;
    private Button btnGuardar, btnCambiarContrasena, btnVerGaleria;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_participante);

        textEmail = findViewById(R.id.textEmail);
        etNombre = findViewById(R.id.etNombre);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCambiarContrasena = findViewById(R.id.btnCambiarContrasena);
        btnVerGaleria = findViewById(R.id.btnVerGaleria);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        cargarDatosPerfil();

        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString().trim();
            if (TextUtils.isEmpty(nuevoNombre)) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            } else {
                guardarNombreEnFirestore(nuevoNombre);
            }
        });

        btnCambiarContrasena.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad futura", Toast.LENGTH_SHORT).show();
        });

        btnVerGaleria.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, GaleriaParticipante.class));
        });
    }

    private void cargarDatosPerfil() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String email = document.getString("email");
                        String nombre = document.getString("name");
                        textEmail.setText(email != null ? email : "Sin email");
                        etNombre.setText(nombre != null ? nombre : "");
                    } else {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarNombreEnFirestore(String nuevoNombre) {
        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .update("name", nuevoNombre)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
