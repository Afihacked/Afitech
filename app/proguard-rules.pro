############################################
# General
############################################

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

############################################
# Kotlin
############################################

-keep class kotlin.Metadata { *; }

############################################
# ViewBinding
############################################

-keep class **.databinding.*Binding {
    *;
}

############################################
# Room Database
############################################

-keep class androidx.room.RoomDatabase { *; }

-keep @androidx.room.Entity class * {
    *;
}

-keep @androidx.room.Dao class * {
    *;
}

-keep class * extends androidx.room.RoomDatabase {
    *;
}

-dontwarn androidx.room.**

############################################
# Glide
############################################

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule

-dontwarn com.bumptech.glide.**

############################################
# Coroutines
############################################

-dontwarn kotlinx.coroutines.**

############################################
# Navigation Component
############################################

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
}

############################################
# Material
############################################

-dontwarn com.google.android.material.**

############################################
# AndroidX
############################################

-dontwarn androidx.**

############################################
# Remove logs in release
############################################

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

############################################
# Optimize
############################################

-optimizationpasses 5

############################################
# Keep Application
############################################

-keep class com.afitech.** {
    *;
}