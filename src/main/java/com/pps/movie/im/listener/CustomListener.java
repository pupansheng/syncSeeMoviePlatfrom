package com.pps.movie.im.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pps.core.authority.security.entity.SysUser;
import com.pps.core.authority.security.mapper.SysUserDao;
import com.pps.movie.entity.UserInfoPo;
import com.pps.movie.im.entity.ImUser;
import com.pps.movie.mapper.UserInfoMapper;
import com.pps.websocket.server.chat.ChatFromProtocl;
import com.pps.websocket.server.chat.ChatProtocl;
import com.pps.websocket.server.chat.MsgAction;
import com.pps.websocket.server.event.SocketEnvent;
import com.pps.websocket.server.listener.SocketAutoListenr;
import com.pps.websocket.server.util.SendMsgUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 * @discription;
 * @time 2020/12/17 15:30
 */
@Component
@Slf4j
public class CustomListener implements SocketAutoListenr {

    public static final ConcurrentHashMap<String, ImUser> LOGIN_USER=new ConcurrentHashMap<>();
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    SysUserDao sysUserDao;

    @Override
    public void loginSuccessful(SocketEnvent socketEnvent) {

        ImUser user=new ImUser();
        String username= (String)getDataByKey(socketEnvent.getContent().channel(),"username");
        SysUser sysUser =new SysUser() ;
        sysUser.setName(username);
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getName,username);
        sysUser = sysUserDao.selectOne(queryWrapper);
        user.setName(username);
        user.setId(sysUser.getId());
        UserInfoPo userInfoPo =new UserInfoPo() ;
        userInfoPo.setUserId(sysUser.getId().intValue());
        QueryWrapper<UserInfoPo> userInfoPoQueryWrapper=new QueryWrapper<>();
        userInfoPoQueryWrapper.lambda().eq(UserInfoPo::getUserId,sysUser.getId());
        UserInfoPo userInfo= userInfoMapper.selectOne(userInfoPoQueryWrapper);
        user.setUserInfoPo(userInfo);
        user.setContext(socketEnvent.getContent());
        LOGIN_USER.put(username,user);
        log.info(username+":登陆成功！");
    }

    @Override
    public void loginFail(SocketEnvent socketEnvent) {


        System.out.println("登陆失败");

    }

    @Override
    public void loginOut(SocketEnvent socketEnvent) {

        String username= (String)getDataByKey(socketEnvent.getContent().channel(),"username");
        LOGIN_USER.remove(username);
        log.info(username+":用户退出：");

        //通知用户
        LOGIN_USER.forEach((k,v)->{
            ChatProtocl chatProtocl=new ChatProtocl();
            chatProtocl.setData(username);
            chatProtocl.setMsgType(100);
            ChannelHandlerContext context = v.getContext();
            SendMsgUtil.sendMsgToClientForText(context,chatProtocl);
        });



    }

    @Override
    public void acceptText(ChannelHandlerContext content, ChatFromProtocl chatFromProtocl, Date happenTime) {

        //心跳检测包
        if(chatFromProtocl.getMsgType()==2000){

            ChatProtocl chatProtocl=new ChatProtocl();
            chatProtocl.setMsgType(2000);
            SendMsgUtil.sendMsgToClientForText(content,chatProtocl);
            return;
        }


        int action = chatFromProtocl.getAction();
        MsgAction actionByType = MsgAction.getActionByType(action);

        switch (actionByType){
            case sendToSingle:
                String to = chatFromProtocl.getTo();
                ImUser user = LOGIN_USER.get(to);
                if(user!=null){
                    ChannelHandlerContext context = user.getContext();
                    ChatProtocl chatProtocl=new ChatProtocl();
                    chatProtocl.setData(chatFromProtocl.getData());
                    chatProtocl.setMsgType(chatFromProtocl.getMsgType());
                    SendMsgUtil.sendMsgToClientForText(context,chatProtocl);
                }else {
                    boolean existsUser=false;
                    //todo 如果用户存在 只是不在线 可以离线保存 上线时再发送
                   if(existsUser){


                    }else {//todo 如果用户不存在



                    }


                }
                break;
            case sendToAll:

                break;
            case other:

                break;
            default:
                SendMsgUtil.sendMsgToClientForErrorInfo(content,"不支持该动作！");
        }
    }

    @Override
    public void acceptBin(ChannelHandlerContext content, CharSequence fileInfo, ByteBuf fileData, Date happenTime) {
        log.info("服务器收到二进制消息：文件信息："+fileInfo);
        Map map = JSON.parseObject(String.valueOf(fileInfo), Map.class);


    }

    @Override
    public void other(SocketEnvent socketEnvent) {


    }
}
