package ca.grasley.spaceshooter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

            // Leer datos desde Firestore
            db.collection("usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("nombre");
                            Long puntos = documentSnapshot.getLong("puntos");
                            Long ranking = documentSnapshot.getLong("ranking");

                            tvUserName.setText("Hola, " + (nombre != null ? nombre : "Jugador"));
                            tvUserPoints.setText(String.valueOf(puntos != null ? puntos : 0));
                            tvUserRanking.setText(String.valueOf(ranking != null ? ranking : 0));
                        } else {
                            // Si no existe el doc, muestra valores por defecto
                            tvUserName.setText("Hola, Jugador");
                            tvUserPoints.setText("0");
                            tvUserRanking.setText("0");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("PaginaPrincipal", "Error al obtener datos", e);
                        // Valores por defecto en caso de error
                        tvUserName.setText("Hola, Jugador");
                        tvUserPoints.setText("0");
                        tvUserRanking.setText("0");
                    });

            // Cargar imagen perfil (si tiene)
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
        } else {
            // Usuario no autenticado
            tvUserName.setText("Hola, Invitado");
            tvUserPoints.setText("0");
            tvUserRanking.setText("0");
        }

        btnStartGame.setOnClickListener(v -> {
            // TODO: Cambiar por tu pantalla de juego
            Log.d("PaginaPrincipal", "Iniciar juego");
        });

        btnRanking.setOnClickListener(v -> startActivity(new Intent(this, Ranking.class)));

        btnOptions.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }
}
