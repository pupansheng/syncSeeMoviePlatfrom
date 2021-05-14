package com.pps.movie.controller;

import com.alibaba.fastjson.JSONObject;
import com.pps.core.common.model.Result;
import com.pps.movie.db.GlobalDb;
import com.pps.movie.entity.ChunckFile;
import com.pps.movie.mapper.UploadRecordMapper;
import com.pps.movie.entity.UploadRecordPo;
import exception.ChunckInitException;
import lombok.extern.slf4j.Slf4j;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import property.UploadResult;
import util.FastdfsUtil;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/jwt/file")
@Slf4j
public class uploadController extends BaseController {


    @Autowired
    GlobalDb globalDb;
    @Autowired
    UploadRecordMapper uploadRecordMapper;
    @Autowired
    FastdfsUtil fastdfsUtil;

    @PostMapping("chunck/process")
    public Result searchProcess(@RequestBody JSONObject jsonObject){
        String uploadId=jsonObject.getString("uploadId");
        ChunckFile value = (ChunckFile) globalDb.getValue(uploadId);
        return Result.ok(value.getHasUploadSize());
    }
    @PostMapping("chunck/complete")
    public Result complete(@RequestBody JSONObject jsonObject) throws IOException, MyException {
        String uploadId=jsonObject.getString("uploadId");
        ChunckFile value = (ChunckFile) globalDb.getValue(uploadId);
        try {
            List<UploadResult> uploadResults = value.getUploadResults();
            String url= uploadResults.get(0).getUrl();
            UploadRecordPo uploadRecordPo = new UploadRecordPo();
            uploadRecordPo.setKey(uploadResults.get(0).getFileId());
            uploadRecordPo.setUrl(url);
            uploadRecordPo.setSuffix(value.getName().substring(value.getName().lastIndexOf(".")+1));
            uploadRecordPo.setOptTime(new Date());
            uploadRecordPo.setId(UUID.randomUUID().toString());
            uploadRecordMapper.insert(uploadRecordPo);
            return Result.ok(url);
        } catch (Exception e){
            globalDb.deleteValue(uploadId);
            globalDb.deleteValue(value.getSign());
            fastdfsUtil.deleteFile(uploadId);
            throw  new RuntimeException(e);
        }
    }


    @PostMapping("/chunck/upload")
    public synchronized Result uploadFile(MultipartFile file, Integer pageNumber, String uploadId){
        ChunckFile value = (ChunckFile) globalDb.getValue(uploadId);
        if(value==null){
            throw  new RuntimeException("上传终止");
        }
        List<UploadResult> uploadResults = value.getUploadResults();
        boolean isHasUpload=  uploadResults.stream().anyMatch(p->{
            if(p.getPageNumber()==pageNumber){
                return  true;
            }
            return false;
        });
       if(isHasUpload){
          return Result.ok("已上传");
       }
        try {
            fastdfsUtil.chunckUpload(value.getUploadId(),file.getBytes(),pageNumber,value.getPageSize(),(t)->{
                value.add(t);
            });
            return Result.ok("已上传");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.err("上传发生错误 已取消");
        }
    }
    @PostMapping("/chunck/init")
    @ResponseBody
    public Result initChunckFile(@RequestBody JSONObject jsonObject) throws ChunckInitException {
        Map map=new HashMap();
        String sign = jsonObject.getString("sign");
        String name = jsonObject.getString("name");
        String user = jsonObject.getString("user");
        String userId = jsonObject.getString("userId");
        Integer pageSize = jsonObject.getInteger("pageSize");
        Object value = globalDb.getValue(sign);
        if(value==null){
            //todo 说明此前没有传过这个文件对象
            //创建文件key
            String key=user+"/"+userId+"/"+name;
            log.info("创建文件{}的key：{}",name,key);
            String uploadId = fastdfsUtil.init_chunck(name,map);
            log.info("创建文件{}的uploadId：{}",name,uploadId);
            ChunckFile chunckFile=new ChunckFile();
            chunckFile.setKey(key);
            chunckFile.setUploadId(uploadId);
            chunckFile.setName(name);
            chunckFile.setPageSize(pageSize);
            chunckFile.setSign(sign);
            globalDb.putValue(sign,uploadId);
            globalDb.putValue(uploadId,chunckFile);
            map.put("hasUpload",false);
            map.put("uploadId",uploadId);
            map.put("pageNumber",1);
            map.put("key",key);
        }else {//已上传过 但是还未上传完毕

            String uploadID=(String)value;
            ChunckFile chunckFile= (ChunckFile)globalDb.getValue(uploadID);
            String key=chunckFile.getKey();
            map.put("uploadId",uploadID);
            map.put("key",key);
            map.put("pageNumber",1);
            map.put("hasUpload",true);
        }

        return  Result.ok(map);
    }
    @PostMapping("/simple/upload")
    @ResponseBody
    public Result uploadFileSimple(MultipartFile file) throws IOException {

        UploadResult uploadResult = fastdfsUtil.uploadFile(file.getInputStream(), file.getOriginalFilename(), new HashMap<>());
        return  Result.ok(uploadResult.getUrl());
    }
}
