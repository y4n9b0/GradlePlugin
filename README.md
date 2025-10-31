[![License](https://img.shields.io/badge/License-WTFPL-blue.svg)](https://github.com/y4n9b0/ttleosis/blob/master/LICENSE)
[![API](https://img.shields.io/badge/API-21%2B-blue.svg)](https://developer.android.com/about/versions/android-5.0.html)
[![JitPack](https://jitpack.io/v/y4n9b0/ttleosis.svg)](https://jitpack.io/#y4n9b0/ttleosis)

TtleOsis
--------
TransactionTooLargeException onSaveInstanceState since Android 12

* [Root launcher activities are no longer finished on Back press](https://developer.android.google.cn/about/versions/12/behavior-changes-all#back-press)
* [使用火山引擎 APMPlus 解决抖音Top 1 Java 崩溃的通用优化方案](https://juejin.cn/post/7306388118914973734?searchId=20240319120438D5D8E326DA58837413A9)

Requirements
--------
TtleOsis works on Android 5.0+ (API level 21+) and Java 8+.

Releases
--------
```groovy
implementation 'com.github.y4n9b0:ttleosis:1.0.4'
```

The latest release is available on [JitPack](https://jitpack.io/#y4n9b0/ttleosis),
don't forget add JitPack to maven repository.
```groovy
maven { url 'https://jitpack.io' }
```

Usage
--------
TtleOsis will take care of transaction data automatically, nothing else need to do.
But you really should find out why transaction data is so large.
Monitor logcat output to see transaction info of outState:
```shell
adb logcat -s ttleosis
```

Example logcat output:
```text
D ttleosis: onActivityPostSaveInstanceState activity=y4n9b0.ttleosis.app.TtleosisReproduceActivity@4ca12a
D ttleosis: Bundle Bytes=6388
D ttleosis: ├─ Bundle@android:viewHierarchyState Bytes=984
D ttleosis: │  └─ SparseArray[7]@android:views 
D ttleosis: │     ├─ android.view.AbsSavedState$1@16908290 Bytes=4
D ttleosis: │     ├─ androidx.appcompat.widget.Toolbar$SavedState@2131230773 Bytes=76
D ttleosis: │     ├─ android.view.AbsSavedState$1@2131230775 Bytes=4
D ttleosis: │     ├─ android.view.AbsSavedState$1@2131230781 Bytes=4
D ttleosis: │     ├─ android.view.AbsSavedState$1@2131230863 Bytes=4
D ttleosis: │     ├─ com.google.android.material.button.MaterialButton$SavedState@2131231040 Bytes=72
D ttleosis: │     └─ com.google.android.material.button.MaterialButton$SavedState@2131231045 Bytes=72
D ttleosis: ├─ Bundle@androidx.lifecycle.BundlableSavedStateRegistry.key Bytes=4776
D ttleosis: │  ├─ Bundle@androidx:appcompat Bytes=4
D ttleosis: │  ├─ Bundle@androidx.lifecycle.internal.SavedStateHandlesProvider Bytes=4
D ttleosis: │  ├─ Bundle@android:support:activity-result Bytes=1676
D ttleosis: │  │  ├─ ArrayList[9]@KEY_COMPONENT_ACTIVITY_REGISTERED_KEYS
D ttleosis: │  │  │  ├─ String=FragmentManager:StartIntentSenderForResult 
D ttleosis: │  │  │  ├─ String=FragmentManager:StartActivityForResult 
D ttleosis: │  │  │  ├─ String=FragmentManager:a0712bbc-9939-4523-b296-14d0377edb1e:StartIntentSenderForResult 
D ttleosis: │  │  │  ├─ String=FragmentManager:1170ad3d-b798-426d-a958-f10debb3c4b6:StartActivityForResult 
D ttleosis: │  │  │  ├─ String=FragmentManager:a0712bbc-9939-4523-b296-14d0377edb1e:RequestPermissions 
D ttleosis: │  │  │  ├─ String=FragmentManager:1170ad3d-b798-426d-a958-f10debb3c4b6:RequestPermissions 
D ttleosis: │  │  │  ├─ String=FragmentManager:RequestPermissions 
D ttleosis: │  │  │  ├─ String=FragmentManager:1170ad3d-b798-426d-a958-f10debb3c4b6:StartIntentSenderForResult 
D ttleosis: │  │  │  └─ String=FragmentManager:a0712bbc-9939-4523-b296-14d0377edb1e:StartActivityForResult 
D ttleosis: │  │  ├─ ArrayList[9]@KEY_COMPONENT_ACTIVITY_REGISTERED_RCS
D ttleosis: │  │  │  ├─ Integer=1942165898 
D ttleosis: │  │  │  ├─ Integer=356918738 
D ttleosis: │  │  │  ├─ Integer=182926120 
D ttleosis: │  │  │  ├─ Integer=66967503 
D ttleosis: │  │  │  ├─ Integer=1726710991 
D ttleosis: │  │  │  ├─ Integer=14827718 
D ttleosis: │  │  │  ├─ Integer=1393861623 
D ttleosis: │  │  │  ├─ Integer=1236973480 
D ttleosis: │  │  │  └─ Integer=413874826 
D ttleosis: │  │  ├─ Bundle@KEY_COMPONENT_ACTIVITY_PENDING_RESULT Bytes=4
D ttleosis: │  │  └─ ArrayList[0]@KEY_COMPONENT_ACTIVITY_LAUNCHED_KEYS
D ttleosis: │  └─ Bundle@android:support:fragments Bytes=2784
D ttleosis: │     └─ FragmentManagerState@android:support:fragments
D ttleosis: │        ├─ FragmentState@com.ly.genjidialog.GenjiDialog(a0712bbc-9939-4523-b296-14d0377edb1e) Bytes=1208
D ttleosis: │        └─ FragmentState@com.ly.genjidialog.GenjiDialog(1170ad3d-b798-426d-a958-f10debb3c4b6) Bytes=1208
D ttleosis: ├─ Integer@android:lastAutofillId 
D ttleosis: └─ FragmentManagerState@android:fragments Bytes=260
```

License
--------
Released under the WTFPL license. Please see the [LICENSE](https://github.com/y4n9b0/ttleosis/blob/master/LICENSE) file for more information.