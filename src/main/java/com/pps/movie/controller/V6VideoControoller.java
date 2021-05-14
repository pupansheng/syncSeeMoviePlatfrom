package com.pps.movie.controller;

import com.alibaba.fastjson.JSONObject;
import com.pps.core.authority.security.component.exception.ServiceException;
import com.pps.core.common.model.Result;
import com.pps.movie.im.entity.ImUser;
import com.pps.movie.im.listener.CustomListener;
import com.pps.movie.pachong.moviecatch.ResourceCatchService;
import com.pps.movie.pachong.analy.HtmlAnaly;
import com.pps.movie.pachong.entity.ResourceStategy;
import com.pps.movie.pachong.entity.SearchVideo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @author
 * @discription;
 * @time 2020/12/30 17:24
 */
@RestController
@RequestMapping("/jwt/v6")
@Slf4j
public class V6VideoControoller extends BaseController {

    @Autowired
    HtmlAnaly htmlAnaly;

    @PostMapping("/source")
    public Result getSource(){

        return  Result.ok(ResourceStategy.getKeys());

    }
    @PostMapping("/search")
    public Result getList(@RequestBody JSONObject jsonObject){
        ResourceCatchService resourceCatchService = ResourceStategy.get(jsonObject.getString("source"));
        List<SearchVideo> keyword = resourceCatchService.getSearchResult(jsonObject.getString("keyword"));
        if(keyword.size()==0){
            throw new ServiceException("没有搜索到内容");
        }
        return  Result.ok(keyword);

    }
    @PostMapping("/getPlayList")
    public Result add(@RequestBody JSONObject jsonObject){
        String link = jsonObject.getString("link");
        ResourceCatchService resourceCatchService = ResourceStategy.get(jsonObject.getString("source"));
        return Result.ok(resourceCatchService.videoResource(link));

    }

    @PostMapping("/getHttpLink")
    public Result get(@RequestBody JSONObject jsonObject) {
        String username= (String) (SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        ImUser imUser = CustomListener.LOGIN_USER.get(username);
        if(!Objects.isNull(imUser)){
            Integer movieNumber = imUser.getUserInfoPo().getMovieNumber();
            if(movieNumber<=0){
                throw new ServiceException(username+"用户 观影卷已经没有了！请充值！");
            }
        }else {
            throw new ServiceException(username+"用户 未在线！");
        }
        ResourceCatchService resourceCatchService = ResourceStategy.get(jsonObject.getString("source"));
        String resourceId = resourceCatchService.getVideoUrl(jsonObject.getString("resourceId"));
        return Result.ok(resourceId);
    }
    @PostMapping("/analy")
    public Result anltHtml(@RequestBody JSONObject jsonObject){
        return Result.ok(htmlAnaly.toOutHtml(jsonObject.getString("url"),jsonObject.getJSONObject("header")));
    }
}
