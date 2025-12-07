package com.example.minhaparte.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minhaparte.Model.UsuarioModel;
import com.example.minhaparte.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DesempenhoColaboradorAdapter extends RecyclerView.Adapter<DesempenhoColaboradorAdapter.ViewHolder> {

    private final List<UsuarioModel> usuarios;
    private final Context context;

    private static final String SUPABASE_URL =
            "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBicGt4Ymt3ZnB6bmtrdXdjeGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEzMzAzMzYsImV4cCI6MjA3NjkwNjMzNn0.pg-ZC6GAXr0sXIDjetecT8QVL11ZSABhlunerXFwqSM";

    public DesempenhoColaboradorAdapter(Context context, List<UsuarioModel> usuarios) {
        this.context = context;
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public DesempenhoColaboradorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_desempenho_colaborador, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DesempenhoColaboradorAdapter.ViewHolder holder, int position) {
        UsuarioModel usuario = usuarios.get(position);

        holder.txtNome.setText(usuario.nome != null ? usuario.nome : "-");
        holder.txtMatricula.setText("Matrícula: " + (usuario.matricula != null ? usuario.matricula : "-"));
        holder.txtTipo.setText("Tipo: " + (usuario.tipo != null ? usuario.tipo : "-"));
        holder.txtCargo.setText("Cargo: " + (usuario.cargo != null ? usuario.cargo : "-"));
        holder.txtSetor.setText("Setor: " + (usuario.setor != null ? usuario.setor : "-"));
        holder.txtCpf.setText("CPF: " + (usuario.cpf != null ? usuario.cpf : "-"));
        holder.txtContato.setText("Contato: " + (usuario.contato != null ? usuario.contato : "-"));

        holder.txtScore.setText("Score: --/1000"); // valor inicial

        // Busca o score_final mais recente para esse usuário
        carregarScoreFinal(usuario.id, holder);
    }

    @Override
    public int getItemCount() {
        return usuarios != null ? usuarios.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNome, txtMatricula, txtTipo, txtCargo, txtSetor, txtCpf, txtContato, txtScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeUsuario);
            txtMatricula = itemView.findViewById(R.id.txtMatriculaUsuario);
            txtTipo = itemView.findViewById(R.id.txtTipoUsuario);
            txtCargo = itemView.findViewById(R.id.txtCargoUsuario);
            txtSetor = itemView.findViewById(R.id.txtSetorUsuario);
            txtCpf = itemView.findViewById(R.id.txtCpfUsuario);
            txtContato = itemView.findViewById(R.id.txtContatoUsuario);
            txtScore = itemView.findViewById(R.id.txtScoreUsuario);
        }
    }

    /**
     * Mesma lógica do PerfilUsuarioActivity.carregarScoreFinal,
     * mas agora para cada usuário da lista.
     */
    private void carregarScoreFinal(long usuarioId, ViewHolder holder) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String urlStr = SUPABASE_URL +
                        "/rest/v1/avaliacoes_desempenho?id_usuario=eq." + usuarioId +
                        "&select=score_final&order=created_at.desc&limit=1";

                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);

                int code = conn.getResponseCode();
                if (code / 100 != 2) {
                    return;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONArray arr = new JSONArray(sb.toString());
                if (arr.length() == 0) {
                    ((Activity) context).runOnUiThread(() ->
                            holder.txtScore.setText("Score: 0/1000")
                    );
                    return;
                }

                JSONObject obj = arr.getJSONObject(0);
                double scoreFinal = obj.optDouble("score_final", 0.0); // 0–1
                if (scoreFinal < 0) scoreFinal = 0;
                if (scoreFinal > 1) scoreFinal = 1;

                int scoreMil = (int) Math.round(scoreFinal * 1000.0);
                int finalScoreMil = scoreMil;

                ((Activity) context).runOnUiThread(() ->
                        holder.txtScore.setText("Score: " + finalScoreMil + "/1000")
                );

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
