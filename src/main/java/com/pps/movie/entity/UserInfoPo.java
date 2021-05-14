package com.pps.movie.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
* (UserInfo)实体类
*
* @author default
* @since 2021-01-05 11:28:32
*/
@Data
@TableName("user_info")
public class UserInfoPo  implements Serializable {

private static final long serialVersionUID = -12468817133391798L;


private Integer id;
    
private Integer userId;
    
private String headImage;
    
private String sex;
    
private String address;
    
private Integer age;
    
private Integer lever;
    
private Date optTime;
    
private Integer flag;

@TableField(updateStrategy = FieldStrategy.NEVER )
private Integer movieNumber;

}