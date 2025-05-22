package com.dani.rally_fotografa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    Button btnValidarFotos, btnConfigurarRally, btnEstadisticas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnValidarFotos = findViewById(R.id.btnValidarFotos);
        btnConfigurarRally = findViewById(R.id.btnConfigurarRally);
        btnEstadisticas = findViewById(R.id.btnEstadisticas);

        btnValidarFotos.setOnClickListener(v ->
                startActivity(new Intent(this, ValidarFotos.class))
        );

        btnConfigurarRally.setOnClickListener(v ->
                startActivity(new Intent(this, ConfigurarRally.class)) // Clase que aún podemos crear
        );

        btnEstadisticas.setOnClickListener(v ->
                startActivity(new Intent(this, EstadisticasAdmin.class)) // Clase futura
        );
    }
}
