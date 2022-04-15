package com.example.qukuailian.controller;

import com.example.qukuailian.bean.Message;
import com.example.qukuailian.bean.UserWithBLOBs;
import com.example.qukuailian.dao.UserMapper;
import com.example.qukuailian.service.CertService;
import com.example.qukuailian.service.UserService;
import com.example.qukuailian.util.OPE.CustomException;
import com.example.qukuailian.util.OPE.MessageUtil;
import com.example.qukuailian.util.Cert.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/cert")
public class DownloadController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<Object> downloadFile(String username) {
        HttpHeaders headers = new HttpHeaders();
        try {        //读取文件
            UserWithBLOBs uwb = userMapper.selectByUserName(username);

            if (uwb == null) {
                return new ResponseEntity<>(
                        "用户不存在",
                        HttpStatus.BAD_REQUEST);
            }

            if (uwb.getCert() == null || uwb.getCert().length == 0) {
                return new ResponseEntity<>(
                        "证书不存在",
                        HttpStatus.BAD_REQUEST);
            }

            byte[] bytes = uwb.getCert();

            //设置响应头
            String filename = username + ".cer";
            File file = FileUtil.byteToFile(bytes, filename);
            FileSystemResource fsr = new FileSystemResource(file);

            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fsr.getFilename()));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(fsr.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify")
    public Message<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 还原证书
            if (CertService.verifyCertificate(file)) return MessageUtil.ok("验证成功");
            else return MessageUtil.ok("验证失败");
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120,e.getMessage());
        }
    }
}
