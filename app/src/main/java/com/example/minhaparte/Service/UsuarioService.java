package com.example.minhaparte.Service;

import com.example.minhaparte.Model.UsuarioModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.PATCH;
import retrofit2.http.Headers;
import retrofit2.http.Body;
import retrofit2.http.Path;

public interface UsuarioService {

    @GET("rest/v1/usuarios?select=*")
    Call<List<UsuarioModel>> getUsuarios(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth
    );

    @DELETE("rest/v1/usuarios")
    Call<Void> deleteUsuario(
            @Query("id") String idFilter,     // você envia "eq.5" na Activity
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth
    );

    @PATCH("rest/v1/usuarios")
    Call<Void> atualizarSenha(
            @Query("id") String idFilter, // Ex: eq.5
            @Body Map<String, Object> body,
            @Header("apikey") String apikey,
            @Header("Authorization") String token
    );
}
