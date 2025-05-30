package ca.grasley.spaceshooter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InicioSesion extends BaseActivity {

    private static final String TAG = "InicioSesion";
    private static final int RC_SIGN_IN = 9001;

    private EditText etGmail, etPassword;
    private Button btnLogin;
    private SignInButton btnGoogle;
    private TextView tvRegister;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);
        MusicManager.start(this, R.raw.background_music);

        mAuth = FirebaseAuth.getInstance();

        etGmail = findViewById(R.id.etGmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvRegister = findViewById(R.id.tvRegister);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setSize(SignInButton.SIZE_WIDE);
        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        btnLogin.setOnClickListener(v -> {
            String email = etGmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isEmailValid(email)) {
                Toast.makeText(this, "Correo no válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String username = extractUsernameFromEmail(currentUser.getEmail());
                                crearUsuarioEnFirestore(currentUser, username);
                                saveUsernameToPrefs(username);
                                goToMain(username);
                            }
                        } else {
                            Toast.makeText(this, "Credenciales incorrectas.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesion.this, Registro.class);
            startActivity(intent);
        });
    }

    private void signInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String username = extractUsernameFromEmail(currentUser.getEmail());
                            crearUsuarioEnFirestore(currentUser, username);
                            saveUsernameToPrefs(username);
                            goToMain(username);
                        }
                    } else {
                        Toast.makeText(this, "Autenticación fallida.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void crearUsuarioEnFirestore(FirebaseUser user, String username) {
        if (user == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = user.getUid();

        db.collection("usuarios").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("nombre", username);  // Aquí uso el username extraído
                userData.put("puntos", 0L);
                userData.put("ranking", 0L);

                db.collection("usuarios").document(uid).set(userData)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Usuario creado correctamente"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Error al crear usuario", e));
            }
        });
    }

    private void goToMain(String username) {
        Intent intent = new Intent(InicioSesion.this, PaginaPrincipal.class);
        intent.putExtra("username", username); // Si quieres usarlo en la siguiente actividad
        startActivity(intent);
        finish();
    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile("^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Método para extraer el nombre de usuario del email
    private String extractUsernameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return "Jugador"; // fallback si no se puede extraer
    }

    // Guardar username en SharedPreferences para acceso posterior
    private void saveUsernameToPrefs(String username) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.apply();
    }
}
