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
	        implementation 'com.github.HARIKRISHNAYAYAVAR:irahUtils:1.0.0'
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

#### Open Connection Setting

```java
  irahUtils.openSettings(this);
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
Please make sure to update tests as appropriate.