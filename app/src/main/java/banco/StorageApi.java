package banco;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

interface StorageApi {
    // upload binário direto
    @POST("object/{bucket}/{path}")
    @Headers({"Content-Type: application/octet-stream"})
    Call<ResponseBody> upload(@Path("bucket") String bucket,
                              @Path(value="path", encoded=true) String objectPath,
                              @Body RequestBody fileBytes);

    // URL assinada (download seguro)
    @POST("object/sign/{bucket}/{path}")
    Call<SignedUrlResponse> signUrl(@Path("bucket") String bucket,
                                    @Path(value="path", encoded=true) String objectPath,
                                    @Body SignBody body);
}
class SignBody { public int expiresIn; SignBody(int s){expiresIn=s;} }
class SignedUrlResponse { public String signedURL; }
