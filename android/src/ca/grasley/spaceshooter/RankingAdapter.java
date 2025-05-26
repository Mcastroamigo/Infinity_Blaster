package ca.grasley.spaceshooter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<UsuarioRanking> userList;

    public RankingAdapter(List<UsuarioRanking> userList) {
        this.userList = userList;
    }

    @Override
    public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankingViewHolder holder, int position) {
        UsuarioRanking user = userList.get(position);

        holder.userName.setText(user.getUsuario());
        holder.userPoints.setText(String.valueOf(user.getPuntuacion()));

        switch (position) {
            case 0: holder.position.setText("🥇 1. "); break;
            case 1: holder.position.setText("🥈 2. "); break;
            case 2: holder.position.setText("🥉 3. "); break;
            default: holder.position.setText("#" + (position + 1) + ". "); break;
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        public TextView position, userName, userPoints;

        public RankingViewHolder(View itemView) {
            super(itemView);
            position = itemView.findViewById(R.id.position);
            userName = itemView.findViewById(R.id.userName);
            userPoints = itemView.findViewById(R.id.userPoints);
        }
    }
}
