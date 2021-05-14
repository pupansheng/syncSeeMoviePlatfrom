package com.pps.movie;


import com.pps.core.authority.security.mapper.SysUserDao;
import com.pps.movie.pachong.entity.PlayLink;
import com.pps.movie.pachong.entity.SearchVideo;
import com.pps.movie.pachong.moviecatch.impl.V6Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SyncMovieAppTest {


    @Autowired
    SysUserDao sysUserDao;

    @Autowired
    V6Resource resouce;
    @Test
    public void f1(){
/*
        List<SearchVideo> d = resouce.getSearchResult("盗梦");
        System.out.println(d);*/

        List<SearchVideo> daom = resouce.getSearchResult("盗梦");

        daom.forEach(searchVideo -> {
            System.out.println(searchVideo);
            String link = searchVideo.getLink();
            List<PlayLink> links = resouce.videoResource(link);
            links.forEach(linksss->{
                String videoUrl = resouce.getVideoUrl(linksss.getUrl());
                System.out.println(videoUrl);
            });

            System.out.println("------------------");

        });


    }



}