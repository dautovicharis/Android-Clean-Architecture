# Android-Clean-Architecture

-  App: Android application
    - Access: everything
    - Type: Android-Application
- Presentation: Presentation layer
    - Access: domain
    - Type: Android-Library
- domain: Domain layer
    - Access: nothing
    - Type: Kotlin-Library
- data: Data layer
    - Access: domain
    - Type: Android-Library
