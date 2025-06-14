package io.github.irah.Utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Initialize views
        statusTextView = findViewById(R.id.status_text)
        emailInput = findViewById(R.id.email_input)
        phoneInput = findViewById(R.id.phone_input)

        // Setup keyboard handler
        irahKotUtils.setupKeyboardHandler(this, findViewById(R.id.root_layout))

        // Button listeners
        findViewById<Button>(R.id.btn_open_settings).setOnClickListener {
            irahKotUtils.openSettings(this)
        }

        findViewById<Button>(R.id.btn_get_app_version).setOnClickListener {
            statusTextView.text = "App Version: ${irahKotUtils.getAppVersion(this)}"
        }

        findViewById<Button>(R.id.btn_check_network).setOnClickListener {
            if (irahKotUtils.isNetworkAvailable(this)) {
                statusTextView.text = "Network Available"
            } else {
                irahKotUtils.showNetworkError(this)
                statusTextView.text = "Network Not Available"
            }
        }

        findViewById<Button>(R.id.btn_validate_email).setOnClickListener {
            val email = emailInput.text.toString()
            val isValid = irahKotUtils.isValidEmail(email)
            statusTextView.text = "Email Valid: $isValid"
        }

        findViewById<Button>(R.id.btn_validate_phone).setOnClickListener {
            val phone = phoneInput.text.toString()
            val isValid = irahKotUtils.isValidPhoneNumber(phone)
            statusTextView.text = "Phone Valid: $isValid"
        }

        findViewById<Button>(R.id.btn_check_app_installed).setOnClickListener {
            val isInstalled = irahKotUtils.isAppInstalled("com.android.chrome", this)
            statusTextView.text = "Chrome Installed: $isInstalled"
        }

        findViewById<Button>(R.id.btn_open_url).setOnClickListener {
            irahKotUtils.openUrl(this, "https://google.com")
        }

        findViewById<Button>(R.id.btn_share_app).setOnClickListener {
            irahKotUtils.shareApp(this)
        }

        findViewById<Button>(R.id.btn_get_app_name).setOnClickListener {
            statusTextView.text = "App Name: ${irahKotUtils.getApplicationName(this)}"
        }

        findViewById<Button>(R.id.btn_get_greeting).setOnClickListener {
            statusTextView.text = irahKotUtils.greeting
        }

        findViewById<Button>(R.id.btn_start_blink).setOnClickListener {
            irahKotUtils.startBlinking(statusTextView, 500L)
        }

        findViewById<Button>(R.id.btn_stop_blink).setOnClickListener {
            irahKotUtils.stopBlinking(statusTextView)
        }

        findViewById<Button>(R.id.btn_get_datetime).setOnClickListener {
            statusTextView.text = "DateTime: ${irahKotUtils.getCurrentDateTime()}"
        }

        findViewById<Button>(R.id.btn_dp_to_px).setOnClickListener {
            val dp = 16f
            statusTextView.text = "$dp dp = ${irahKotUtils.dpToPx(dp)} px"
        }

        findViewById<Button>(R.id.btn_px_to_dp).setOnClickListener {
            val px = 32f
            statusTextView.text = "$px px = ${irahKotUtils.pxToDp(px)} dp"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        irahKotUtils.cleanup()
    }
}