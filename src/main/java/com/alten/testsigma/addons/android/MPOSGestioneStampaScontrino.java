package com.alten.testsigma.addons.android;

import com.testsigma.sdk.AndroidAction;
import com.testsigma.sdk.ApplicationType;
import com.testsigma.sdk.Result;
import com.testsigma.sdk.annotation.Action;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Action(actionText = "Android MPOS Gestione Stampa Scontrino", applicationType = ApplicationType.ANDROID)
public class MPOSGestioneStampaScontrino extends AndroidAction {

    @Override
    public Result execute() throws NoSuchElementException {


        //mapping per samsung con stampante usata
        String stampaButtonSamsung = "com.android.printspooler:id/print_button";

        //samsung senza stampante
        String stampaButtonSamsung2 = "//*[@text='Seleziona stampante']";

        //huawei
        String stampaButtonHuawei = "//*[@text='Select printer']";

        //Your Awesome code starts here
        Result result = null;
        logger.info("Initiating execution");
        AndroidDriver androidDriver = (AndroidDriver) this.driver;

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        String brand = (String) androidDriver.getCapabilities().getCapability("deviceManufacturer");
        System.out.println(brand);
//deviceManufacturer -> HUAWEI and deviceManufacturer -> samsung
        //creare if di cambio per passaggio da samsung e huawei e altri device

        if (brand.equals("samsung")) {
            System.out.println("brand samsung");
            boolean primoElementoPresente = false;
            try {
                WebElement stampa = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(stampaButtonSamsung)));
                if (stampa.isDisplayed()) {
                    primoElementoPresente = true;
                    setSuccessMessage("Bottone stampa per Samsung Android Visibile");
                    result = Result.SUCCESS;
                }
            } catch (Exception e) {
                System.out.println("Errore durante la ricerca del primo elemento:" + e);
            }
            if (!primoElementoPresente) {
                try {
                    WebElement stampa2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(stampaButtonSamsung2)));
                    if (stampa2.isDisplayed()) {
                        setSuccessMessage("Bottone stampa per Samsung Android (2) Visibile");
                        result = Result.SUCCESS;
                    } else {
                        setErrorMessage("Bottone Stampa per Samsung Android NON visibile, KO!!!");
                        result = Result.FAILED;
                    }
                } catch (Exception e) {
                    System.out.println("Errore durante la ricerca del secondo elemento:" + e);
                    result = Result.FAILED;
                }
            }
        } else if (brand.equals("HUAWEI")) {
                try {
                    //flusso huawei
                    WebElement stampa = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(stampaButtonHuawei)));

                    if (stampa.isDisplayed()) {
                        setSuccessMessage("Bottone stampa per Huawei Android Visibile");
                        result = Result.SUCCESS;
                    } else {
                        setErrorMessage("Bottone Stampa per Huawei Android NON visibile, KO!!!");
                        result = Result.FAILED;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result = Result.FAILED;
                }
            }
            return result;
        }

}