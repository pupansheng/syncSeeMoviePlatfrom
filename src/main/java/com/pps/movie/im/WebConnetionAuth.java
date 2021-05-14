package com.pps.movie.im;

import com.alibaba.fastjson.JSON;
import com.pps.core.authority.security.property.JWTUtil;
import com.pps.websocket.server.hander.http.WebSocketConnetion;
import io.jsonwebtoken.Claims;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author
 * @discription;定义是否允许连接socket 的自定义验证
 * @time 2020/12/24 11:32
 */
@Component
@Slf4j
public class WebConnetionAuth extends WebSocketConnetion {

    @Autowired
    JWTUtil jwtUtil;
    public WebConnetionAuth() {
        super("/pps");
    }

    public WebConnetionAuth(String wsUrl) {
        super(wsUrl);
    }

    @Override
    public Boolean webSocketCanConnetion(Map<String, Object> requestParams, ChannelHandlerContext ctx) {
        String token=(String)requestParams.get("token");
        if(token==null){
            return false;
        }
        Claims claims = jwtUtil.getClaimByToken(token);
        boolean isNoVaild = claims == null || claims.isEmpty() || jwtUtil.isTokenExpired(claims.getExpiration());
        if (isNoVaild) {
            log.info("token已过期：");
           return false;
        }
        String info = claims.getSubject();
        Map map = JSON.parseObject(info, Map.class);
        String userName=(String)map.get("userName");
        putData(ctx.channel(),"username",userName);
        return  true;
    }

    @Override
    public void customOpreate(ChannelHandlerContext ctx) {

    }
}
