# irahKotUtils (Useful Android Kotlin Utilities)

irahKotUtils is a Kotlin library providing utility functions for Android development.

### Use the following to integrate irahKotUtils

Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

```groovy
dependencies {
    implementation 'com.github.devharikrishna:irahUtils:v1.0.0'
}
```

## Usage

#### Open Device Settings

```kotlin
irahKotUtils.openSettings(context)
```

#### Get App Version

```kotlin
val version = irahKotUtils.getAppVersion(context)
```

#### Setup Keyboard Handler

```kotlin
irahKotUtils.setupKeyboardHandler(activity, view)
```

#### Convert DP to Pixels

```kotlin
val pixels = irahKotUtils.dpToPx(16f)
```

#### Convert Pixels to DP

```kotlin
val dp = irahKotUtils.pxToDp(32f)
```

#### Open URL in Browser

```kotlin
irahKotUtils.openUrl(context, "https://example.com")
```

#### Share App

```kotlin
irahKotUtils.shareApp(context)
```

#### Get Application Name

```kotlin
val appName = irahKotUtils.getApplicationName(context)
```

#### Check Network Availability

```kotlin
if (irahKotUtils.isNetworkAvailable(context)) {
    // Network available
} else {
    irahKotUtils.showNetworkError(context)
}
```

#### Validate Phone Number

```kotlin
val isValid = irahKotUtils.isValidPhoneNumber("9876543210")
```

#### Validate Email

```kotlin
val isValid = irahKotUtils.isValidEmail("test@example.com")
```

#### Check if App is Installed

```kotlin
val isInstalled = irahKotUtils.isAppInstalled("com.android.chrome", context)
```

#### Get Time-Based Greeting

```kotlin
val greeting = irahKotUtils.greeting
```

#### Start View Blinking

```kotlin
irahKotUtils.startBlinking(view, 500L)
```

#### Get Current Date and Time

```kotlin
val dateTime = irahKotUtils.getCurrentDateTime()
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change. Please make sure to update tests as appropriate.