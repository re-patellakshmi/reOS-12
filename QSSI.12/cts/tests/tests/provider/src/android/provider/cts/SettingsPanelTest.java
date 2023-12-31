/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.provider.cts;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import androidx.test.InstrumentationRegistry;
import androidx.test.filters.MediumTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests related SettingsPanels:
 *
 * atest SettingsPanelTest
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class SettingsPanelTest {

    private static final int TIMEOUT = 8000;

    private static final String RESOURCE_DONE = "done";
    private static final String RESOURCE_INTERNET_DIALOG_DONE = "done_layout";
    private static final String RESOURCE_SEE_MORE = "see_more";
    private static final String RESOURCE_TITLE = "panel_title";
    private static final String SYSTEMUI_PACKAGE_NAME = "com.android.systemui";

    private String mSettingsPackage;
    private String mLauncherPackage;

    private Context mContext;
    private boolean mHasTouchScreen;
    private boolean mHasBluetooth;

    private UiDevice mDevice;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        final PackageManager packageManager = mContext.getPackageManager();

        mHasTouchScreen = packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)
                || packageManager.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH);
        mHasBluetooth = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);

        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        mLauncherPackage = packageManager.resolveActivity(launcherIntent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;

        Intent settingsIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        mSettingsPackage = packageManager.resolveActivity(settingsIntent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;

        assumeFalse("Skipping test: Auto does not support provider android.settings.panel", isCar());
        assumeFalse(
            "Skipping test: Watch does not support provider android.settings.panel", isWatch());
    }

    @After
    public void cleanUp() {
        mDevice.pressHome();
        mDevice.wait(Until.hasObject(By.pkg(mLauncherPackage).depth(0)), TIMEOUT);
    }

    // Check correct package is opened

    @Test
    public void internetDialog_correctPackage() {
        launchInternetDialog();

        String currentPackage = mDevice.getCurrentPackageName();

        assertThat(currentPackage).isEqualTo(SYSTEMUI_PACKAGE_NAME);
    }

    @Test
    public void volumePanel_correctPackage() {
        assumeTrue(mHasTouchScreen);
        launchVolumePanel();

        String currentPackage = mDevice.getCurrentPackageName();

        assertThat(currentPackage).isEqualTo(mSettingsPackage);
    }

    @Test
    public void nfcPanel_correctPackage() {
        launchNfcPanel();

        String currentPackage = mDevice.getCurrentPackageName();

        assertThat(currentPackage).isEqualTo(mSettingsPackage);
    }

    @Test
    public void wifiPanel_correctPackage() {
        launchWifiPanel();

        String currentPackage = mDevice.getCurrentPackageName();

        assertThat(currentPackage).isEqualTo(mSettingsPackage);
    }

    @Test
    public void internetDialog_doneClosesDialog() {
        // Launch panel
        launchInternetDialog();
        String currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isEqualTo(SYSTEMUI_PACKAGE_NAME);

        // Click the done button
        if (mHasTouchScreen) {
            mDevice.findObject(
                    By.res(SYSTEMUI_PACKAGE_NAME, RESOURCE_INTERNET_DIALOG_DONE)).click();
            mDevice.wait(Until.hasObject(By.pkg(mLauncherPackage).depth(0)), TIMEOUT);
        } else {
            mDevice.pressBack();
        }

        // Assert that we have left the panel
        currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isNotEqualTo(SYSTEMUI_PACKAGE_NAME);
    }

    @Test
    public void volumePanel_doneClosesPanel() {
        assumeTrue(mHasTouchScreen);
        // Launch panel
        launchVolumePanel();
        String currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isEqualTo(mSettingsPackage);

        // Click the done button
        pressDone();

        // Assert that we have left the panel
        currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isNotEqualTo(mSettingsPackage);
    }

    @Test
    public void nfcPanel_doneClosesPanel() {
        // Launch panel
        launchNfcPanel();
        String currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isEqualTo(mSettingsPackage);

        // Click the done button
        pressDone();

        // Assert that we have left the panel
        currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isNotEqualTo(mSettingsPackage);
    }

    @Test
    public void wifiPanel_doneClosesPanel() {
        // Launch panel
        launchWifiPanel();
        String currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isEqualTo(mSettingsPackage);

        // Click the done button
        pressDone();

        // Assert that we have left the panel
        currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isNotEqualTo(mSettingsPackage);
    }

    @Test
    public void volumePanel_seeMoreButton_launchesIntoSettings() {
        assumeTrue(mHasTouchScreen);
        // Launch panel
        launchVolumePanel();
        String currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isEqualTo(mSettingsPackage);

        // Click the see more button
        pressSeeMore();

        // Assert that we're still in Settings, on a different page.
        currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isEqualTo(mSettingsPackage);
        UiObject2 titleView = mDevice.findObject(By.res(mSettingsPackage, RESOURCE_TITLE));
        assertThat(titleView).isNull();
    }

    @Test
    public void nfcPanel_seeMoreButton_launchesIntoSettings() {
        // Launch panel
        launchNfcPanel();
        String currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isEqualTo(mSettingsPackage);

        // Click the see more button
        assumeTrue(mHasTouchScreen);
        pressSeeMore();

        // Assert that we're still in Settings, on a different page.
        currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isEqualTo(mSettingsPackage);
        UiObject2 titleView = mDevice.findObject(By.res(mSettingsPackage, RESOURCE_TITLE));
        assertThat(titleView).isNull();
    }

    @Test
    public void wifiPanel_seeMoreButton_launchesIntoSettings() {
        // Launch panel
        launchWifiPanel();
        String currentPackage = mDevice.getCurrentPackageName();
        assertThat(currentPackage).isEqualTo(mSettingsPackage);

        // Click the see more button
        assumeTrue(mHasTouchScreen);
        pressSeeMore();

        UiObject2 titleView = mDevice.findObject(By.res(mSettingsPackage, RESOURCE_TITLE));
        assertThat(titleView).isNull();
    }

    private void launchVolumePanel() {
        launchPanel(Settings.Panel.ACTION_VOLUME);
    }

    private void launchInternetDialog() {
        // Start from the home screen
        mDevice.pressHome();
        mDevice.wait(Until.hasObject(By.pkg(mLauncherPackage).depth(0)), TIMEOUT);

        Intent intent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                .setPackage(SYSTEMUI_PACKAGE_NAME);

        mContext.sendBroadcast(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(SYSTEMUI_PACKAGE_NAME).depth(0)), TIMEOUT);
    }

    private void launchNfcPanel() {
        assumeTrue(mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC));
        launchPanel(Settings.Panel.ACTION_NFC);
    }

    private void launchWifiPanel() {
        assumeTrue(mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI));
        launchPanel(Settings.Panel.ACTION_WIFI);
    }

    private void launchPanel(String action) {
        // Start from the home screen
        mDevice.pressHome();
        mDevice.wait(Until.hasObject(By.pkg(mLauncherPackage).depth(0)), TIMEOUT);

        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        mContext.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(mSettingsPackage).depth(0)), TIMEOUT);
    }

    private void pressDone() {
        if (mHasTouchScreen) {
            mDevice.findObject(By.res(mSettingsPackage, RESOURCE_DONE)).click();
            mDevice.wait(Until.hasObject(By.pkg(mLauncherPackage).depth(0)), TIMEOUT);
        } else {
            mDevice.pressBack();
        }
    }

    private void pressSeeMore() {
        mDevice.findObject(By.res(mSettingsPackage, RESOURCE_SEE_MORE)).click();
        mDevice.wait(Until.hasObject(By.pkg(mSettingsPackage).depth(0)), TIMEOUT);
    }

    private boolean isCar() {
        PackageManager pm = mContext.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE);
    }

    private boolean isWatch() {
      return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WATCH);
    }
}
