package banco;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface AuthApi {
    @POST("signup")
    Call<UserResponse> signUp(@Body SignUpBody body);

    @POST("token?grant_type=password")
    Call<TokenResponse> signIn(@Body SignInBody body);

    @POST("token?grant_type=refresh_token")
    Call<TokenResponse> refresh(@Body RefreshBody body);
}

class SignUpBody { public String email; public String password; public java.util.Map<String,Object> data;
    SignUpBody(String e,String p){email=e;password=p; data=new java.util.HashMap<>();}
}
class SignInBody { public String email; public String password; SignInBody(String e,String p){email=e;password=p;} }
class RefreshBody { public String refresh_token; RefreshBody(String t){refresh_token=t;} }

class TokenResponse {
    public String access_token;
    public String refresh_token;
    public User user;
}
class UserResponse { public User user; }
class User { public String id; public String email; }
