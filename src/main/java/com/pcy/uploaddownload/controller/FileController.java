package com.pcy.uploaddownload.controller;

import com.alibaba.fastjson.JSONObject;
import com.pcy.uploaddownload.payload.UploadFileResponse;
import com.pcy.uploaddownload.property.FileStorageProperties;
import com.pcy.uploaddownload.service.AudioService;
import com.pcy.uploaddownload.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

  private static final Logger logger = LoggerFactory.getLogger(FileController.class);

  private final FileStorageService fileStorageService;
  private final AudioService audioService;
  private final FileStorageProperties fileStorageProperties;

  @Autowired
  public FileController(FileStorageService fileStorageService, AudioService audioService, FileStorageProperties fileStorageProperties) {
    this.fileStorageService = fileStorageService;
    this.audioService = audioService;
    this.fileStorageProperties = fileStorageProperties;
  }

  /**
   * 根据用户的账号来上传头像
   * @param file 头像
   * @param userAccount 用户的账号
   * @return
   */
    @PostMapping("/uploadFileByAccount")
    public UploadFileResponse uploadFileByAccount(@RequestParam("file") MultipartFile file,@RequestParam("account") String userAccount) {
        System.out.println("account: "+userAccount);
        String fileName = fileStorageService.uploadHeadIcon(file,userAccount);
        System.out.println("fileName" + fileName + "=========");
        String fileDownloadUri = fileStorageProperties.getIpAddress() + fileName;
        String name = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
        return new UploadFileResponse(name.substring(name.indexOf('-') + 1), fileDownloadUri, file.getContentType(), file.getSize());
    }
    
    @PostMapping("/uploadFile")
  public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
    String fileName = fileStorageService.storeFile(file);
    System.out.println("fileName" + fileName + "=========");
    String fileDownloadUri = fileStorageProperties.getIpAddress() + fileName;
    String name = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
    return new UploadFileResponse(name.substring(name.indexOf('-') + 1), fileDownloadUri, file.getContentType(), file.getSize());
  }

  @PostMapping("/uploadAudioFile")
  public Object uploadAudioFile(@RequestParam("file") MultipartFile file) throws Exception {
    String result = audioService.main(file);
    return JSONObject.parse(result);
  }

  @PostMapping("/uploadMultipleFiles")
  public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
    return Arrays.asList(files)
            .stream()
            .map(file -> uploadFile(file))
            .collect(Collectors.toList());
  }

  @GetMapping("/downloadFile/{fileName:.+}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
    // Load file as Resource
    Resource resource = fileStorageService.loadFileAsResource(fileName);

    // Try to determine file's content type
    String contentType = null;
    try {
      contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    } catch (IOException ex) {
      logger.info("Could not determine file type.");
    }

    // Fallback to the default content type if type could not be determined
    if(contentType == null) {
      contentType = "application/octet-stream";
    }

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
  }
}
