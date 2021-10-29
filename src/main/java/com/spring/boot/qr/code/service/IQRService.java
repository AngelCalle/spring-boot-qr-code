package com.spring.boot.qr.code.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.google.zxing.WriterException;

public interface IQRService {

	 public void createQR(HttpServletResponse httpServletResponse, String url) throws WriterException, IOException;
	 
	 public void createQRLogo(HttpServletResponse httpServletResponse, String url) throws WriterException, IOException;
	 
	 public byte[] downloadRQ(String codeText, Integer width, Integer height) throws WriterException, IOException;
	 
	 public void saveQR(String codeText, Integer width, Integer height) throws WriterException, IOException;
	 
}
