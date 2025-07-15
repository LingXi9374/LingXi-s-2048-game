# Generic Proguard rules for Android applications.

# Keep all fundamental classes of the application so they are not removed.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# Keep all classes that are used as views in XML layouts.
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep all classes annotated with @Keep.
-keep @androidx.annotation.Keep class * {
    *;
}

# Keep all public methods of classes annotated with @Keep.
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

# Keep all fields of classes annotated with @Keep.
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

# Keep the constructors of all classes annotated with @Keep.
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# Jetpack Compose specific rules
-keepclassmembers class * { 
    @androidx.compose.runtime.Composable <methods>; 
}
-keep class androidx.compose.runtime.Composer

# Keep data classes (often used for models)
-keepclassmembers class * extends java.lang.Object {
    public <init>();
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Keep - Serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class **$$serializer { *; }

