package com.pcy.uploaddownload.service;

import com.pcy.uploaddownload.exception.FileStorageException;
import com.pcy.uploaddownload.exception.MyFileNotFoundException;
import com.pcy.uploaddownload.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileStorageService {

  private Path fileStorageLocation;
  private final FileStorageProperties fileStorageProperties;

  public static final String USER = "user";
  public static final String HEAD_ICON = "headicon";

  @Autowired
  public FileStorageService(FileStorageProperties fileStorageProperties) {
    this.fileStorageProperties = fileStorageProperties;
    this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
    }
  }

  /**
   * 更新头像
   * @param file 头像文件
   * @param account 用户账号
   * @return
   */
  public String uploadHeadIcon(MultipartFile file,int account) {
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    try {
      if(fileName.contains("..")) {
        throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
      }
      Path fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + File.separator + USER + File.separator + account + File.separator + HEAD_ICON).toAbsolutePath().normalize();
      if (Files.notExists(fileStorageLocation)) {
        Files.createDirectories(fileStorageLocation);
      }
      Path targetLocation = fileStorageLocation.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      String location = targetLocation.toString();
      System.out.println(location);
      return location.substring("/usr/nginx/huangshan".length());
    } catch (Exception ex) {
      throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
    }
  }

  public String storeFile(MultipartFile file) {
    // Normalize file name
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    try {
      // Check if the file's name contains invalid characters
      if(fileName.contains("..")) {
        throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
      }
      fileName = new Date().getTime() + "-" +fileName;
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
      String date = df.format(new Date());
      Path fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + File.separator + date).toAbsolutePath().normalize();
      System.out.println(this.fileStorageProperties.getUploadDir());
      if (Files.notExists(fileStorageLocation)) {
        Files.createDirectories(fileStorageLocation);
      }
      // Copy file to the target location (Replacing existing file with the same name)
      Path targetLocation = fileStorageLocation.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      String location = targetLocation.toString();

      System.out.println("location: " + location + "=======");
      System.out.println(location.substring("/usr/nginx/huangshan".length()));
      return location.substring("/usr/nginx/huangshan".length());
    } catch (Exception ex) {
      throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
    }
  }

  public Resource loadFileAsResource(String fileName) {
    try {
      Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if(resource.exists()) {
        return resource;
      } else {
        throw new MyFileNotFoundException("File not found " + fileName);
      }
    } catch (MalformedURLException ex) {
      throw new MyFileNotFoundException("File not found " + fileName, ex);
    }
  }
}