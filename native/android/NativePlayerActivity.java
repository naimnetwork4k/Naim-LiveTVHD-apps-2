package com.naim.livetvhd;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.net.Uri;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.ui.PlayerView;

public class NativePlayerActivity extends Activity {
    public static final String EXTRA_URL = "naim_native_video_url";
    public static final String EXTRA_TITLE = "naim_native_video_title";
    public static final String EXTRA_HEADERS = "naim_native_video_headers";

    private ExoPlayer player;
    private PlayerView playerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        String url = getIntent().getStringExtra(EXTRA_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (url == null || url.trim().isEmpty()) {
            finish();
            return;
        }

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        playerView = new PlayerView(this);
        playerView.setUseController(true);
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING);
        playerView.setKeepScreenOn(true);
        playerView.setFocusable(true);
        playerView.setFocusableInTouchMode(true);
        root.addView(playerView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        TextView heading = new TextView(this);
        heading.setText(title == null ? "Naim LiveTVHD" : title);
        heading.setTextColor(Color.WHITE);
        heading.setTextSize(14);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        heading.setPadding(18, 8, 18, 8);
        heading.setBackgroundColor(0x66000000);
        FrameLayout.LayoutParams headingParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.START
        );
        root.addView(heading, headingParams);
        setContentView(root);

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("User-Agent", "NaimLiveTVHD/1.1 (Android)");
        try {
            String rawHeaders = getIntent().getStringExtra(EXTRA_HEADERS);
            if (rawHeaders != null) {
                JSONObject json = new JSONObject(rawHeaders);
                Iterator<String> keys = json.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = json.optString(key, "");
                    if (!value.isEmpty() && !key.equalsIgnoreCase("host") && !key.equalsIgnoreCase("content-length")) requestHeaders.put(key, value);
                }
            }
        } catch (Exception ignored) { }
        DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(requestHeaders.remove("User-Agent"))
                .setDefaultRequestProperties(requestHeaders)
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(20000)
                .setReadTimeoutMs(30000);

        player = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(this)
                        .setDataSourceFactory(httpFactory))
                .build();
        playerView.setPlayer(player);

        MediaItem.Builder mediaBuilder = new MediaItem.Builder().setUri(url);
        String path = Uri.parse(url).getPath();
        String lower = (path == null ? url : path).toLowerCase(java.util.Locale.ROOT);
        if (lower.endsWith(".m3u8")) {
            mediaBuilder.setMimeType(MimeTypes.APPLICATION_M3U8);
        } else if (lower.endsWith(".mpd")) {
            mediaBuilder.setMimeType(MimeTypes.APPLICATION_MPD);
        } else if (lower.endsWith(".ts") || lower.endsWith(".m2ts") || lower.endsWith(".mts")) {
            mediaBuilder.setMimeType(MimeTypes.VIDEO_MP2T);
        }

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                android.widget.Toast.makeText(
                        NativePlayerActivity.this,
                        "ভিডিও চালু হয়নি: " + error.getErrorCodeName(),
                        android.widget.Toast.LENGTH_LONG
                ).show();
            }
        });
        player.setMediaItem(mediaBuilder.build());
        player.prepare();
        player.play();
        playerView.requestFocus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) player.pause();
    }

    @Override
    protected void onDestroy() {
        if (playerView != null) playerView.setPlayer(null);
        if (player != null) {
            player.release();
            player = null;
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }
}
