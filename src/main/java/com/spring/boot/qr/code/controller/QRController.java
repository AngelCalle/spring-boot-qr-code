package com.spring.boot.qr.code.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;
import com.spring.boot.qr.code.service.IQRService;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class QRController {
	
	@Autowired
	private HttpServletResponse httpServletResponse;

	@Autowired
	private IQRService qRService;

	/**
	 * Generate ordinary QR code according to String.
	 * http://localhost:8080/createQR?url=http://www.caramelo.com
	 */
	@RequestMapping(value = "/createQR")
	public void createQR(String url) {
		try {
			qRService.createQR(httpServletResponse, url);
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Example with ResponseEntity <Resource> response for open api.
	 * http://localhost:8080/createQRAPi?code=coded
	 */
	@GetMapping(value = "/createQRAPi", produces = { "image/png", "application/problem+json; charset=utf-8" })
	public ResponseEntity<Resource> createQRAPi(
			@NotNull @Pattern(regexp = "^[A-Za-z0-9-_.]{3,50}") @Size(min = 3, max = 50) @Valid String code) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		try {
			qRService.createQR(httpServletResponse, code);
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
		Resource resource = (Resource) httpServletResponse;
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}

	/**
	 * Generate QR code with logo according to String.
	 * http://localhost:8080/createQRLogo?url=http://www.caramelo.com
	 */
	@RequestMapping(value = "/createQRLogo")
	public void createQRLogo(String url) {
		try {
			qRService.createQRLogo(httpServletResponse, url);
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate and save in the default route a QR code according to the String and
	 * some measurements. http://localhost:8080/saveQR/caramelo/500/500
	 */
	@GetMapping(value = "/saveQR/{codeText}/{width}/{height}")
	public void saveQR(@PathVariable("codeText") String codeText, @PathVariable("width") Integer width,
			@PathVariable("height") Integer height) {
		try {
			qRService.saveQR(codeText, width, height);
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates and returns in byte format a QR code according to the String and
	 * some measurements http://localhost:8080/downloadRQ/caramelo/500/500
	 */
	@GetMapping(value = "/downloadRQ/{codeText}/{width}/{height}")
	public ResponseEntity<byte[]> downloadRQ(@PathVariable("codeText") String codeText,
			@PathVariable("width") Integer width, @PathVariable("height") Integer height) {
		byte[] qr;
		try {
			qr = qRService.downloadRQ(codeText, width, height);
			return ResponseEntity.status(HttpStatus.OK).body(qr);
		} catch (WriterException | IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
	}

}
