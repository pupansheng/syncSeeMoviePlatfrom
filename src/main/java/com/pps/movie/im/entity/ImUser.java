package com.pps.movie.im.entity;


import com.pps.movie.entity.UserInfoPo;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

/**
 * @author
 * @discription;
 * @time 2021/1/5 11:35
 */
public class ImUser implements Serializable {
    private Long id;
    private String name;
    private UserInfoPo userInfoPo;
    private ChannelHandlerContext context;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserInfoPo getUserInfoPo() {
        return userInfoPo;
    }

    public void setUserInfoPo(UserInfoPo userInfoPo) {
        this.userInfoPo = userInfoPo;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
