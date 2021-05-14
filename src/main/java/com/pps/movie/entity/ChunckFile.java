package com.pps.movie.entity;

import lombok.extern.slf4j.Slf4j;
import property.UploadResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @discription;
 * @time 2020/12/30 11:34
 */
@Slf4j
public class ChunckFile {

    private String key;
    private String name;
    private String uploadId;
    private String sign;
    private Integer pageSize;
    private volatile  boolean isComplete=false;
    private List<UploadResult> uploadResults=new ArrayList<>();

   public synchronized void   add(UploadResult uploadResult){
        uploadResults.add(uploadResult);
   }
    public synchronized   List<UploadResult> getUploadResults() {
        return uploadResults;
    }

    public synchronized void  setUploadResults(List<UploadResult> uploadResults) {
        this.uploadResults = uploadResults;
    }

    public synchronized int getHasUploadSize(){
        return  this.uploadResults.size();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public boolean isComplete() {
        return isComplete;
    }
    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
