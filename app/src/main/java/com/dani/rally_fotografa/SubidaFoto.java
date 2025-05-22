package com.dani.rally_fotografa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubidaFoto extends AppCompatActivity {

    private ImageView imagePreview;
    private Button btnSeleccionar, btnSubir;
    private Uri imageUri;

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imagePreview.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subida_foto);

        imagePreview = findViewById(R.id.imagePreview);
        btnSeleccionar = findViewById(R.id.btnSeleccionar);
        btnSubir = findViewById(R.id.btnSubir);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnSeleccionar.setOnClickListener(v -> seleccionarImagen());
        btnSubir.setOnClickListener(v -> validarLimiteYSubir());
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        filePickerLauncher.launch(intent);
    }

    private void validarLimiteYSubir() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("config").document("rally").get()
                .addOnSuccessListener(configDoc -> {
                    long limite = configDoc.contains("limiteFotos") ?
                            configDoc.getLong("limiteFotos") : 5;

                    long fechaFinSubida = configDoc.contains("fechaFinSubida") ?
                            configDoc.getLong("fechaFinSubida") : Long.MAX_VALUE;

                    long ahora = System.currentTimeMillis();
                    if (ahora > fechaFinSubida) {
                        Toast.makeText(this, "El plazo para subir fotos ha finalizado", Toast.LENGTH_LONG).show();
                        return;
                    }

                    db.collection("fotos")
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener(fotos -> {
                                if (fotos.size() >= limite) {
                                    Toast.makeText(this, "Has alcanzado el máximo de " + limite + " fotos permitidas", Toast.LENGTH_LONG).show();
                                } else {
                                    subirImagen(); // Todo correcto, se permite subir
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error al contar fotos", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al obtener configuración", Toast.LENGTH_SHORT).show());
    }


    private void subirImagen() {
        if (imageUri == null) {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Subiendo imagen...");
        progressDialog.show();

        String userId = auth.getCurrentUser().getUid();
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference ref = storage.getReference().child("photos/" + userId + "/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();

                    Map<String, Object> photoData = new HashMap<>();
                    photoData.put("userId", userId);
                    photoData.put("url", downloadUrl);
                    photoData.put("estado", "pendiente");
                    photoData.put("timestamp", System.currentTimeMillis());
                    photoData.put("votos", 0);

                    db.collection("fotos").add(photoData)
                            .addOnSuccessListener(documentReference -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Foto subida. Esperando validación", Toast.LENGTH_LONG).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Error al guardar en base de datos", Toast.LENGTH_SHORT).show();
                            });

                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
