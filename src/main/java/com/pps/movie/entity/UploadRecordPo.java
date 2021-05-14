package com.pps.movie.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
* (UploadRecord)实体类
*
* @author default
* @since 2020-12-30 17:46:17
*/
@Data
@TableName("upload_record")
public class UploadRecordPo  implements Serializable {


private static final long serialVersionUID = 250150923260118014L;
    
private String id;
    
private String key;
    
private String url;
    
private Integer userId;
    
private String fileName;
    
private String suffix;
    
private Date optTime;


}