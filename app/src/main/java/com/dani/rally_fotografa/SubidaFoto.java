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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class subida_foto extends AppCompatActivity {

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
        btnSubir.setOnClickListener(v -> subirImagen());
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        filePickerLauncher.launch(intent);
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

                    db.collection("photos").add(photoData)
                            .addOnSuccessListener(documentReference -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Foto subida. Esperando validación", Toast.LENGTH_LONG).show();
                                finish(); // opcional
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
