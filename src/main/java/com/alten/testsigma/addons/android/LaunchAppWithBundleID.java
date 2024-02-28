package com.alten.testsigma.addons.android;

import com.testsigma.sdk.AndroidAction;
import com.testsigma.sdk.ApplicationType;
import com.testsigma.sdk.Result;
import com.testsigma.sdk.annotation.Action;
import com.testsigma.sdk.annotation.TestData;
import io.appium.java_client.android.AndroidDriver;
import lombok.Data;
import org.openqa.selenium.NoSuchElementException;

@Data
@Action(actionText = "Launch App with BundleID: bundleid",
        applicationType = ApplicationType.ANDROID,
        useCustomScreenshot = false)

public class LaunchAppWithBundleID extends AndroidAction {

    @TestData(reference = "bundleid")
    public com.testsigma.sdk.TestData bundleid;

    @Override
    public Result execute() throws NoSuchElementException {
        //Your Awesome code starts here
        Result result = null;
        AndroidDriver androidDriver = (AndroidDriver)this.driver;
        androidDriver.activateApp(bundleid.getValue().toString());
        result = com.testsigma.sdk.Result.SUCCESS;
        return result;
    }
}