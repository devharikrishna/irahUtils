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
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class irahUtils {

    /**
     * Opens the device settings screen.
     */
    public static void openDeviceSettings(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * Retrieves the app's version name.
     */
    @SuppressWarnings("deprecation")
    public static String getAppVersionName(@NonNull Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.PackageInfoFlags.of(0));
            } else {
                packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            }
            return packageInfo.versionName != null ? packageInfo.versionName : "1.0.0";
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.0";
        }
    }

    /**
     * Hides the soft keyboard when touching non-EditText views.
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void hideKeyboardOnTouch(@NonNull Activity activity, @NonNull View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && activity.getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            });
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                hideKeyboardOnTouch(activity, viewGroup.getChildAt(i));
            }
        }
    }

    /**
     * Converts dp to pixels.
     */
    public static int dpToPixel(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Converts pixels to dp.
     */
    public static int pixelToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Encodes data to Base64 string.
     */
    private static String encodeBase64(@NonNull byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * Opens a URL in Chrome Custom Tabs or default browser.
     */
    public static void openUrl(@NonNull Context context, @NonNull String url) {
        try {
            Uri uri = Uri.parse(url);
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .setUrlBarHidingEnabled(true)
                    .setShareState(CustomTabsIntent.SHARE_STATE_OFF);
            CustomTabsIntent intent = builder.build();
            if (!(context instanceof Activity)) {
                intent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (isAppInstalled(context, "com.android.chrome")) {
                intent.intent.setPackage("com.android.chrome");
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Fallback to default browser for API 23+
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                if (!(context instanceof Activity)) {
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(browserIntent);
                return;
            }
            intent.launchUrl(context, uri);
        } catch (Exception e) {
            // Fallback for older APIs or if Custom Tabs fail
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (!(context instanceof Activity)) {
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            try {
                context.startActivity(browserIntent);
            } catch (Exception ignored) {
                // Handle case where no browser is available
            }
        }
    }

    /**
     * Shares the app's Play Store link.
     */
    public static void shareAppLink(@NonNull Context context) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_SUBJECT, getAppName(context))
                    .putExtra(Intent.EXTRA_TEXT, String.format(
                            "\nLet me recommend you %s application\n\nhttps://play.google.com/store/apps/details?id=%s\n\n",
                            getAppName(context), context.getPackageName()));
            Intent chooserIntent = Intent.createChooser(shareIntent, "Share Via");
            if (!(context instanceof Activity)) {
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(chooserIntent);
        } catch (Exception e) {
            // Silently handle to avoid crashes
        }
    }

    /**
     * Retrieves the application name.
     */
    public static String getAppName(@NonNull Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        return appInfo.labelRes == 0
                ? appInfo.nonLocalizedLabel != null ? appInfo.nonLocalizedLabel.toString() : "App"
                : context.getString(appInfo.labelRes);
    }

    /**
     * Checks if the device is connected to a network.
     */
    @SuppressWarnings("deprecation")
    public static boolean isNetworkConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return cm.getActiveNetwork() != null;
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && (networkInfo.isConnected() || networkInfo.isConnectedOrConnecting());
        }
    }

    /**
     * Returns a network connectivity error message.
     */
    public static String getNetworkErrorMessage() {
        return "Please connect to a network";
    }

    /**
     * Shows a toast indicating no network connection.
     */
    public static void showNoNetworkToast(@NonNull Context context) {
        Toast.makeText(context, getNetworkErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Validates a phone number (10 digits).
     */
    public static boolean isValidPhoneNumber(@NonNull String phoneNumber) {
        if (phoneNumber.trim().isEmpty() || phoneNumber.length() != 10) {
            return false;
        }
        return Pattern.matches("^\\d{10}$", phoneNumber);
    }

    /**
     * Validates an email address.
     */
    public static boolean isValidEmail(@NonNull String email) {
        return Pattern.matches("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}", email);
    }

    /**
     * Checks if an app is installed.
     */
    @SuppressWarnings("deprecation")
    public static boolean isAppInstalled(@NonNull Context context, @NonNull String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES));
            } else {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Returns a greeting based on the time of day.
     */
    public static String getTimeBasedGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) return "Good Morning";
        if (hour < 15) return "Good Afternoon";
        if (hour < 21) return "Good Evening";
        return "Good Night";
    }

    /**
     * Makes a view blink at specified intervals.
     */
    public static void startBlinking(@NonNull View view, int intervalMillis) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable blinkRunnable = new Runnable() {
            @Override
            public void run() {
                view.setVisibility(view.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                handler.postDelayed(this, intervalMillis);
            }
        };
        handler.post(blinkRunnable);
    }

    /**
     * Returns the current date and time in dd-MM-yyyy HH:mm:ss format.
     */
    public static String getCurrentDateTime() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}