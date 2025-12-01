package com.example.minhaparte.minhaparte.all;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minhaparte.R;


import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    public interface OnUsuarioClick {
        void onDeleteClick(UsuarioModel user);
    }

    private List<UsuarioModel> usuarios;
    private OnUsuarioClick listener;

    public UsuarioAdapter(List<UsuarioModel> usuarios, OnUsuarioClick listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsuarioModel user = usuarios.get(position);

        holder.nome.setText(user.nome);
        holder.matricula.setText("Matrícula: " + user.matricula);
        holder.tipo.setText("Tipo: " + user.tipo);

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(user));
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome, matricula, tipo;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.txtNomeUsuario);
            matricula = itemView.findViewById(R.id.txtMatriculaUsuario);
            tipo = itemView.findViewById(R.id.txtTipoUsuario);
            btnDelete = itemView.findViewById(R.id.btnDeleteUsuario);
        }
    }
}
