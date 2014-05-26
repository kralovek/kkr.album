package kkr.album.components.manager_image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsFile;

import org.apache.log4j.Logger;
import org.imgscalr.Scalr;

public class ManagerImageImgscalr extends ManagerImageImgscalrFwk implements
		ManagerImage {
	private static final Logger LOGGER = Logger
			.getLogger(ManagerImageImgscalr.class);

	public void resize(File fileIn, File fileOut, int width)
			throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			FileInputStream fileInputStream = null;
			BufferedImage readImage = null;
			try {
				try {
					fileInputStream = new FileInputStream(fileIn);
					readImage = ImageIO.read(fileInputStream);
				} catch (FileNotFoundException ex) {
					throw new FunctionalException("File does not exist: "
							+ fileIn.getAbsolutePath(), ex);
				} catch (IOException ex) {
					throw new TechnicalException("Cannot read the file: "
							+ fileIn.getAbsolutePath(), ex);
				}

				BufferedImage thumbnail = Scalr.resize(readImage,
						Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 1600,
						1200, Scalr.OP_ANTIALIAS);

				try {
					ImageIO.write(thumbnail, "jpg", fileOut);
				} catch (IOException ex) {
					throw new TechnicalException("Cannot write the file: "
							+ fileOut.getAbsolutePath(), ex);
				}

				fileInputStream.close();
				fileInputStream = null;
			} catch (IOException ex) {
				throw new TechnicalException("Unexpected IO problem", ex);
			} finally {
				UtilsFile.closeRessource(fileInputStream);
			}
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
