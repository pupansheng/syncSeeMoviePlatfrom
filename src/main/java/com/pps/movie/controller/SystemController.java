package com.pps.movie.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pps.core.authority.security.entity.SysPermisson;
import com.pps.core.authority.security.entity.SysRole;
import com.pps.core.authority.security.entity.SysUser;
import com.pps.core.authority.security.mapper.SysPermissonDao;
import com.pps.core.authority.security.mapper.SysRoleDao;
import com.pps.core.authority.security.mapper.SysUserDao;
import com.pps.core.authority.security.property.MySecurityProperty;
import com.pps.core.common.model.Result;
import com.pps.core.common.util.ValidateUtil;
import com.pps.movie.entity.UserInfoPo;
import com.pps.movie.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author
 * @discription;
 * @time 2020/8/27 17:07
 */
@RestController
@RequestMapping("/jwt/system")
public class SystemController {

    @Autowired
    SysPermissonDao sysPermissonDao;
    @Autowired
    SysRoleDao sysRoleDao;
    @Autowired
    SysUserDao sysUserDao;
    @Autowired
    MySecurityProperty mySecurityProperty;
    @Autowired
    UserInfoMapper userInfoMapper;

    @RequestMapping("/getLoginInfo")//登陆获得自己登录信息
    public Result getLoginInfo(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username= (String) authentication.getPrincipal();
        Map info=new HashMap<>();

        if(mySecurityProperty.getOpenConfigUser()){
            List<Map> maps = JSON.parseArray(mySecurityProperty.getConfigUser(), Map.class);
            List<Map> userMap = maps.stream().filter(p -> p.get("username").equals(username)).collect(Collectors.toList());
            if(ValidateUtil.isNotEmpty(userMap)){
                SysUser sysUser=new SysUser();
                sysUser.setName((String)userMap.get(0).get("username"));
                sysUser.setPassword((String)userMap.get(0).get("password"));
                List<SysRole> roles=new ArrayList<>();
                SysRole sysRole1=new SysRole();
                sysRole1.setId(1L);
                sysRole1.setName("ROLE_ADMIN");
                roles.add(sysRole1);
                info.put("userInfo",sysUser);
                info.put("roles", roles);
                info.put("permission",new ArrayList<>());
                return  Result.ok(info);
            }
        }


        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getName,username);
        SysUser sysUser = sysUserDao.selectOne(queryWrapper);
        QueryWrapper<SysRole> roleQueryWrapper=new QueryWrapper<>();
        roleQueryWrapper.lambda().eq(SysRole::getUserId,sysUser.getId());
        List<SysRole> sysRoles =sysRoleDao.selectList(roleQueryWrapper);
        info.put("userInfo",sysUser);
        info.put("roles", sysRoles);
        List<SysPermisson> sysPermissons=new ArrayList<>();
        sysRoles.stream().forEach(p->{
            QueryWrapper<SysPermisson> sysPermissonQueryWrapper=new QueryWrapper<>();
            sysPermissonQueryWrapper.lambda().eq(SysPermisson::getSysRoleId,p.getId());
            List<SysPermisson> sysPermissons1 = sysPermissonDao.selectList(sysPermissonQueryWrapper);
            sysPermissons.addAll(sysPermissons1);
        });

        info.put("permission",sysPermissons);

        QueryWrapper<UserInfoPo> userInfoPoQueryWrapper=new QueryWrapper<>();
        userInfoPoQueryWrapper.lambda().eq(UserInfoPo::getUserId,sysUser.getId());
        UserInfoPo userInfoPoDb = userInfoMapper.selectOne(userInfoPoQueryWrapper);
        info.put("userInfoExt",userInfoPoDb);

        return  Result.ok(info);
    }





}
