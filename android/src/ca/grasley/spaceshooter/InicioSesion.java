package ca.grasley.spaceshooter;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class InicioSesion extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        // Inicia Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        SignInButton signInButton = findViewById(R.id.btnGoogle);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        // Configura el click del botón de Google Sign-In
        signInButton.setOnClickListener(v -> iniciarSesionConGoogle());
    }

    private void iniciarSesionConGoogle() {
        // Implementar la lógica de inicio de sesión con Google

        // Una vez que el inicio de sesión sea exitoso, lanzamos la actividad del juego
        if (mAuth.getCurrentUser() != null) {
            // Si el usuario ya está autenticado, lo llevamos a la pantalla principal del juego
            Intent intent = new Intent(InicioSesion.this, AndroidLauncher.class);
            startActivity(intent);
            finish();  // Para asegurarnos de que no se pueda volver a la pantalla de inicio de sesión
        }
    }
}
