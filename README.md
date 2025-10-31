FlatDeps
--------
Output all the dependencies of Android application or library.<br>
Requires Android Gradle Plugin (AGP) 4.x or higher.

Normally, running `./gradlew :${module}:dependencies` prints a full dependency tree.<br>
However, most of the time, we just want the final, flattened list of dependencies for each app variant.<br>
This plugin does exactly that — simple and clean.

how to use
--------
* Add the plugin to your module’s build.gradle:
    ```groovy
    plugins {
        id 'io.github.y4n9b0.flatDeps' version "1.0.0"
    }
    ```

* Run the Gradle task to output dependencies into: ${module}/build/outputs/logs/flatDeps${variant}.txt
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
    ···
    ```

Local Development & Test
--------
* Apply the Gradle file in the flatDeps module:
  ```groovy
  apply from: "${rootDir}/gradle-plugin-mvn-publish.gradle"
  ```
* Modify the FlatDepsPlugin class as needed.
* Bump the plugin version in ${rootDir}/gradle-plugin-mvn-publish.gradle.
* Publish the plugin to your local Maven repository: 
  ```bash
  ./gradlew clean :flatDeps:publishToMavenLocal
  ```
* In your app module, update the plugin dependency to use the new local version and verify the changes.

Todo
--------
* List .so files of each dependency (if present)

License
--------
Released under the WTFPL license. See the [LICENSE](https://github.com/y4n9b0/GradlePlugin/blob/master/LICENSE) file 
for details.