package com.example.minhaparte;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView; // <-- usa PlayerView (não Styled)

public class PlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;

    // Métrica de tempo assistido
    private final Handler tick = new Handler(Looper.getMainLooper());
    private boolean ticking = false;
    private long watchedMs = 0L;
    private long lastTickMs = 0L;

    private final Runnable tickRunnable = new Runnable() {
        @Override public void run() {
            if (player != null && player.isPlaying()) {
                long now = System.currentTimeMillis();
                watchedMs += (now - lastTickMs);
                lastTickMs = now;
            }
            if (ticking) tick.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.player_view);

        // Inicializa o ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // TODO: troque pela sua URL (Supabase / MP4 / HLS)
        Uri uri = Uri.parse("https://storage.example.com/meu_video.mp4");
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
        player.setPlayWhenReady(false);

        // Tick do tempo assistido
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) startTick(); else stopTick();
            }
        });

        // Se quiser fullscreen, chame manualmente:
        // enterFullscreen();  // ou amarre em um botão seu
    }

    private void startTick() {
        if (!ticking) {
            ticking = true;
            lastTickMs = System.currentTimeMillis();
            tick.postDelayed(tickRunnable, 1000);
        }
    }

    private void stopTick() {
        ticking = false;
        tick.removeCallbacks(tickRunnable);
    }

    // Fullscreen helpers (opcional; chame quando quiser)
    private void enterFullscreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hideSystemBars();
    }
    private void exitFullscreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        showSystemBars();
    }

    @SuppressLint("InlinedApi")
    private void hideSystemBars() {
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
    private void showSystemBars() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override protected void onStart() { super.onStart(); if (player != null) player.play(); }
    @Override protected void onStop()  { super.onStop();  stopTick(); if (player != null) player.pause(); }
    @Override protected void onDestroy() {
        super.onDestroy();
        stopTick();
        if (playerView != null) playerView.setPlayer(null);
        if (player != null) { player.release(); player = null; }
    }

    private void enviarWatchedToBackend(long watchedMilliseconds, long pos, long dur) {
        // TODO: Retrofit/OkHttp -> Supabase (Edge Function/REST)
    }
}
