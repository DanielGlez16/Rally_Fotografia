package com.dani.rally_fotografa;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        // 🔁 Redirige directamente a la pantalla de subida de fotos
        startActivity(new Intent(this, SubidaFoto.class));
        finish(); // Para evitar volver atrás
    }
}
