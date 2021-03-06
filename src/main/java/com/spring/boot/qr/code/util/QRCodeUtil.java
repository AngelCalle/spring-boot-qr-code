package com.spring.boot.qr.code.util;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumMap;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


public class QRCodeUtil {

	private static final String CHARSET = "utf-8";
	private static final String FORMAT_NAME = "JPG";
	private static final int QRCODE_SIZE = 300;
	private static final int WIDTH = 60;
	private static final int HEIGHT = 60;

	private QRCodeUtil() {
		throw new IllegalStateException("QRCodeUtil class");
	}

	private static BufferedImage createImage(String content, String imgPath, boolean needCompress)
			throws WriterException, IOException {

		EnumMap<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 1);

		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,
				hints);

		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		if (imgPath == null || "".equals(imgPath)) {
			return image;
		}
		// Insertar imagen
		QRCodeUtil.insertImage(image, imgPath, needCompress);
		return image;
	}

	private static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws IOException {
		File file = new File(imgPath);
		if (!file.exists()) {
			System.err.println("ERROOR: " + imgPath + "??El archivo no existe!");
			return;
		}
		Image src = ImageIO.read(new File(imgPath));
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		if (needCompress) { // Comprimir LOGO
			if (width > WIDTH) {
				width = WIDTH;
			}
			if (height > HEIGHT) {
				height = HEIGHT;
			}
			Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			g.drawImage(image, 0, 0, null); // Dibuja la imagen reducida
			g.dispose();
			src = image;
		}
		// insertar LOGO
		Graphics2D graph = source.createGraphics();
		int x = (QRCODE_SIZE - width) / 2;
		int y = (QRCODE_SIZE - height) / 2;
		graph.drawImage(src, x, y, width, height, null);
		Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
		graph.setStroke(new BasicStroke(3f));
		graph.draw(shape);
		graph.dispose();
	}

	public static void encode(String content, String imgPath, String destPath, boolean needCompress)
			throws IOException, WriterException {
		BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
		mkdirs(destPath);
		ImageIO.write(image, FORMAT_NAME, new File(destPath));
	}

	public static BufferedImage encode(String content, String imgPath, boolean needCompress)
			throws WriterException, IOException {
		return QRCodeUtil.createImage(content, imgPath, needCompress);
	}

	public static void mkdirs(String destPath) {
		File file = new File(destPath);
		// Cuando la carpeta no existe, mkdirs crear?? autom??ticamente directorios de
		// varios niveles, que es diferente de mkdir. (mkdir lanzar?? una excepci??n si el
		// directorio principal no existe)
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}

	public static void encode(String content, String imgPath, OutputStream output, boolean needCompress)
			throws WriterException, IOException {
		BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
		ImageIO.write(image, FORMAT_NAME, output);
	}

	public static void encode(String content, OutputStream output) throws WriterException, IOException {
		QRCodeUtil.encode(content, null, output, false);
	}

}
