package com.example.minhaparte;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class CriarVideoActivity extends AppCompatActivity {

    // Ajuste para seu projeto
    private static final String TAG = "CriarVideoActivity";
    private static final String SUPABASE_URL = "https://pbpkxbkwfpznkkuwcxjl.supabase.co";
    private static final String SUPABASE_API_KEY = "COLOQUE_SUA_ANON_KEY_AQUI";
    private static final String STORAGE_BUCKET = "videos";
    private static final long USUARIO_ID_PADRAO = 1L; // passe o id real quando tiver auth

    private EditText etTitulo, etDescricao;
    private TextView tvArquivo;
    private Button btnSelecionar, btnSalvar;
    private ProgressBar progress;

    private Uri videoUriSelecionado = null;
    private final OkHttpClient http = new OkHttpClient();

    // Seletor estável; não precisa persistir permissão
    private final ActivityResultLauncher<String> picker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    videoUriSelecionado = uri;
                    tvArquivo.setText(obterNomeArquivo(uri));
                    Toast.makeText(this, "Vídeo selecionado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Seleção cancelada", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_video);

        etTitulo = findViewById(R.id.etTitulo);
        etDescricao = findViewById(R.id.etDescricao);
        tvArquivo = findViewById(R.id.tvArquivo);
        btnSelecionar = findViewById(R.id.btnSelecionar);
        btnSalvar = findViewById(R.id.btnSalvar);
        progress = findViewById(R.id.progress);

        btnSelecionar.setOnClickListener(v -> {
            // GetContent concede permissão temporária de leitura enquanto esta Activity está ativa
            picker.launch("video/*");
        });

        btnSalvar.setOnClickListener(v -> {
            if (!validar()) return;
            bloquearUI(true);
            subirVideoEInserirRegistro();
        });
    }

    private boolean validar() {
        String titulo = etTitulo.getText().toString().trim();
        if (TextUtils.isEmpty(titulo)) {
            etTitulo.setError("Informe um título");
            etTitulo.requestFocus();
            return false;
        }
        if (videoUriSelecionado == null) {
            Toast.makeText(this, "Selecione um arquivo de vídeo", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void bloquearUI(boolean bloquear) {
        btnSelecionar.setEnabled(!bloquear);
        btnSalvar.setEnabled(!bloquear);
        progress.setVisibility(bloquear ? ProgressBar.VISIBLE : ProgressBar.GONE);
    }

    // -------- Upload + insert --------
    private void subirVideoEInserirRegistro() {
        String fileName = gerarNomeArquivo(videoUriSelecionado);
        MediaType mediaType = MediaType.parse(obterMimeType(videoUriSelecionado));
        if (mediaType == null) mediaType = MediaType.parse("video/mp4");

        // RequestBody que abre/fecha o stream internamente (evita "Stream closed")
        RequestBody fileBody = new ContentUriRequestBody(getContentResolver(), videoUriSelecionado, mediaType);

        String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + STORAGE_BUCKET + "/" + fileName;

        Request uploadReq = new Request.Builder()
                .url(uploadUrl)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("x-upsert", "true")
                .post(fileBody) // binário cru
                .build();

        http.newCall(uploadReq).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Upload falhou", e);
                runOnUiThread(() -> {
                    bloquearUI(false);
                    Toast.makeText(CriarVideoActivity.this, "Falha no upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Upload erro HTTP " + response.code() + " - " + body);
                    runOnUiThread(() -> {
                        bloquearUI(false);
                        Toast.makeText(CriarVideoActivity.this, "Erro upload (" + response.code() + "): " + body, Toast.LENGTH_LONG).show();
                    });
                    return;
                }
                Log.d(TAG, "Upload OK: " + fileName);
                inserirRegistroBD(fileName);
            }
        });
    }

    private void inserirRegistroBD(String arquivoNome) {
        String titulo = etTitulo.getText().toString().trim();
        String descricao = etDescricao.getText().toString().trim();

        String json = "{"
                + "\"usuario_id\":" + USUARIO_ID_PADRAO + ","
                + "\"titulo\":\"" + escapar(titulo) + "\","
                + "\"descricao\":\"" + escapar(descricao) + "\","
                + "\"arquivo_nome\":\"" + escapar(arquivoNome) + "\""
                + "}";

        String url = SUPABASE_URL + "/rest/v1/videos";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request req = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        http.newCall(req).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Insert falhou", e);
                runOnUiThread(() -> {
                    bloquearUI(false);
                    Toast.makeText(CriarVideoActivity.this, "Erro ao salvar no banco: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "Insert resp(" + response.code() + "): " + resp);
                runOnUiThread(() -> {
                    bloquearUI(false);
                    if (response.isSuccessful()) {
                        Toast.makeText(CriarVideoActivity.this, "✅ Vídeo cadastrado!", Toast.LENGTH_LONG).show();
                        finish(); // só finaliza aqui, quando conclui tudo
                    } else {
                        Toast.makeText(CriarVideoActivity.this, "❌ Banco (" + response.code() + "): " + resp, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    // -------- utilitários --------
    private String obterNomeArquivo(Uri uri) {
        String nome = null;
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c != null) {
            try {
                int nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0 && c.moveToFirst()) nome = c.getString(nameIndex);
            } finally { c.close(); }
        }
        return nome != null ? nome : "video";
    }

    private String gerarNomeArquivo(Uri uri) {
        String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(obterMimeType(uri));
        if (ext == null) ext = "mp4";
        return "vid_" + UUID.randomUUID() + "." + ext;
    }

    private String obterMimeType(Uri uri) {
        ContentResolver cr = getContentResolver();
        String type = cr.getType(uri);
        return type != null ? type : "video/mp4";
    }

    private static String escapar(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // RequestBody que abre o stream apenas dentro do writeTo (evita "Stream closed")
    static class ContentUriRequestBody extends RequestBody {
        private final ContentResolver contentResolver;
        private final Uri uri;
        private final MediaType mediaType;

        ContentUriRequestBody(ContentResolver cr, Uri uri, MediaType mediaType) {
            this.contentResolver = cr;
            this.uri = uri;
            this.mediaType = mediaType;
        }

        @Override public MediaType contentType() { return mediaType; }

        @Override public long contentLength() { return -1; } // streaming (sem calcular tamanho)

        @Override public void writeTo(BufferedSink sink) throws IOException {
            try (InputStream in = contentResolver.openInputStream(uri)) {
                if (in == null) throw new IOException("Não foi possível abrir o stream do arquivo");
                byte[] buf = new byte[8192];
                int r;
                while ((r = in.read(buf)) != -1) sink.write(buf, 0, r);
            }
        }
    }
}
