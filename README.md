# irahUtils (Useful Native Android Utils (Java))

irahUtils is a library consist of some useful Utility functions in Native Android.

### Use the ![jitpack.io](https://jitpack.io) to install irahUtils

Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

  Step 2. Add the dependency

  	dependencies {
	        implementation 'com.github.devharikrishna:irahUtils:v1.0.0'
	}

## Usage

#### Check Internet Connection

```java
        if(irahUtils.isConnected(this)){
            // Connection is Available
        }else{
            //network not available
            irahUtils.show_no_network_toast(this);
        }

```

#### Check if particular app is available in device with package name

```java
        irahUtils.isAppInstalled("package_name", this);

        eg : 
            irahUtils.isAppInstalled("com.android.chrome", this);
```

#### Check if a phone number is valid or not
```java
        irahUtils.isValidPhoneNumber("phone_number");

        eg : 
            irahUtils.isValidPhoneNumber("+919876543210");
```

#### Check if a mail is valid or not
```java
        irahUtils.isValidEmail("email");

        eg : 
            irahUtils.isValidEmail("abcd@gmail.com");
```


#### To make a view blink in a particular speed
```java
        irahUtils.blink({view to blink},500);

        eg : 
            irahUtils.blink(findViewById(R.id.view),500);
```

#### To get wish of current time like Good Morning, Good Afternoon, Good Evening and Good Night
```java

        irahUtils.getWish();

```



#### And many more

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
Please make sure to update tests as appropriate.
