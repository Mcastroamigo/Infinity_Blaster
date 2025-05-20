package ca.grasley.spaceshooter;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;

public class PaginaPrincipal extends BaseActivity  {

    private Button btnStartGame, btnOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pagina_principal);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnStartGame = findViewById(R.id.btnStartGame);
        btnOptions = findViewById(R.id.btnOptions);

        btnStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(PaginaPrincipal.this, AndroidLauncher.class);
            startActivity(intent);
        });

        btnOptions.setOnClickListener(v -> {
            Intent intent = new Intent(PaginaPrincipal.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}
