package com.naim.livetvhd;

import android.content.Intent;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "NativeVideoPlayer")
public class NativeVideoPlayerPlugin extends Plugin {
    @PluginMethod
    public void openPlayer(PluginCall call) {
        String url = call.getString("url");
        String title = call.getString("title", "Naim LiveTVHD");
        JSObject headers = call.getObject("headers");
        if (url == null || url.trim().isEmpty()) {
            call.reject("ভিডিও লিংক পাওয়া যায়নি");
            return;
        }
        try {
            Intent intent = new Intent(getContext(), NativePlayerActivity.class);
            intent.putExtra(NativePlayerActivity.EXTRA_URL, url.trim());
            intent.putExtra(NativePlayerActivity.EXTRA_TITLE, title);
            if (headers != null) intent.putExtra(NativePlayerActivity.EXTRA_HEADERS, headers.toString());
            getActivity().runOnUiThread(() -> {
                try { getContext().startActivity(intent); }
                catch (Exception e) { call.reject("Native Player খোলা যায়নি: " + e.getMessage(), e); }
            });
            JSObject result = new JSObject();
            result.put("opened", true);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Native Player খোলা যায়নি: " + e.getMessage(), e);
        }
    }
}
