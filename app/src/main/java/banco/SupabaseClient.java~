package banco;

import android.content.Context;
import android.content.SharedPreferences;

import com.seu.pacote.BuildConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public final class SupabaseClient {
    private static final String BASE_URL = BuildConfig.SUPABASE_URL;
    private static final String API_KEY  = BuildConfig.SUPABASE_ANON;

    private static volatile String accessToken;      // JWT do usuário logado
    private static volatile String refreshToken;     // para refresh
    private static SharedPreferences prefs;

    public static void init(Context ctx) {
        prefs = ctx.getSharedPreferences("supabase_prefs", Context.MODE_PRIVATE);
        accessToken  = prefs.getString("access", null);
        refreshToken = prefs.getString("refresh", null);
    }

    public static void setTokens(String access, String refresh) {
        accessToken = access;
        refreshToken = refresh;
        if (prefs != null) {
            prefs.edit().putString("access", access).putString("refresh", refresh).apply();
        }
    }

    private static OkHttpClient http(AuthApi authApi) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder b = original.newBuilder()
                            .addHeader("apikey", API_KEY)
                            .addHeader("Prefer", "return=representation");
                    if (accessToken != null && !accessToken.isEmpty()) {
                        b.addHeader("Authorization", "Bearer " + accessToken);
                    }
                    Response resp = chain.proceed(b.build());

                    // Refresh automático se 401 e tivermos refresh_token
                    if (resp.code() == 401 && refreshToken != null) {
                        resp.close();
                        try {
                            retrofit2.Response<TokenResponse> r = authApi.refresh(new RefreshBody(refreshToken)).execute();
                            if (r.isSuccessful() && r.body() != null) {
                                setTokens(r.body().access_token, r.body().refresh_token);
                                Request retry = original.newBuilder()
                                        .removeHeader("Authorization")
                                        .addHeader("Authorization", "Bearer " + accessToken)
                                        .addHeader("apikey", API_KEY)
                                        .addHeader("Prefer", "return=representation")
                                        .build();
                                return chain.proceed(retry);
                            }
                        } catch (IOException ignored) {}
                    }
                    return resp;
                })
                .build();
    }

    private static Retrofit retrofit(String base, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(base)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    public static AuthApi auth() {
        // Auth não precisa do interceptor de refresh
        return retrofit(BASE_URL + "/auth/v1/", new OkHttpClient()).create(AuthApi.class);
    }

    public static PostgrestApi db(AuthApi authApi) {
        return retrofit(BASE_URL + "/rest/v1/", http(authApi)).create(PostgrestApi.class);
    }

    public static StorageApi storage(AuthApi authApi) {
        return retrofit(BASE_URL + "/storage/v1/", http(authApi)).create(StorageApi.class);
    }
}
