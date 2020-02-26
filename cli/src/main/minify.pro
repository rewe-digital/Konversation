-keep class org.rewedigital.konversation.Cli {
  public static void main(java.lang.String[]);
}

-keepattributes SourceFile, LineNumberTable, *Annotation*, Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, AnnotationDefault
-dontobfuscate

-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.**
-dontwarn okhttp3.internal.platform.*Platform
-dontwarn org.apache.commons.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn org.jetbrains.spek.engine.**
-dontwarn org.slf4j.**
-dontwarn sun.misc.**
-dontwarn kotlin.**
-dontwarn kotlinx.atomicfu.AtomicFU
-dontwarn kotlinx.coroutines.flow.**

-dontnote com.fasterxml.jackson.**
-dontnote com.google.**
-dontnote com.squareup.moshi.**
-dontnote com.sun.**
-dontnote org.apache.**
-dontnote okhttp3.**
-dontnote java.**
-dontnote javax.**
-dontnote kotlinx.**
-dontnote kotlin.**
-dontnote sun.**
-dontnote khttp.**

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class org.rewedigital.konversation.generator.alexa.Status {*;}

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

# apache http
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**

-keep class com.google.api.client.util.GenericData$Flags { *; }

# kotlin-io
-keepclassmembers class kotlinx.io.** {
    volatile <fields>;
}

-keepclassmembers class kotlinx.coroutines.io.** {
    volatile <fields>;
}

-keepclassmembernames class kotlinx.io.** {
    volatile <fields>;
}

-keepclassmembernames class kotlinx.coroutines.io.** {
    volatile <fields>;
}
