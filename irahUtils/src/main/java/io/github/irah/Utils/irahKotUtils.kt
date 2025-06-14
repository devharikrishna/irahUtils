package io.github.irah.Utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

object irahKotUtils {
    private val emailPattern: Pattern by lazy { Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}") }
    private val phonePattern: Pattern by lazy { Pattern.compile("^\\d{10}$") }
    private val blinkScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val blinkJobs = mutableMapOf<View, Job>()

    fun openSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun getAppVersion(context: Context): String {
        return runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrDefault("1.0.0").toString()
    }

    @SuppressLint("ClickableViewAccessibility")
    @MainThread
    fun setupKeyboardHandler(activity: Activity, view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                activity.hideKeyboard()
                false
            }
        }
        if (view is ViewGroup) {
            view.children.forEach { child ->
                setupKeyboardHandler(activity, child)
            }
        }
    }

    private fun Activity.hideKeyboard() {
        currentFocus?.windowToken?.let { token ->
            getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(token, 0)
        }
    }

    fun dpToPx(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Float): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun openUrl(context: Context, url: String?) {
        if (url.isNullOrBlank()) return
        runCatching {
            val uri = url.toUri()
            if (uri.scheme == null) throw IllegalArgumentException("Invalid URL scheme")
            CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setUrlBarHidingEnabled(true)
                .build()
                .launchUrl(context, uri)
        }.onFailure {
            context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    fun shareApp(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getApplicationName(context))
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out ${getApplicationName(context)}:\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
            )
        }
        context.startActivity(
            Intent.createChooser(shareIntent, "Share via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    fun getApplicationName(context: Context): String {
        return context.applicationInfo.run {
            labelRes.takeIf { it != 0 }?.let(context::getString) ?: nonLocalizedLabel.toString()
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService<ConnectivityManager>() ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork?.let { network ->
                connectivityManager.getNetworkCapabilities(network)
                    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            } == true
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }

    fun showNetworkError(context: Context?) {
        context ?: return
        Toast.makeText(context, "Please connect to a network", Toast.LENGTH_SHORT).show()
    }

    fun isValidPhoneNumber(phoneNumber: String?): Boolean {
        return phoneNumber?.trim()?.takeIf { it.length > 6 }?.let {
            phonePattern.matcher(it).matches()
        } == true
    }

    fun isValidEmail(email: String?): Boolean {
        return email?.let { emailPattern.matcher(it).matches() } == true
    }

    fun isAppInstalled(packageName: String, context: Context): Boolean {
        return runCatching {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        }.getOrDefault(false)
    }

    val greeting: String
        get() = when (Calendar.getInstance()[Calendar.HOUR_OF_DAY]) {
            in 0..11 -> "Good Morning"
            in 12..14 -> "Good Afternoon"
            in 15..20 -> "Good Evening"
            else -> "Good Night"
        }

    @MainThread
    fun startBlinking(view: View, intervalMs: Long) {
        if (intervalMs <= 0) return
        stopBlinking(view)
        blinkJobs[view] = blinkScope.launch {
            while (true) {
                view.visibility = if (view.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
                delay(intervalMs)
            }
        }
    }

    @MainThread
    fun stopBlinking(view: View) {
        blinkJobs.remove(view)?.cancel()
        view.visibility = View.VISIBLE
    }

    fun cleanup() {
        blinkScope.cancel()
        blinkJobs.clear()
    }

    fun getCurrentDateTime(): String {
        return SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
    }
}