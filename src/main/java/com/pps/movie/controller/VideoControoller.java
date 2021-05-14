package com.pps.movie.controller;


import com.pps.core.common.model.Result;
import com.pps.movie.mapper.UploadRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pps.movie.entity.UploadRecordPo;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author
 * @discription;
 * @time 2020/12/30 17:24
 */
@RestController
@RequestMapping("/jwt/video")
public class VideoControoller extends BaseController {

    @Autowired
    UploadRecordMapper uploadRecordMapper;

    @PostMapping("/list")
    public Result getList(){

        List<UploadRecordPo> uploadRecordPos = uploadRecordMapper.selectList(null);
       return Result.ok(uploadRecordPos);

    }
    @PostMapping("/add")
    public Result add(@RequestBody UploadRecordPo uploadRecordPo){

        uploadRecordPo.setOptTime(new Date());
        uploadRecordPo.setId(UUID.randomUUID().toString());
        uploadRecordMapper.insert(uploadRecordPo);
        return Result.ok(uploadRecordPo);

    }
    @PostMapping("/delete")
    public Result delete(@RequestBody UploadRecordPo uploadRecordPo){
        int delete = uploadRecordMapper.deleteById(uploadRecordPo.getId());
        return Result.ok(delete==1);
    }

}
