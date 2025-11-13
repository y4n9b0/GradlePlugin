FlatDeps
--------
Output all the dependencies of Android application or library.<br>
Requires Android Gradle Plugin (AGP) 7.x or higher.

Normally, running `./gradlew :${module}:dependencies` prints a full dependency tree.<br>
However, most of the time, we just want the final, flattened list of dependencies for each app variant.<br>
This plugin does exactly that — simple and clean.

How to use
--------
* Add the plugin to module’s build.gradle:
    ```groovy
    plugins {
        id 'io.github.y4n9b0.flatDeps' version '1.0.1'
    }
    ```

* Run the Gradle task to output dependencies into: ${module}/build/outputs/dependencies/flatDeps${variant}.txt
    ```bash
    ./gradlew clean :${module}:flatDepsDebug
    ./gradlew clean :${module}:flatDepsRelease
    ./gradlew clean :${module}:flatDeps
    ```

  Example output (sorted in ascending order):
    ```txt
    androidx.activity:activity:1.8.0
    androidx.annotation:annotation:1.9.1
    androidx.annotation:annotation-experimental:1.4.1
    androidx.annotation:annotation-jvm:1.9.1
    androidx.appcompat:appcompat:1.7.1
    androidx.appcompat:appcompat-resources:1.7.1
    androidx.arch.core:core-common:2.2.0
    androidx.arch.core:core-runtime:2.2.0
    androidx.cardview:cardview:1.0.0
    com.google.mlkit:barcode-scanning:17.3.0
	    ├─ jni/arm64-v8a/libbarhopper_v3.so
	    ├─ jni/armeabi-v7a/libbarhopper_v3.so
	    ├─ jni/x86/libbarhopper_v3.so
	    └─ jni/x86_64/libbarhopper_v3.so
    com.google.mlkit:barcode-scanning-common:17.0.0
    com.google.mlkit:common:18.11.0
    com.google.mlkit:vision-common:17.3.0
    com.google.mlkit:vision-interfaces:16.3.0
    ···
    ```

Development & Test
--------
* Modify the FlatDepsPlugin class as needed.
* Bump the plugin version in the plugin module’s build.gradle.
* Publish the plugin to your local Maven repository: 
  ```bash
  ./gradlew clean :flatDeps:publishToMavenLocal
  ```
* In your app module, update the plugin dependency to use the new local version and verify that it works as expected.
  Make sure to include `mavenLocal()` in plugin repositories:
  ```groovy
  pluginManagement {
    repositories {
        mavenLocal()
    }
  }
  ```
* After verifying the plugin works locally, commit your changes.

Publish
--------
* To validate a Plugin Portal publication (no upload):
  ```bash
  ./gradlew clean :flatDeps:publishPlugins --validate-only
  ```
* To publish to the Gradle Plugin Portal:
  ```bash
  ./gradlew clean :flatDeps:publishPlugins
  ```

Todo
--------
* ~~List .so files of each dependency (if present)~~

License
--------
Released under the WTFPL license. See the [LICENSE](https://github.com/y4n9b0/GradlePlugin/blob/master/LICENSE) file 
for details.