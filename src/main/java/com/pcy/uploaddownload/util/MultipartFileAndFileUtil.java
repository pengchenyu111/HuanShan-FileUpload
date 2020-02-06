package com.pcy.uploaddownload.util;


import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;

public class MultipartFileAndFileUtil {
  /**
   * MultipartFile 转 File
   * @param file
   * @throws Exception
   */
  public static File multipartFileToFile(MultipartFile file) throws Exception {
    File toFile = null;
    if(file.equals("")||file.getSize()<=0){
      file = null;
    }else {
      InputStream ins = null;
      ins = file.getInputStream();
      toFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
      inputStreamToFile(ins, toFile);
      ins.close();
    }
    return toFile;
  }

  /**
   * File 转 MultipartFile
   * @param file
   * @throws Exception
   */
  public static MultipartFile fileToMultipartFile(File file ) throws Exception {

    FileInputStream fileInput = new FileInputStream(file);
    MultipartFile toMultipartFile = new MockMultipartFile("file",file.getName(),"text/plain", IOUtils.toByteArray(fileInput));
    toMultipartFile.getInputStream();
    return toMultipartFile;
  }


  /**
   * 将流写入文件
   * @param ins 输入流
   * @param file 目标文件
   */
  public static void inputStreamToFile(InputStream ins, File file) {
    try {
      OutputStream os = new FileOutputStream(file);
      int bytesRead = 0;
      byte[] buffer = new byte[8192];
      while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
        os.write(buffer, 0, bytesRead);
      }
      os.close();
      ins.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
