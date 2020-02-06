package com.pcy.uploaddownload.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResultDto {

	private int ok;
	private int err_no;
	private String failed;
	private String data;

}