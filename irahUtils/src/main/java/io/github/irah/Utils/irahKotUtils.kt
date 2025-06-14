package io.github.irah.Utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import androidx.core.view.isVisible

object irahKotUtils {
    fun openSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_SETTINGS))
    }

    fun getAppVersion(context: Context): String {
        var pInfo: PackageInfo? = null
        try {
            pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return pInfo?.versionName ?: "1.0"
    }

    fun getAppVersionName(context: Context): String {
        return try {
            val pm = context.packageManager
            val packageInfo = pm.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun keyboardHandler(activity: Activity, view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v: View?, event: MotionEvent? ->
                try {
                    val inputMethodManager =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        activity.currentFocus?.windowToken,
                        0
                    )
                } catch (ignored: Exception) {
                }
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                keyboardHandler(activity, innerView)
            }
        }
    }

    fun convertDpToPixel(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun convertPixelToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun encodeBase64(dataToEncode: ByteArray): String {
        val dataEncoded = Base64.encode(dataToEncode, Base64.DEFAULT)
        return dataEncoded.contentToString()
    }

    fun gotoLink(link: String?, context: Context) {
        try {
            val pageUri = Uri.parse(link)
            val intentBuilder = CustomTabsIntent.Builder()
            intentBuilder.setShowTitle(true)
            intentBuilder.setUrlBarHidingEnabled(true)
            intentBuilder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)

            val customTabsIntent = intentBuilder.build()
            val packageName = "com.android.chrome"

            if (isAppInstalled(packageName, context)) {
                customTabsIntent.intent.setPackage(packageName)
            }else{
                customTabsIntent.launchUrl(context, pageUri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareAppPlayStoreLink(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getApplicationName(context))
            val shareMessage = "\nLet me recommend you ${getApplicationName(context)} application\n\n https://play.google.com/store/apps/details?id=${context.packageName}\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context.startActivity(Intent.createChooser(shareIntent, "Share Via"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
    }

    fun isConnected(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var activeNetworkInfo: NetworkInfo? = null
            activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null) {
                activeNetworkInfo.isConnected || activeNetworkInfo.isConnectedOrConnecting
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun network_message(): String {
        return "Please Connect to a Network"
    }

    fun show_no_network_toast(context: Context?) {
        Toast.makeText(context, "Please Connect to a Network", Toast.LENGTH_SHORT).show()
    }

    fun isValidPhoneNumber(phoneNumber: String?): Boolean {
        if (phoneNumber == null || phoneNumber.trim { it <= ' ' }.isEmpty()) {
            return false
        }
        if (phoneNumber.length <= 6) {
            return false
        }
        val phonePattern = "^\\d{10}$"
        val pattern = Pattern.compile(phonePattern)
        val matcher = pattern.matcher(phoneNumber)
        return matcher.matches()
    }

    fun isValidEmail(email: String?): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isAppInstalled(packageName: String?, context: Context): Boolean {
        val pm = context.packageManager
        try {
            pm.getPackageInfo(packageName!!, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    val wish: String
        get() {
            var wish = "Hi"
            val c = Calendar.getInstance()
            val timeOfDay = c[Calendar.HOUR_OF_DAY]
            if (timeOfDay >= 0 && timeOfDay < 12) {
                wish = "Good Morning"
            } else if (timeOfDay >= 12 && timeOfDay < 15) {
                wish = "Good Afternoon"
            } else if (timeOfDay >= 15 && timeOfDay < 21) {
                wish = "Good Evening"
            } else if (timeOfDay >= 21 && timeOfDay < 24) {
                wish = "Good Night"
            }
            return wish
        }

    fun blink(view: View, timeInMillis: Int) {
        val handler = Handler()
        Thread {
            try {
                Thread.sleep(timeInMillis.toLong())
            } catch (ignored: Exception) {
            }
            handler.post {
                if (view.isVisible) {
                    view.visibility = View.INVISIBLE
                } else {
                    view.visibility = View.VISIBLE
                }
                blink(view, timeInMillis)
            }
        }.start()
    }

    fun getCurrentDateTime(): String {
        return SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            .format(Date(System.currentTimeMillis()))
    }

}
