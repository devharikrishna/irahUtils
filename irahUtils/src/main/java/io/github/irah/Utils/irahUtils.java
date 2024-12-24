package io.github.irah.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class irahUtils {


    public static void openSettings(Context context){
         context.startActivity(new Intent(Settings.ACTION_SETTINGS));
    }
    public static String getAppVersion(@NonNull Context context) {
        PackageInfo P_info = null;
        try {
            P_info = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(P_info).versionName;
    }
    public static String getAppVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.0";
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void keyboard_handler(Activity activity, View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                try{
                    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
                    }
                }catch (Exception ignored){ }
                return false;
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                keyboard_handler(activity,innerView);
            }
        }
    }

    public static int convertDpToPixel(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int convertPixelToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    private static String encodeBase64(byte[] dataToEncode) {
        byte[] dataEncoded = Base64.encode(dataToEncode, Base64.DEFAULT);
        return Arrays.toString(dataEncoded);
    }


    public static void gotoLink(String link, Context context){
        try {
            Uri page_uri= Uri.parse(link);
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
            intentBuilder.setShowTitle(true);
            intentBuilder.setUrlBarHidingEnabled(true);
            intentBuilder.setShareState(CustomTabsIntent.SHARE_STATE_OFF);
            if (isAppInstalled("com.android.chrome", context)){
                CustomTabsIntent intentBuilder_alt=intentBuilder.build();
                Intent intent=intentBuilder_alt.intent;
                intent.setData(page_uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setPackage("com.android.chrome");
                intentBuilder_alt.launchUrl(context, Objects.requireNonNull(intentBuilder_alt.intent.getData()));
            }else {
                intentBuilder.build().intent.setAction(Intent.ACTION_VIEW);
                intentBuilder.build().intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentBuilder.build().launchUrl(context, page_uri);
            }
        } catch(Exception e) { e.printStackTrace(); }
    }


    public static void shareAppPlayStoreLink(Context context){
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getApplicationName(context));
            String shareMessage = "\nLet me recommend you "+getApplicationName(context)+" application\n\n"
                    + "https://play.google.com/store/apps/details?id=" + context.getPackageName()+"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "Share Via"));
        } catch(Exception e) { e.printStackTrace(); }
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    return activeNetworkInfo.isConnected() || activeNetworkInfo.isConnectedOrConnecting();
                } else {
                    return false;
                }
            }else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }

    public static String network_message(){
        return "Please Connect to a Network";
    }
    public static void show_no_network_toast(Context context){
        Toast.makeText(context, "Please Connect to a Network", Toast.LENGTH_SHORT).show();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        if (phoneNumber.length() <= 6) {
            return false;
        }
        String phonePattern = "^\\d{10}$";
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
    public static boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isAppInstalled(String package_name, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }
        return false;
    }


    public static String getWish(){
        String wish="Hi";
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
            wish="Good Morning";
        }else if(timeOfDay >= 12 && timeOfDay < 15){
            wish="Good Afternoon";
        }else if(timeOfDay >= 15 && timeOfDay < 21){
            wish="Good Evening";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            wish="Good Night";
        }
        return wish;
    }



    public static void blink(View view,int timeInMillis){
        final Handler handler = new Handler();
        new Thread(() -> {
            try{Thread.sleep(timeInMillis);}catch (Exception ignored) {}
            handler.post(() -> {
                if(view.getVisibility() == View.VISIBLE){
                    view.setVisibility(View.INVISIBLE);
                }else{
                    view.setVisibility(View.VISIBLE);
                }
                blink(view,timeInMillis);
            });
        }).start();
    }



    public static String getCurrentDateTime(Context context) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));
    }

}
