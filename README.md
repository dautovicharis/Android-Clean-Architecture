
# Android-Clean-Architecture

-  **App**: Android-Application
    - Access: everything
- **Presentation**: Android-Library
    - Access: domain
- **Domain**: Kotlin-Library
    - Access: nothing
- **Data**: Android-Library
    - Access: domain

**Supported permissions:** 

    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />  
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    AutoStart workaround for XIAOMI, OPPO, VIVO, LETV, HONOR and ONEPLUS.
