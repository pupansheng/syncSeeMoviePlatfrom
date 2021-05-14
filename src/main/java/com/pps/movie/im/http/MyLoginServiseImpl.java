package com.pps.movie.im.http;

import com.pps.websocket.server.hander.http.LoginImService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author
 * @discription;定义登录接口
 * @time 2020/12/16 17:00
 */
@Component
public class MyLoginServiseImpl extends LoginImService {
    public MyLoginServiseImpl() {
        super("/login");
    }
    public MyLoginServiseImpl(String url) {
        super(url);
    }
    @Override
    public Object validUser(Map<String, Object> requestParams, ChannelHandlerContext ctx) {
        String username = (String) requestParams.get("username");
        String password = (String) requestParams.get("password");
        if("123".equals(password)){
          return "ppppppp";
        }
        return null;
    }
}
