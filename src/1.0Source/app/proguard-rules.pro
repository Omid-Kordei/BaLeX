-optimizationpasses 5
-dontpreverify
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
    public static *** wtf(...);
}

-assumenosideeffects class android.widget.Toast {
    public static *** makeText(...);
    public static *** show(...);
}

-assumenosideeffects class android.os.Build {
    public static *** DEBUG;
    public static *** IS_DEBUGGABLE;
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keepattributes Signature, InnerClasses, *Annotation*, EnclosingMethod
-keepattributes SourceFile, LineNumberTable

-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**

-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.Database

-keep class ir.BaleX.models.** { *; }
-keepclassmembers class ir.BaleX.models.** {
    <init>(...);
    <fields>;
}

-keep class ir.BaleX.data.** { *; }
-keepclassmembers class ir.BaleX.data.** {
    <init>(...);
    <fields>;
}

-keep class ir.BaleX.utils.** { *; }
-keepclassmembers class ir.BaleX.utils.** {
    <init>(...);
    <fields>;
}

-keep class ir.BaleX.api.** { *; }
-keepclassmembers class ir.BaleX.api.** {
    <init>(...);
    <fields>;
}

-keep interface ir.BaleX.** {
    public static final *** *;
    public *** *();
}

-keep class ir.BaleX.** {
    public <init>(...);
    public protected private *;
}

-keepclassmembers class ir.BaleX.** {
    public <init>(...);
    public protected private *;
}

-dontnote
-dontwarn
-ignorewarnings

-repackageclasses ''
-allowaccessmodification
-mergeinterfacesaggressively