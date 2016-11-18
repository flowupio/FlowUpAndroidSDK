-printmapping out.map
-renamesourcefileattribute SourceFile

# Preserve all annotations.

-keepattributes *Annotation*
-keepattributes EnclosingMethod

# Preserve FlowUp and FlowUp.Builder classes.

-keep public class io.flowup.FlowUp {
    public protected *;
}

-keep public class io.flowup.FlowUp$Builder {
    public protected *;
}

# Preserve FlowUp and FlowUp.Builder method names.

-keepclassmembernames class io.flowup.FlowUp {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

-keepclassmembernames class io.flowup.FlowUp$Builder {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Preserve FlowUp and FlowUp.Builder native method names and the names of their classes.

-keepclasseswithmembernames class io.flowup.FlowUp {
    native <methods>;
}

-keepclasseswithmembernames class io.flowup.FlowUp$Builder {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your library doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}