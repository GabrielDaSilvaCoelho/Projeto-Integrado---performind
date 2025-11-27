package com.example.minhaparte;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class EnqueteAdapter extends RecyclerView.Adapter<EnqueteAdapter.ViewHolder> {

    private ArrayList<Enquete> enquetes;
    private Context context;

    public EnqueteAdapter(Context context, ArrayList<Enquete> enquetes) {
        this.context = context;
        this.enquetes = enquetes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_enquete, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Enquete e = enquetes.get(position);
        holder.tvTitulo.setText(e.getTitulo());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("enquete_id", e.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return enquetes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
        }
    }
}
