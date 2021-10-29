package com.spring.boot.qr.code.service.impl;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.google.zxing.WriterException;
import com.spring.boot.qr.code.service.IQRService;
import com.spring.boot.qr.code.util.QRCodeGenerator;
import com.spring.boot.qr.code.util.QRCodeUtil;

@Service
public class QRServiceImpl implements IQRService {

	private static final String QR_CODE_IMAGE_PATH = "./src/main/resources/templates/QRCode.png";

	@Override
	public void createQR(HttpServletResponse httpServletResponse, String url) throws WriterException, IOException {
		ServletOutputStream stream = null;
		stream = httpServletResponse.getOutputStream();
		QRCodeUtil.encode(url, stream);
	}

	@Override
	public void createQRLogo(HttpServletResponse httpServletResponse, String url) throws WriterException, IOException {
		ServletOutputStream stream = null;

		stream = httpServletResponse.getOutputStream();
		String logoPath = Thread
				.currentThread()
				.getContextClassLoader()
				.getResource("")
				.getPath() + "templates" + File.separator + "logo.jpg";

		QRCodeUtil.encode(url, logoPath, stream, true);

		if (stream != null) {
			stream.flush();
			stream.close();
		}
	}

	@Override
	public byte[] downloadRQ(String codeText, Integer width, Integer height) throws WriterException, IOException {
		return QRCodeGenerator.getQRCodeImage(codeText, width, height);
	}

	@Override
	public void saveQR(String codeText, Integer width, Integer height) throws WriterException, IOException {
		QRCodeGenerator.generateQRCodeImage(codeText, width, height, QR_CODE_IMAGE_PATH);
	}

}
