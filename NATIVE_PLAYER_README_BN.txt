Naim LiveTVHD - Native Android Player update

এই ZIP-এ আপনার মূল www/index.html এবং বিদ্যমান Capacitor workflow রাখা হয়েছে।
পরিবর্তন:
- Android APK-তে Media3/ExoPlayer Native fullscreen player যুক্ত করার source।
- HLS/M3U8, DASH/MPD, TS এবং ডিভাইস-সমর্থিত progressive video native player-এ পাঠানো হয়।
- Chrome/সাধারণ ওয়েব ব্রাউজারে আগের HTML player-ই থাকবে।
- Native player থেকে বের হতে Android Back চাপুন।

গুরুত্বপূর্ণ:
- GitHub Actions-এ নতুন Build চালিয়ে Artifact থেকে নতুন APK ইনস্টল করে পরীক্ষা করতে হবে।
- DRM/geo-block/token expired/Referer বা server authorization প্রয়োজন এমন স্ট্রিম এই player-এও ব্যর্থ হতে পারে।
- সব MKV/MOV/AC3/DTS/TrueHD ফাইল Android ডিভাইসের decoder support-এর ওপর নির্ভর করে।
- RTMP/FLV browser/native fallback এই পরিবর্তনে নিশ্চিতভাবে সমর্থিত নয়; প্রথম লক্ষ্য HLS/M3U8 ও DASH।


সংশোধন: Native player-এ Referer/Origin/User-Agent header পাঠানো হয়; native player ব্যর্থ হলে আগের WebView player fallback হিসেবে চলবে। TS URL-এর extension শনাক্তকরণও ঠিক করা হয়েছে।
