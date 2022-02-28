package com.eu.frame.system.controller;

import com.eu.frame.common.exception.GlobalExceptionCode;
import com.eu.frame.common.wrapper.GlobalResponseWrapper;
import com.eu.frame.system.pojo.vo.FileVo;
import com.eu.frame.system.service.MinIOService;
import com.eu.frame.common.wrapper.Authority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private MinIOService minIOService;


    /**
     * 上传
     * 要求前端传递过来的 body 中文件的 key 必须为 file
     *
     * @param file
     * @return
     */
    @PostMapping("")
    @Authority(name = "文件上传", mark = "file:upload")
    public GlobalResponseWrapper upload(@RequestPart("file") MultipartFile file) {

        FileVo fileVo = this.minIOService.upload(file);

        if (fileVo != null) {
            return new GlobalResponseWrapper().data(fileVo);
        } else {
            return new GlobalResponseWrapper(GlobalExceptionCode.FILE_UPLOAD_FAIL);
        }

    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件路径
     * @return ResponseEntity<byte [ ]>
     */
    @PostMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam("fileUrl") String fileUrl) {
        ResponseEntity<byte[]> entity;
        byte[] bytes = this.minIOService.download(fileUrl);
        entity = new ResponseEntity<>(bytes, HttpStatus.OK);
        return entity;
    }

}
