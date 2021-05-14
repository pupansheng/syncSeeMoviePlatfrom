
package com.pps.movie.pachong.moviecatch.impl;

import com.pps.core.authority.security.component.exception.ServiceException;
import com.pps.http.PpsHttpUtilForSpring;
import com.pps.http.myrequest.phantom.PhantomClientHttpResponse;
import com.pps.http.util.PhantomDriverUtil;
import com.pps.http.util.RegexUtil;
import com.pps.movie.pachong.entity.PlayLink;
import com.pps.movie.pachong.entity.ResourceStategy;
import com.pps.movie.pachong.entity.SearchVideo;
import com.pps.movie.pachong.moviecatch.ResourceCatchService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 6v 影视爬取
 *
 * @author Pu PanSheng, 2021/5/13
 * @version OPRA v1.0
 */
@Service
public class V6Resource implements ResourceCatchService, InitializingBean {

    private static String url = "https://www.ai66.cc";

    @Autowired
    private PpsHttpUtilForSpring ppsHttpUtilForSpring;

    @Override
    public String getVideoUrl(String url) {
        AtomicReference<String> r=new AtomicReference<>();
        ppsHttpUtilForSpring
                .createPhantojsClient(false)
                .setUrl(url)
                .setAutoTransforString(false)
                .get((re,s)->{

                    PhantomClientHttpResponse phantomClientHttpResponse = PhantomDriverUtil.castResponse(re);
                    PhantomJSDriver driver = phantomClientHttpResponse.getDriver();
                    WebDriver frame = driver.switchTo().frame(0);
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    PhantomDriverUtil.initDriver(driver);
                    driver.executeScript(PhantomDriverUtil.generateDataToPageJavascipt()+PhantomDriverUtil.getHostAddressScript()+"saveData(hostAddress+main)");
                    List<String> dataFromPage = PhantomDriverUtil.getDataFromPage();
                    if(dataFromPage==null||dataFromPage.size()==0){
                        throw new ServiceException("该资源不可用！");
                    }
                    r.set(dataFromPage.get(0));
                });

        return r.get();
    }

    @Override
    public List<SearchVideo> getSearchResult(String word) {

        List<SearchVideo> list = new ArrayList<>();

        ppsHttpUtilForSpring.createPhantojsClient(false)
                .setUrl(url).setAutoTransforString(false)
                .get((r, s) -> {
                    //找到搜索框
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    PhantomClientHttpResponse response = (PhantomClientHttpResponse) r;
                    PhantomJSDriver driver = response.getDriver();
                    List<WebElement> keyboards = driver.findElements(By.id("searchform"));
                    WebElement keyboard = keyboards.get(keyboards.size() - 1);
                    WebElement element = keyboard.findElement(By.className("search-s"));
                    element.sendKeys(word);
                    keyboard.findElement(By.id("searchsubmit")).click();
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String pageSource = driver.getPageSource();
                    List<String> lis = RegexUtil.findManyContentByStartAndEnd("<li class=\"post box row fixed-hight\">", "</li>", pageSource);
                    lis.forEach(li -> {

                        String image = RegexUtil.findOneContentByStartAndEnd("<img src=\"", "\"", li);
                        String link = RegexUtil.findOneContentByStartAndEnd("<a href=\"", "\"", li);
                        String name = RegexUtil.findOneContentByStartAndEnd("title=\"", "\"", li);
                        String artcle = RegexUtil.findOneContentByStartAndEnd("<div class=\"article\">", "</div>", li);
                        String desc = RegexUtil.findOneContentByStartAndEnd("<p>", "</p>", artcle);
                        SearchVideo searchVideo = new SearchVideo();
                        searchVideo.setDetail(desc);
                        searchVideo.setImage(image);
                        searchVideo.setLink(link);
                        searchVideo.setName(name);

                        list.add(searchVideo);
                    });

                });


        return list;
    }

    @Override
    public List<PlayLink> videoResource(String link) {

        List<PlayLink> links = new ArrayList<>();
        ppsHttpUtilForSpring.createPhantojsClient(false)
                .setAutoTransforString(false)
                .setUrl(link).get((r, s) -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String pageSource = ((PhantomClientHttpResponse) r).getDriver().getPageSource();

            List<String> divs = RegexUtil.findManyContentByStartAndEnd("<div class=\"widget box row\">", "</div>", pageSource);

            divs.forEach(div -> {
                String loca = RegexUtil.findOneContentByStartAndEnd("<h3>", "</h3>", div);
                List<String> as = RegexUtil.findManyContentByStartAndEnd(" <a", "</a>", div);
                as.forEach(a -> {
                    String herf = RegexUtil.findOneContentByStartAndEnd("href=\"", "\"", a);
                    String name = RegexUtil.findOneContentByStartAndEnd(">", "\"", a);
                    String desc = RegexUtil.findOneContentByStartAndEnd("<span>", "</span>", div);
                    PlayLink playLink = new PlayLink();
                    playLink.setUrl(herf);
                    playLink.setName(name + desc + "(" + loca + ")");
                    links.add(playLink);
                });


            });

        });

        return links;
    }
        @Override
        public void afterPropertiesSet () throws Exception {
            ResourceStategy.register("v6",this);
        }
    }
