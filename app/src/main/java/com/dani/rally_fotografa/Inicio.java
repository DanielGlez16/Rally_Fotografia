package com.dani.rally_fotografa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Inicio extends AppCompatActivity {

    Button btnRegistro, btnLogin, btnGaleria, btnVerBases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        btnRegistro = findViewById(R.id.btnRegistro);
        btnLogin = findViewById(R.id.btnLogin);
        btnGaleria = findViewById(R.id.btnGaleria);
        btnVerBases = findViewById(R.id.btnVerBases); // ← Nuevo botón

        btnRegistro.setOnClickListener(v ->
                startActivity(new Intent(this, Registro.class))
        );

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, Login.class))
        );

        btnGaleria.setOnClickListener(v ->
                startActivity(new Intent(this, GaleriaPublica.class))
        );

        btnVerBases.setOnClickListener(v ->
                startActivity(new Intent(this, BasesConcurso.class))
        );
    }
}
