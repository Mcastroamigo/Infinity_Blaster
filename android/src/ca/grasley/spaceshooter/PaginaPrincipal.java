package ca.grasley.spaceshooter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

            db.collection("usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("nombre");
                            Long puntos = documentSnapshot.getLong("puntos");

                            tvUserName.setText("Hola, " + (nombre != null ? nombre : "Jugador"));
                            tvUserPoints.setText(String.valueOf(puntos != null ? puntos : 0));

                            calcularYMostrarRanking(uid);
                        } else {
                            tvUserName.setText("Hola, Jugador");
                            tvUserPoints.setText("0");
                            tvUserRanking.setText("-");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("PaginaPrincipal", "Error al obtener datos", e);
                        tvUserName.setText("Hola, Jugador");
                        tvUserPoints.setText("0");
                        tvUserRanking.setText("-");
                    });

            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .into(mainProfileImage);
            }
        } else {
            tvUserName.setText("Hola, Invitado");
            tvUserPoints.setText("0");
            tvUserRanking.setText("-");
        }

        btnStartGame.setOnClickListener(v -> {
            Log.d("PaginaPrincipal", "Iniciar juego");
        });

        btnRanking.setOnClickListener(v -> startActivity(new Intent(this, Ranking.class)));

        btnOptions.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void calcularYMostrarRanking(String uid) {
        db.collection("usuarios")
                .orderBy("puntos", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int posicion = 1;
                    boolean encontrado = false;

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        if (doc.getId().equals(uid)) {
                            tvUserRanking.setText(String.valueOf(posicion));
                            encontrado = true;
                            break;
                        }
                        posicion++;
                    }

                    if (!encontrado) {
                        tvUserRanking.setText("-");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PaginaPrincipal", "Error al calcular ranking", e);
                    tvUserRanking.setText("-");
                });
    }
}
