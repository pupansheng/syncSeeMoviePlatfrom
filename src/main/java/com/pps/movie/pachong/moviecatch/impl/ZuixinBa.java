/*
 * Copyright (c) ACCA Corp.
 * All Rights Reserved.
 */
package com.pps.movie.pachong.moviecatch.impl;

import com.pps.core.authority.security.component.exception.ServiceException;
import com.pps.http.PpsHttpUtilForSpring;
import com.pps.http.myrequest.phantom.PhantomClientHttpResponse;
import com.pps.http.util.RegexUtil;
import com.pps.movie.pachong.entity.PlayLink;
import com.pps.movie.pachong.entity.ResourceStategy;
import com.pps.movie.pachong.entity.SearchVideo;
import com.pps.movie.pachong.moviecatch.ResourceCatchService;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Pu PanSheng, 2021/5/9
 * @version OPRA v1.0
 */
@Service
public class ZuixinBa implements ResourceCatchService, InitializingBean {

    @Autowired
    private PpsHttpUtilForSpring ppsHttpUtilForSpring;

    @Override
    public String getVideoUrl(String url) {


        AtomicReference<String> result=new AtomicReference<>();

        try {
            ppsHttpUtilForSpring.createSyncClient().setUrl(url).get((r,s)->{
                String js = RegexUtil.findOneContentByStartAndEnd("<div class=\"player\" id=\"player\">", "</div>", s);
                String frame = RegexUtil.findOneContentByStartAndEnd("src=\"", "\"", js);
                frame="https://www.zuixin8.com"+frame;
                ppsHttpUtilForSpring.createSyncClient().setUrl(frame).get((r2,s2)->{
                    String frame2 = RegexUtil.findOneContentByStartAndEnd("src=\"", "\"", s2);
                    ppsHttpUtilForSpring.createSyncClient().setUrl(frame2).get((r3,s3)->{
                        String Vurl = RegexUtil.findOneContentByStartAndEnd("var vid=\"", "\"", s3);
                        result.set(Vurl);

                    });
                });
            });
        } catch (Exception e) {

        }

        String s = result.get();
        if(s==null||"".equals(s)){

            //尝试patomjs
            ppsHttpUtilForSpring.createPhantojsClient(false).setUrl(url).setAutoTransforString(false).get((r,h)->{
                PhantomClientHttpResponse r1 = (PhantomClientHttpResponse) r;
                PhantomJSDriver driver = r1.getDriver();
                WebDriver frame = driver.switchTo().frame(0);
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String pageSource1 = frame.getPageSource();
                String vi= RegexUtil.findOneContentByStartAndEnd("<video", "</video>", pageSource1);
                String u = RegexUtil.findOneContentByStartAndEnd("src=\"", "\"", vi);
                if(u==null||"".equals(u)){
                    throw new ServiceException("此资源不可用！");
                }
                result.set(u);
            });

        }


        return result.get();
    }

    @Override
    public List<SearchVideo> getSearchResult(String word) {

        List<SearchVideo> list=new ArrayList<>();
        HashMap hashMap=new HashMap();
        hashMap.put("wd",word);
        ppsHttpUtilForSpring.createSyncClient().setParam(hashMap).setAddW(true).setUrl("https://www.zuixin8.com/search").get((r,s)->{
            List<String> divs = RegexUtil.findManyContentByStartAndEnd("<div class=\"movie-item\">", "</div>", s);
            divs.forEach(p->{
                String a = RegexUtil.findOneContentByStartAndEnd("<a", "</a>", p);
                String name= RegexUtil.findOneContentByStartAndEnd("title=\"","\"",a);
                String image= RegexUtil.findOneContentByStartAndEnd("src=\"","\"",a);
                String link= RegexUtil.findOneContentByStartAndEnd("href=\"","\"",a);
                link="https://www.zuixin8.com"+link;
                SearchVideo searchVideo=new SearchVideo();
                searchVideo.setName(name);
                searchVideo.setLink(link);
                searchVideo.setImage(image);
                searchVideo.setDetail(name);
                list.add(searchVideo);
            });
        });

        return list;
    }

    @Override
    public List<PlayLink> videoResource(String link) {

        List<PlayLink> links=new ArrayList<>();
        ppsHttpUtilForSpring.createSyncClient().setUrl(link).get((r,s)->{

            List<String> uls = RegexUtil.findManyContentByStartAndEnd("<ul class=\"dslist-group\">", "</ul>", s);

            int a []=new int[1];
            uls.forEach(ul->{
                a[0]++;
                List<String> lis = RegexUtil.findManyContentByStartAndEnd("<li ", "</li>", ul);
                lis.forEach(li->{

                    String url=  RegexUtil.findOneContentByStartAndEnd("href=\"","\"",li);
                    url="https://www.zuixin8.com"+url;
                    String t = RegexUtil.findOneContentByStartAndEnd("<a", "/a>", li);
                    String d = RegexUtil.findOneContentByStartAndEnd(">", "<", t);
                    String name=d+"(源"+a[0]+")";
                    PlayLink playLink=new PlayLink();
                    playLink.setName(name);
                    playLink.setUrl(url);

                    links.add(playLink);

                });

            });


        });

        return links;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ResourceStategy.register("zuixin8", this);
    }
}
