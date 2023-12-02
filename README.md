
# Android-Clean-Architecture

-  **[App](https://github.com/dautovicharis/Android-Clean-Architecture/tree/development/app)**: Android-Application
    - Access: everything
    - Features: [main](https://github.com/dautovicharis/Android-Clean-Architecture/tree/development/app/src/main/java/com/hd/clean_arch/ui/main), [permissions](https://github.com/dautovicharis/Android-Clean-Architecture/tree/development/app/src/main/java/com/hd/clean_arch/ui/permissions)
- **[Presentation](https://github.com/dautovicharis/Android-Clean-Architecture/tree/development/presentation)**: Android-Library
    - Access: domain
    - Tests: [UnitTests](https://github.com/dautovicharis/Android-Clean-Architecture/tree/development/presentation/src/test/java/com/hd/presentation)
    - Features: [main](https://github.com/dautovicharis/Android-Clean-Architecture/tree/development/presentation/src/main/java/com/hd/presentation/main), [permissions](https://github.com/dautovicharis/Android-Clean-Architecture/tree/development/presentation/src/main/java/com/hd/presentation/permissions)
- **[Domain](https://github.com/dautovicharis/Android-Clean-Architecture/tree/development/domain)**: Kotlin-Library
    - Access: nothing
    - UseCase: [PermissionsUseCase](https://github.com/dautovicharis/Android-Clean-Architecture/blob/development/domain/src/main/java/com/hd/domain/permissions/usecase/PermissionsUseCase.kt)
- **[Data](https://github.com/dautovicharis/Android-Clean-Architecture/tree/development/data)**: Android-Library
    - Access: domain
    - UseCase: [PermissionsUseCaseImpl](https://github.com/dautovicharis/Android-Clean-Architecture/blob/development/data/src/main/java/com/hd/data/permissions/PermissionsUseCaseImpl.kt)

**Supported permissions:** 

    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />  
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    AutoStart workaround for XIAOMI, OPPO, VIVO, LETV, HONOR and ONEPLUS.
