package com.example.minhaparte.minhaparte.all;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface UsuarioService {

    @GET("rest/v1/usuarios?select=*")
    Call<List<UsuarioModel>> getUsuarios(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth
    );

    @DELETE("rest/v1/usuarios")
    Call<Void> deleteUsuario(
            @Query("id") String idFilter,        // <--- AQUI ESTÁ O CERTO
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth
    );
}
