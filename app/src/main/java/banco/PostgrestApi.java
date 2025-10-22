package banco;

import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

interface PostgrestApi {
    // VIDEOS
    @GET("videos")
    Call<List<Video>> listVideos(@Query("select") String select,
                                 @Query("owner_id") String ownerEq,
                                 @Query("visibility") String visibilityEq,
                                 @Query("order") String order); // ex: "created_at.desc"

    @POST("videos")
    Call<List<Video>> insertVideo(@Body Video video);

    @PATCH("videos")
    Call<List<Video>> updateVideo(@Query("id") String idEq, @Body Map<String,Object> patch);

    @DELETE("videos")
    Call<ResponseBody> deleteVideo(@Query("id") String idEq);

    // PROGRESS (UPSERT)
    @Headers({"Prefer: resolution=merge-duplicates"})
    @POST("video_progress")
    Call<List<VideoProgress>> upsertProgress(@Query("on_conflict") String onConflict,
                                             @Body VideoProgress body);

    // PERFIL (criar após signup)
    @POST("profiles")
    Call<List<Profile>> insertProfile(@Body Profile p);
}

class Video { public String id; public String owner_id; public String title; public String storage_path; public String visibility; public Integer duration_seconds; }
class VideoProgress { public String video_id; public String user_id; public double last_position_seconds; public int total_watched_seconds; }
class Profile { public String id; public String full_name; public String avatar_url; }
