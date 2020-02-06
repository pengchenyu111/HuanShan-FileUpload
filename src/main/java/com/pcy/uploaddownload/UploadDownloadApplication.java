package com.pcy.uploaddownload;

import com.pcy.uploaddownload.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ // 使使用 @ConfigurationProperties 注解的类生效
        FileStorageProperties.class
})
public class UploadDownloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadDownloadApplication.class, args);
    }

}
