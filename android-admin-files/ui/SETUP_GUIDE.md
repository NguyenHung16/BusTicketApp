# 🚀 Hướng dẫn cấu hình Android Studio

## 📋 Các file đã chuẩn bị

```
android-admin-files/
├── ui/activity/
│   ├── LoginActivity.kt         ← Màn hình đăng nhập
│   ├── DashboardActivity.kt     ← Màn hình chính
│   └── TripsActivity.kt         ← Quản lý trips
├── layout/
│   ├── activity_login.xml
│   ├── activity_dashboard.xml
│   └── activity_trips.xml
├── network/
│   ├── RetrofitClient.kt
│   └── ApiService.kt
├── model/
│   ├── ApiResponse.kt
│   ├── AuthResponse.kt
│   ├── TripRequest.kt
│   ├── RouteRequest.kt
│   └── OperatorRequest.kt
└── utils/
    └── SessionManager.kt
```

---

## ✅ Bước 1: Tạo project Android mới

1. Mở Android Studio
2. File → New → New Project
3. Chọn `Empty Activity`
4. Điền thông tin:
   - Name: `BusAdminApp`
   - Package: `com.example.busadmin`
   - Language: `Kotlin`
   - Minimum SDK: `API 24` hoặc cao hơn
5. Finish

---

## ✅ Bước 2: Cấu hình `build.gradle` (Module: app)

Thay thế toàn bộ nội dung:

```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.example.busadmin'
    compileSdk 34

    defaultConfig {
        applicationId "com.example.busadmin"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding true
    }
}

dependencies {
    // Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.5.0'
    implementation 'androidx.fragment:fragment-ktx:1.6.0'

    // ViewModel & LiveData
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.2'

    // Retrofit & OkHttp
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'

    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'

    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

Sau đó: **Sync Now**

---

## ✅ Bước 3: Cấu hình `AndroidManifest.xml`

Thêm quyền Internet:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.example.busadmin">

    <!-- Quyền Internet -->
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.BusAdminApp"
        android:usesCleartextTraffic="true">

        <activity
            android:name=".ui.activity.LoginActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".ui.activity.DashboardActivity"
            android:exported="false" />

        <activity
            android:name=".ui.activity.TripsActivity"
            android:exported="false" />

        <activity
            android:name=".ui.activity.RoutesActivity"
            android:exported="false" />

        <activity
            android:name=".ui.activity.OperatorsActivity"
            android:exported="false" />
    </application>
</manifest>
```

---

## ✅ Bước 4: Copy file vào project

### 4.1 Copy file `network/`
- **RetrofitClient.kt** → `app/src/main/java/com/example/busadmin/network/`
- **ApiService.kt** → `app/src/main/java/com/example/busadmin/network/`

### 4.2 Copy file `model/`
- **ApiResponse.kt** → `app/src/main/java/com/example/busadmin/model/`
- **AuthResponse.kt** → `app/src/main/java/com/example/busadmin/model/`
- **TripRequest.kt** → `app/src/main/java/com/example/busadmin/model/`
- **RouteRequest.kt** → `app/src/main/java/com/example/busadmin/model/`
- **OperatorRequest.kt** → `app/src/main/java/com/example/busadmin/model/`

### 4.3 Copy file `utils/`
- **SessionManager.kt** → `app/src/main/java/com/example/busadmin/utils/`

### 4.4 Copy file `ui/activity/`
- **LoginActivity.kt** → `app/src/main/java/com/example/busadmin/ui/activity/`
- **DashboardActivity.kt** → `app/src/main/java/com/example/busadmin/ui/activity/`
- **TripsActivity.kt** → `app/src/main/java/com/example/busadmin/ui/activity/`

### 4.5 Copy layout XML
- **activity_login.xml** → `app/src/main/res/layout/`
- **activity_dashboard.xml** → `app/src/main/res/layout/`
- **activity_trips.xml** → `app/src/main/res/layout/`

---

## ✅ Bước 5: Cấu hình URL Backend

**File: `RetrofitClient.kt`**

```kotlin
// Nếu dùng Android Emulator:
private const val BASE_URL = "http://10.0.2.2:8080/"

// Nếu dùng thiết bị thật, thay bằng IP máy tính:
// private const val BASE_URL = "http://192.168.1.100:8080/"
```

---

## ✅ Bước 6: Chạy app

1. Chọn Emulator hoặc kết nối thiết bị
2. Run → Run 'app'
3. App sẽ mở LoginActivity

---

## 🧪 Test Login

### Tài khoản admin (nếu backend có dữ liệu):
```
Email: admin@example.com
Password: 123456
```

hoặc

```
Email: test@gmail.com
Password: 123456
```

---

## 📝 Lưu ý quan trọng

### 1️⃣ Backend chưa chạy?
- Bạn vẫn có thể code FE
- Khi cả FE & BE chạy, chỉ cần điều chỉnh URL

### 2️⃣ Cấu hình Network
- **Emulator**: `http://10.0.2.2:8080/`
- **Device**: Dùng IP máy tính, ví dụ `http://192.168.1.100:8080/`

### 3️⃣ Kiểm tra Android version
- Minimum SDK API 24 trở lên

### 4️⃣ Quyền Internet
- Đảm bảo `<uses-permission android:name="android.permission.INTERNET" />`

### 5️⃣ ViewBinding
- Tôi đã cấu hình `viewBinding true` để dễ thao tác UI

---

## 📑 File cần tạo thêm

Tạo 2 Activity trống để app không crash khi click button:

```kotlin
// RoutesActivity.kt
// OperatorsActivity.kt
```

(Chi tiết sau)

---

## ✅ Kết luận

Sau khi setup xong, bạn có:
- ✅ LoginActivity (đăng nhập)
- ✅ DashboardActivity (menu chính)
- ✅ TripsActivity (quản lý chuyến)
- ✅ Retrofit Client (gọi API)
- ✅ SessionManager (lưu token)

Bạn có thể bắt đầu test ngay!
