package ca.grasley.spaceshooter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.content.Context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AndroidLauncher extends AndroidApplication implements AndroidInterface {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		SettingsManager settingsManager = new AndroidSettingsManager(this);
		initialize(new SpaceShooterGame(this, settingsManager), config);
		// ✅ Se pasan ambos objetos
	}

	@Override
	public void savePoints(int newPoints) {
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

		if (user != null) {
			String uid = user.getUid();
			DocumentReference usuarioRef = db.collection("usuarios").document(uid);

			usuarioRef.get().addOnSuccessListener(documentSnapshot -> {
				if (documentSnapshot.exists()) {
					Long puntosActuales = documentSnapshot.getLong("puntos");
					if (puntosActuales == null || newPoints > puntosActuales) {
						usuarioRef.update("puntos", newPoints)
								.addOnSuccessListener(aVoid -> Gdx.app.log("Firebase", "Mejor puntuación actualizada correctamente"))
								.addOnFailureListener(e -> Gdx.app.log("Firebase", "Error al actualizar puntos: " + e.getMessage()));
					} else {
						Gdx.app.log("Firebase", "Puntuación no actualizada, ya existe una mejor o igual.");
					}
				} else {
					usuarioRef.set(new HashMap<String, Object>() {{
								put("puntos", newPoints);
							}}).addOnSuccessListener(aVoid -> Gdx.app.log("Firebase", "Usuario creado con puntos"))
							.addOnFailureListener(e -> Gdx.app.log("Firebase", "Error al crear usuario: " + e.getMessage()));
				}
			}).addOnFailureListener(e -> Gdx.app.log("Firebase", "Error al obtener documento: " + e.getMessage()));
		} else {
			Gdx.app.log("Firebase", "Usuario no autenticado, no se pueden guardar puntos.");
		}
	}




	@Override
	public void vibrate(int milliseconds) {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if (v != null && v.hasVibrator()) {
			v.vibrate(milliseconds);
		}
	}
	@Override
	public void goToMainPage() {
		runOnUiThread(() -> {
			Intent intent = new Intent(AndroidLauncher.this, PaginaPrincipal.class); // Cambia esto si tu main page tiene otro nombre
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish(); // Cierra la actividad actual (el juego)
		});
	}

	// Este método se puede llamar desde la clase InicioSesion cuando el usuario se autentique
	public void launchGameActivity() {
		Intent intent = new Intent(this, AndroidLauncher.class);
		startActivity(intent);
	}

}
