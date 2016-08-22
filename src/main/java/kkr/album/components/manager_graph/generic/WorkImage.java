package kkr.album.components.manager_graph.generic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import kkr.album.components.manager_graph.data.Constants;
import kkr.album.components.manager_graph.data.ImageData;
import kkr.album.exception.BaseException;
import kkr.album.exception.TechnicalException;

public class WorkImage implements Constants {
	private static final Logger LOG = Logger.getLogger(WorkStructure.class);

	public static ImageData createImage( //
			int width, int height, //
			String titleGraph, String titleX, String titleY1, String titleY2, //
			Color colorX, Color colorY1, Color colorY2, //
			double valueMinX, double valueMaxX, //
			double valueMinY1, double valueMaxY1, //
			Double valueMinY2, Double valueMaxY2 //
	) throws BaseException {
		LOG.trace("BEGIN");
		try {
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			ImageData imageData = new ImageData(bufferedImage, //
					titleGraph, titleX, titleY1, titleY2, //
					colorX, colorY1, colorY2, //
					valueMinX, valueMaxX, valueMinY1, valueMaxY1, valueMinY2, valueMaxY2);

			imageData.getGraphics().setBackground(Color.WHITE);
			imageData.getGraphics().clearRect(0, 0, imageData.getImage().getWidth(), imageData.getImage().getHeight());

			LOG.trace("OK");
			return imageData;
		} finally {
			LOG.trace("END");
		}
	}

	public static void saveImage(ImageData imageData, File file) throws BaseException {
		LOG.trace("BEGIN");
		try {
			try {
				ImageIO.write(imageData.getImage(), "jpeg", file);
			} catch (IOException ex) {
				throw new TechnicalException("Cannot create the image: " + file.getAbsolutePath());
			}
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
