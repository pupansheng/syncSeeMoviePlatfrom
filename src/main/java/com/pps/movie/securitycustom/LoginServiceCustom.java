
package com.pps.movie.securitycustom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pps.core.authority.security.entity.SysUser;
import com.pps.core.authority.security.privider.CustomLoginService;
import com.pps.movie.entity.UserInfoPo;
import com.pps.movie.mapper.UserInfoMapper;
import com.pps.movie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pu PanSheng, 2021/5/12
 * @version OPRA v1.0
 */
@Component
public class LoginServiceCustom implements CustomLoginService {

    @Autowired
    UserService userService;
    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public Map getUserExtraInfo(String username) {

        SysUser sysUser = userService.queryOneByUserName(username);
        QueryWrapper<UserInfoPo> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(UserInfoPo::getUserId,sysUser.getId());
        UserInfoPo userInfoPo = userInfoMapper.selectOne(queryWrapper);
        Map map=new HashMap();
        map.put("ext",userInfoPo);
        return map;
    }

    @Override
    public void LoginFail(AuthenticationException exception, Map responseMap) {
     responseMap.put("err", exception.getMessage());
    }

    @Override
    public void logoutCustom(HttpServletRequest request, HttpServletResponse response) {


    }
}
