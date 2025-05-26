package ca.grasley.spaceshooter;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class Ranking extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RankingAdapter rankingAdapter;
    private List<UsuarioRanking> userList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking); // usa tu XML con RecyclerView

        recyclerView = findViewById(R.id.recyclerViewRanking);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        rankingAdapter = new RankingAdapter(userList);
        recyclerView.setAdapter(rankingAdapter);

        db = FirebaseFirestore.getInstance();

        loadRankingData();
    }

    private void loadRankingData() {
        db.collection("users")
                .orderBy("puntuacion", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("RankingActivity", "Error al obtener datos", error);
                        return;
                    }

                    userList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        String userId = doc.getId();
                        String usuario = doc.getString("usuario");
                        Long puntosLong = doc.getLong("puntuacion");

                        if (usuario != null && puntosLong != null) {
                            int puntuacion = puntosLong.intValue();
                            userList.add(new UsuarioRanking(userId, usuario, puntuacion));
                        }
                    }
                    rankingAdapter.notifyDataSetChanged();
                });
    }
}
