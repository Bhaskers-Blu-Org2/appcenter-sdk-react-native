// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.testapp;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.microsoft.appcenter.crashes.model.TestCrashException;
import com.microsoft.appcenter.reactnative.shared.AppCenterReactNativeShared;

import java.util.concurrent.atomic.AtomicInteger;

public class TestAppNativeModule extends ReactContextBaseJavaModule {

    static {
        System.loadLibrary("native-lib");
    }

    private static final String APP_SECRET = "app_secret";

    private static final String START_AUTOMATICALLY = "start_automatically";

    private final SharedPreferences mSharedPreferences;

    private native void nativeAllocateLargeBuffer();

    TestAppNativeModule(ReactApplicationContext context) {
        super(context);
        mSharedPreferences = context.getSharedPreferences(getName(), Context.MODE_PRIVATE);
        String secretOverride = mSharedPreferences.getString(APP_SECRET, null);
        AppCenterReactNativeShared.setAppSecret(secretOverride);
        boolean startAutomaticallyOverride = mSharedPreferences.getBoolean(START_AUTOMATICALLY, true);
        AppCenterReactNativeShared.setStartAutomatically(startAutomaticallyOverride);
        context.registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                Log.d("TestApp", "onTrimMemory ");
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {

            }

            @Override
            public void onLowMemory() {
                Log.d("TestApp", "onLowMemory ");
            }
        });
    }

    @Override
    public String getName() {
        return "TestAppNative";
    }

    @ReactMethod
    public void configureStartup(String secretString, boolean startAutomatically) {

        /* We need to use empty string in Android for no app secret like in JSON file. */
        if (secretString == null) {
            secretString = "";
        }
        mSharedPreferences.edit()
                .putString(APP_SECRET, secretString)
                .putBoolean(START_AUTOMATICALLY, startAutomatically)
                .apply();
    }

    @ReactMethod
    public void generateTestCrash() {

        /*
         * To crash the test app even in release.
         * We reach this code if Crashes.generateTestCrash detected release mode.
         * We can tell with stack trace whether we used SDK method in debug or this one in release.
         */
        throw new TestCrashException();
    }

    @ReactMethod
    public void produceLowMemoryWarning() {
        final AtomicInteger i = new AtomicInteger(0);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                nativeAllocateLargeBuffer();
                Log.d("TestApp", "Memory allocated: " + i.addAndGet(128) + "MB");
                handler.post(this);
            }
        });
    }
}
