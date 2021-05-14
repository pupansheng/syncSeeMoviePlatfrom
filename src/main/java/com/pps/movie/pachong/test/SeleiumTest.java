package com.pps.movie.pachong.test;

import com.pps.http.util.RegexUtil;
import com.pps.movie.pachong.entity.PlayLink;
import com.pps.movie.pachong.entity.SearchVideo;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author
 * @discription;
 * @time 2021/1/25 15:27
 */
public class SeleiumTest {



    public static void main(String args[]) throws InterruptedException, IOException {


        //设置必要参数
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", false);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", false);

        dcaps.setPlatform(Platform.ANDROID);

        dcaps.setCapability("load-images","no");
        dcaps.setCapability("ignore-ssl-errors",true);
        //js支持
        dcaps.setJavascriptEnabled(true);

        //驱动支持
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,"c:\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        //创建无界面浏览器对象
        String t1="https://www.ai66.cc/";

        String t2="https://www.ai66.cc/e/DownSys/play/?classid=2&id=15788&pathid1=0&bf=0";
        String t3="https://www.ai66.cc/dianshiju/guoju/15652.html";
        String t4="https://vod3.buycar5.cn/share/HiAzaKx8juh5SPB6";
        String t6="http://127.0.0.1:8848/test/index.html";
        String t7="https://www.jisuysw.com";
        String word="灵笼";
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        Instant instant=Instant.now();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        PhantomDriverUtil.initDriver(driver);
        PhantomDriverUtil.openConsoleLog();

       try {
           driver.get(t7);

           List<WebElement> formsearchs = driver.findElements(By.id("formsearch"));
           WebElement webElement = formsearchs.get(formsearchs.size() - 1);

           WebElement wd = webElement.findElement(By.id("wd"));
           wd.sendKeys(word);
           WebElement wdB = webElement.findElement(By.tagName("button"));
           wdB.click();
           Thread.sleep(2000);

           String pageSource = driver.getPageSource();
           System.out.println(pageSource);

       }finally {

           Instant instant2=Instant.now();
           long seconds = Duration.between(instant, instant2).toMillis();
           System.out.println("花费时间："+seconds);
           driver.close();
           driver.quit();

       }





    }

}
