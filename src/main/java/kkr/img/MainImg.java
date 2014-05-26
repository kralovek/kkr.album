package kkr.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

public class MainImg {

	public static final void main(String[] args) throws Exception {
		File fileIn = new File("h:/tmp/10/00035591o_20140517-074242.jpg");
		File fileOut = new File("h:/tmp/10/00035591n_20140517-074242.jpg");
		FileInputStream fileInputStream = new FileInputStream(fileIn);
		BufferedImage readImage = ImageIO.read(fileInputStream);
		BufferedImage thumbnail = Scalr.resize(readImage, Scalr.Method.SPEED,
				Scalr.Mode.FIT_TO_WIDTH, 1600, 1200, Scalr.OP_ANTIALIAS);
		
		ImageIO.write(thumbnail, "jpg", fileOut);
	}
}
