package com.alten.testsigma.addons.android.test;


import com.alten.testsigma.addons.android.GmailCheck;
import com.alten.testsigma.addons.android.InboxEmailAllRead;
import com.alten.testsigma.addons.android.SwipeOnScreen;

import com.google.common.collect.ImmutableMap;
import com.testsigma.sdk.TestData;
import com.testsigma.sdk.runners.ActionRunner;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.time.Duration;;

public class TestAndroidAction {
    private ActionRunner runner;
    private AndroidDriver driver;

    @BeforeClass
    public void setup() throws Exception {
        // Make sure to start Appium server
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("app", "C:\\NexiPay\\NEXIPay_7.9.1-mock-debugDexguard.apk");
        caps.setCapability("deviceName", "huawei-ana_nx9-VWS0220428003258");
        caps.setCapability("udid", "VWS0220428003258");
        caps.setCapability("platformName", "ANDROID");
        driver = new AndroidDriver(new URL("http://localhost:4723/wd/hub"), caps);
        caps.setCapability("noReset","true");
        caps.setCapability("fullReset","false");
        caps.setCapability("appPackage", "it.icbpi.mobile"); // Sostituisci con il pacchetto dell'app
        caps.setCapability("appActivity", "it.icbpi.mobile.feature.launchmode.LaunchModeActivity");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        //driver.executeScript("mobile: activeApp", ImmutableMap.of("appPackage", driver.getCapabilities().getCapability("appium:appPackage")));

        //driver.executeScript("mobile: appActivity", ImmutableMap.of("appPackage", ""));
        runner = new ActionRunner(driver); //Initialie Action runner

    }

    @Test
    public void Test() throws Exception {
        GmailCheck action = new GmailCheck();
        //InboxEmailAllRead action = new InboxEmailAllRead();
        action.setPassword(new TestData("ntgq zkne mhri vplj"));
        action.setUsername(new TestData("nexitestautomation@gmail.com"));
        action.setTargetPhrase(new TestData("Attenzione! Non è stato possibile richiamare le informazioni sul luogo di accesso."));
        runner.run(action);
    }

    @AfterClass
    public void teardown() {
        if (runner != null) {
            runner.quit();
        }
    }
}
