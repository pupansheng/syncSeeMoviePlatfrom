/*
 * Copyright (c) ACCA Corp.
 * All Rights Reserved.
 */
package com.pps.movie.pachong.moviecatch.impl;

import com.pps.movie.pachong.entity.PlayLink;
import com.pps.movie.pachong.entity.SearchVideo;
import com.pps.movie.pachong.moviecatch.ResourceCatchService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Pu PanSheng, 2021/5/14
 * @version OPRA v1.0
 */
@Service
public class JisuysbResource implements ResourceCatchService, InitializingBean {
    @Override
    public String getVideoUrl(String url) {
        return null;
    }

    @Override
    public List<SearchVideo> getSearchResult(String word) {
        return null;
    }

    @Override
    public List<PlayLink> videoResource(String link) {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
