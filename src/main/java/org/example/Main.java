package org.example;

import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main {

    static String email;
    static int test_number;

        public static String generateRandomString(int length, String characters) {
            Random random = new Random();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int randomIndex = random.nextInt(characters.length());
                char randomChar = characters.charAt(randomIndex);
                sb.append(randomChar);
            }
            return sb.toString();
        }


    //kas vyksta prieš tai - registracija
        @BeforeAll
        public static void before() throws InterruptedException {
            test_number = 0;
            String lowerCharacters = "abcsdefghijklmnopqrstuvwxyz";
            email = generateRandomString(6, lowerCharacters) + "@gmail.com";
            ChromeDriver driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get("https://demowebshop.tricentis.com/");
            driver.findElement(By.xpath("//a[@href='/login']")).click();
            driver.findElement(By.xpath("//input[@class='button-1 register-button']")).click();
            Thread.sleep(2000);
            driver.findElement(By.xpath("//input[@id='gender-male']")).click();
            driver.findElement(By.xpath("//input[@name='FirstName']")).sendKeys("Petriukas");
            driver.findElement(By.xpath("//input[@name='LastName']")).sendKeys("Petras");
            driver.findElement(By.xpath("//input[@id='Email']")).sendKeys(email);
            driver.findElement(By.xpath("//input[@id='Password']")).sendKeys("thisisatest2");
            driver.findElement(By.xpath("//input[@id='ConfirmPassword']")).sendKeys("thisisatest2");
            driver.findElement(By.xpath("//input[@id='register-button']")).click();
            driver.findElement(By.xpath("//input[@class='button-1 register-continue-button']")).click();
            driver.quit();
        }

        //Testai
        @ParameterizedTest
        @ValueSource(strings = {"src/main/resources/data1.txt", "src/main/resources/data2.txt"})
        public void scenario(String path) throws IOException, InterruptedException {
            ChromeDriver driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get("https://demowebshop.tricentis.com/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            //Prisijungimas
            driver.findElement(By.xpath("//a[@href='/login']")).click();
            driver.findElement(By.xpath("//input[@id='Email']")).sendKeys(email);
            driver.findElement(By.xpath("//input[@id='Password']")).sendKeys("thisisatest2");
            driver.findElement(By.xpath("//input[@class='button-1 login-button']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='inactive']//a[@href='/digital-downloads']")));

            driver.findElement(By.xpath("//li[@class='inactive']//a[@href='/digital-downloads']")).click();

            List<String> lines = readFromFile(path);
            for (String line: lines
            ) {
//                driver.findElement(By.xpath("//h2[@class='product-title']/a[text()='" + line +
//                        "']/ancestor::div[@class='details']/div[@class='add-info']/div[@class" +
//                        "='buttons']/input[@value='Add to cart']")).click();
//                Thread.sleep(2000);
                WebElement cart = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("" +
                        "//h2[@class='product-title']/a[text()='" + line +
                        "']/ancestor::div[@class='details']/div[@class='add-info']/div[@class" +
                        "='buttons']/input[@value='Add to cart']")));
                cart.click();
            }
            driver.findElement(By.xpath("//a[@href='/cart']")).click();

            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement webel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='termsofservice']")));
            js.executeScript("arguments[0].scrollIntoView(true);", webel);
            webel.click();
            driver.findElement(By.xpath("//button[@id='checkout']")).click();

            if(test_number == 0) {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='BillingNewAddress_CountryId']")));
                driver.findElement(By.xpath("//select[@id='BillingNewAddress_CountryId']")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//option[@value='4']")));
                driver.findElement(By.xpath("//option[@value='4']")).click();
                driver.findElement(By.xpath("//input[@id='BillingNewAddress_City']")).sendKeys("test");
                driver.findElement(By.xpath("//input[@id='BillingNewAddress_Address1']")).sendKeys("test");
                driver.findElement(By.xpath("//input[@id='BillingNewAddress_ZipPostalCode']")).sendKeys("post-3333");
                driver.findElement(By.xpath("//input[@id='BillingNewAddress_PhoneNumber']")).sendKeys("3333333333");
            }

            driver.findElement(By.xpath("//input[@title='Continue']")).click();

            webel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='button-1 payment-method-next-step-button']")));
            //driver.findElement(By.xpath("//input[@class='button-1 payment-method-next-step-button']")).click();
            webel.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='button-1 payment-info-next-step-button']")));
            driver.findElement(By.xpath("//input[@class='button-1 payment-info-next-step-button']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='button-1 confirm-order-next-step-button']")));
            webel = driver.findElement(By.xpath("//input[@class='button-1 confirm-order-next-step-button']"));
            js.executeScript("arguments[0].scrollIntoView(true);", webel);
            webel.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='title']")));
            String result = driver.findElement(By.xpath("//div[@class='title']")).getText();

            assertEquals("Your order has been successfully processed!", result);
            test_number = 1;
            driver.quit();
        }


        public List<String> readFromFile(String filePath) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            List<String> lines = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            return lines;
        }

}
