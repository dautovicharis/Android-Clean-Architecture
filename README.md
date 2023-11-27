# Android-Clean-Architecture

Modules:
- app: Android application 
    Access: Everything
    Type: Android-Application
- presentation: Presentation layer
    Access: domain
    Type: Android-Library
- domain: Domain layer
    Access: Nothing
    Type: Android-Library
- data: Data layer
    Access: domain
    Type: Android-Library