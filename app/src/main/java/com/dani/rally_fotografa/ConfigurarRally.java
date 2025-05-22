package com.dani.rally_fotografa;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ConfigurarRally extends AppCompatActivity {

    private EditText etLimiteFotos, etFechaFinSubida, etFechaFinVotacion;
    private Button btnGuardar;

    private FirebaseFirestore db;
    private DocumentReference configRef;

    private final String ID_CONFIG = "rally";
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar_rally);

        etLimiteFotos = findViewById(R.id.etLimiteFotos);
        etFechaFinSubida = findViewById(R.id.etFechaFinSubida);
        etFechaFinVotacion = findViewById(R.id.etFechaFinVotacion);
        btnGuardar = findViewById(R.id.btnGuardar);

        db = FirebaseFirestore.getInstance();
        configRef = db.collection("config").document(ID_CONFIG);

        cargarConfiguracion();

        btnGuardar.setOnClickListener(v -> guardarConfiguracion());
    }

    private void cargarConfiguracion() {
        configRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                if (doc.contains("limiteFotos"))
                    etLimiteFotos.setText(String.valueOf(doc.getLong("limiteFotos")));

                if (doc.contains("fechaFinSubida")) {
                    long fechaMillis = doc.getLong("fechaFinSubida");
                    etFechaFinSubida.setText(millisAFecha(fechaMillis));
                }

                if (doc.contains("fechaFinVotacion")) {
                    long fechaMillis = doc.getLong("fechaFinVotacion");
                    etFechaFinVotacion.setText(millisAFecha(fechaMillis));
                }
            }
        });
    }

    private void guardarConfiguracion() {
        String limiteTexto = etLimiteFotos.getText().toString().trim();
        String fechaSubida = etFechaFinSubida.getText().toString().trim();
        String fechaVotacion = etFechaFinVotacion.getText().toString().trim();

        if (TextUtils.isEmpty(limiteTexto) || TextUtils.isEmpty(fechaSubida) || TextUtils.isEmpty(fechaVotacion)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int limiteFotos = Integer.parseInt(limiteTexto);
        long fechaSubidaMillis = convertirFecha(fechaSubida);
        long fechaVotacionMillis = convertirFecha(fechaVotacion);

        if (fechaSubidaMillis == -1 || fechaVotacionMillis == -1) {
            Toast.makeText(this, "Formato de fecha incorrecto (usa dd/MM/yyyy)", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, Object> config = new HashMap<>();
        config.put("limiteFotos", limiteFotos);
        config.put("fechaFinSubida", fechaSubidaMillis);
        config.put("fechaFinVotacion", fechaVotacionMillis);

        configRef.set(config)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private long convertirFecha(String fecha) {
        try {
            Date date = sdf.parse(fecha);
            return date != null ? date.getTime() : -1;
        } catch (ParseException e) {
            return -1;
        }
    }

    private String millisAFecha(Long millis) {
        if (millis == null || millis == 0) return "";
        return sdf.format(new Date(millis));
    }
}
