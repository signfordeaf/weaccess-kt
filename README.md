# WeAccess WePhoto
### WePhoto allows you to automatically depict photos so that photos can be read these images on the screen reader.
## 🛠️ Install
[![](https://jitpack.io/v/signfordeaf/wephoto-kt.svg)](https://jitpack.io/#signfordeaf/wephoto-kt)

 Step 1. Add the JitPack repository to your build file (settings.gradle)
```gradle
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
  Step 2. Add the dependency
```gradle
dependencies {
	        implementation 'com.github.signfordeaf:wephoto-kt:1.0.0'
	}
```

### Permission
Ensure that the following permission is present in your Android Manifest file, located in app/src/main/AndroidManifest.xml:
```
<uses-permission android:name="android.permission.INTERNET"/>
```

## 🧑🏻💻 Usage

###  📄Kotlin Class
   Call the getImageDescription function.
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WeAccessConfig.initialize("YOUR_API_KEY")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
         imageView.getImageDescription("image-url", DescriptionType.LONG)
    }
}
```
