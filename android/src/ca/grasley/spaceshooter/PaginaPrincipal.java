package ca.grasley.spaceshooter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaginaPrincipal extends AppCompatActivity {

    private TextView tvUserName, tvUserPoints, tvUserRanking;
    private ShapeableImageView mainProfileImage;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);

        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias de vistas
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPoints = findViewById(R.id.tvUserPoints);
        tvUserRanking = findViewById(R.id.tvUserRanking);
        mainProfileImage = findViewById(R.id.mainProfileImage);

        Button btnStartGame = findViewById(R.id.btnStartGame);
        Button btnRanking = findViewById(R.id.btnRanking);
        Button btnOptions = findViewById(R.id.btnOptions);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String displayName = currentUser.getDisplayName();

            // Mostrar nombre
            tvUserName.setText("Hola, " + (displayName != null ? displayName : "Jugador"));

            // Cargar puntos y ranking desde Firestore
            db.collection("usuarios").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    long puntos = documentSnapshot.getLong("puntos") != null ? documentSnapshot.getLong("puntos") : 0;
                    long ranking = documentSnapshot.getLong("ranking") != null ? documentSnapshot.getLong("ranking") : 0;

                    tvUserPoints.setText(String.valueOf(puntos));
                    tvUserRanking.setText(String.valueOf(ranking));
                }
            }).addOnFailureListener(e -> {
                Log.e("PaginaPrincipal", "Error al obtener datos de usuario", e);
            });

            // Cargar imagen de perfil (si es login con Google)
            if (currentUser.getPhotoUrl() != null) {
                new Thread(() -> {
                    try {
                        URL url = new URL(currentUser.getPhotoUrl().toString());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                        runOnUiThread(() -> mainProfileImage.setImageBitmap(myBitmap));
                    } catch (IOException e) {
                        Log.e("PaginaPrincipal", "Error cargando imagen", e);
                    }
                }).start();
            }
        }

        // Navegación de botones
        btnStartGame.setOnClickListener(v -> {
            // TODO: Reemplaza por la clase de tu pantalla de juego
            // startActivity(new Intent(this, GameActivity.class));
            Log.d("PaginaPrincipal", "Iniciar juego");
        });

        btnRanking.setOnClickListener(v -> {
            startActivity(new Intent(this, Ranking.class));
        });

        btnOptions.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
    }
}
