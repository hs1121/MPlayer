# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#-dontwarn kotlin.**
#-dontwarn kotlinx.atomicfu.** # https://github.com/Kotlin/kotlinx.coroutines/issues/1155
#-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
#-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
#-keepclassmembernames class kotlinx.** {  volatile <fields>;}
#-keepclassmembernames class kotlinx.** { public <methods>; }
#-keepclassmembernames class kotlinx.** { public <fields>; }
#-keepclassmembernames class kotlinx.** { public static <fields>; }
#-keepclassmembernames class kotlinx.** { public static <methods>; }

#-keep class kotlinx.** { *; }
#-keep class kotlin.** { *; }
#-keepnames class androidx.navigation.fragment.*
#-keep class layout.** { *; }
#-keep class com.** { *; }

#-keepnames class kotlin.ResultKt {}

#
#-keepclassmembernames class kotlin.** {  volatile <fields>;}
#-keepclassmembernames class kotlin.** { public <methods>; }
#-keepclassmembernames class kotlin.** { public <fields>; }
#-keepclassmembernames class kotlin.** { public static <fields>; }
#-keepclassmembernames class kotlin.** { public static <methods>; }

