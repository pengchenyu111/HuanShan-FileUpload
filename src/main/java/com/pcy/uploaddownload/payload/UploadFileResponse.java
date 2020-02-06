package com.pcy.uploaddownload.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileResponse {
  private String fileName;
  private String fileUri;
  private String fileType;
  private long size;
}