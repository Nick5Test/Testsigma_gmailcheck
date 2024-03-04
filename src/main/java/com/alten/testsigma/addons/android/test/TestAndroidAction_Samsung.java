package com.alten.testsigma.addons.android.test;
import com.alten.testsigma.addons.android.GmailCheckDeviceInfo;
import com.alten.testsigma.addons.android.LaunchAppWithBundleID;
import com.testsigma.sdk.TestData;
import com.testsigma.sdk.runners.ActionRunner;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.time.Duration;

;

public class TestAndroidAction_Samsung {
    private ActionRunner runner;
    private AndroidDriver driver;

    @BeforeClass
    public void setup() throws Exception {
        // Make sure to start Appium server
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("app", "C:\\NexiPay\\NEXIPay_7.9.1-mock-debugDexguard.apk");
        caps.setCapability("deviceName", "samsung-sm_g991b-R5CR92NRVNP");
        caps.setCapability("udid", "R5CR92NRVNP");
        caps.setCapability("platformName", "ANDROID");
        driver = new AndroidDriver(new URL("http://localhost:4723/wd/hub"), caps);
        caps.setCapability("noReset","true");
        caps.setCapability("fullReset","false");
        caps.setCapability("appPackage", "it.icbpi.mobile"); // Sostituisci con il pacchetto dell'app  it.icbpi.mobile
        caps.setCapability("appActivity", "it.icbpi.mobile.feature.launchmode.LaunchModeActivity");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        runner = new ActionRunner(driver);

    }

    @Test
    public void Test() throws Exception {
        GmailCheckDeviceInfo action = new GmailCheckDeviceInfo();
        action.setPassword(new TestData("ntgq zkne mhri vplj"));
        action.setUsername(new TestData("nexitestautomation@gmail.com"));
        action.setTargetPhrase(new TestData("Attenzione! Non è stato possibile richiamare le informazioni sul luogo di accesso"));
        runner.run(action);
    }

    @AfterClass
    public void teardown() {
        if (runner != null) {
            runner.quit();
        }
    }
}
