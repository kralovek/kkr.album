package kkr.album.components.manager_graph.generic;

import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

import kkr.album.components.manager_graph.data.Constants;
import kkr.album.components.manager_graph.data.ImageData;
import kkr.album.components.manager_graph.utils.AdaptPoint;
import kkr.album.components.manager_graph.utils.UtilsGraph;

public class WorkStructure implements Constants {
	private static final Logger LOG = Logger.getLogger(WorkStructure.class);

	private static final Color COLOR_GRID = new Color(201, 201, 201);

	public static void writeStructure(ImageData imageData) {
		LOG.trace("BEGIN");
		try {
			createTitles(imageData);

			createAxis(imageData);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private static void createTitles(ImageData imageData) {
		FontRenderContext fontRenderContext = imageData.getGraphics().getFontRenderContext();

		//
		// TITLE
		//
		{
			imageData.getGraphics().setColor(Color.BLUE);
			imageData.getGraphics().setFont(FONT_TITLE_GRAPH);
			Rectangle2D rectangle2dTitle = FONT_TITLE_GRAPH.getStringBounds(imageData.getTitle(), fontRenderContext);
			Rectangle2D rectangle2dLabel = FONT_TITLE_AXIS.getStringBounds("ABC", fontRenderContext);

			imageData.getGraphics().drawString( //
					imageData.getTitle(), //
					(imageData.getAxisX().getPosMin().getX() + imageData.getAxisX().getPosMax().getX()
							- (int) rectangle2dTitle.getWidth()) / 2, //
					AdaptPoint.y(imageData, imageData.getAxisY1().getPosMax().getY() + BORDER_SPACE
							+ (int) rectangle2dLabel.getHeight() + BORDER_SPACE));
		}
	}

	private static void createAxis(ImageData imageData) {
		LOG.trace("BEGIN");
		try {
			createAxisX(imageData);
			createAxisY1(imageData);
			if (imageData.getAxisY2() != null) {
				createAxisY2(imageData);
			} else {
				createBorderRight(imageData);
			}
			createBorderTop(imageData);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private static void createBorderRight(ImageData imageData) {
		imageData.getGraphics().setColor(Color.BLACK);
		imageData.getGraphics().drawLine( //
				AdaptPoint.x(imageData, imageData.getAxisX().getPosMax().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisY1().getPosMin().getY()), //
				AdaptPoint.x(imageData, imageData.getAxisX().getPosMax().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisY1().getPosMax().getY()));
	}

	private static void createBorderTop(ImageData imageData) {
		imageData.getGraphics().setColor(Color.BLACK);
		imageData.getGraphics().drawLine( //
				AdaptPoint.x(imageData, imageData.getAxisX().getPosMin().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisY1().getPosMax().getY()), //
				AdaptPoint.x(imageData, imageData.getAxisX().getPosMax().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisY1().getPosMax().getY()));
	}

	private static void createAxisX(ImageData imageData) {
		FontRenderContext fontRenderContext = imageData.getGraphics().getFontRenderContext();

		Rectangle2D rectangle2dTitle = FONT_TITLE_AXIS.getStringBounds(imageData.getAxisX().getName(),
				fontRenderContext);
		Rectangle2D rectangle2dLabel = FONT_LABEL_AXIS.getStringBounds("ABC", fontRenderContext);

		imageData.getGraphics().setColor(Color.BLACK);
		imageData.getGraphics().drawLine( //
				AdaptPoint.x(imageData, imageData.getAxisX().getPosMin().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisX().getPosMin().getY()), //
				AdaptPoint.x(imageData, imageData.getAxisX().getPosMax().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisX().getPosMax().getY()));

		imageData.getGraphics().setFont(FONT_TITLE_AXIS);
		imageData.getGraphics().setColor(imageData.getAxisX().getColor());
		imageData.getGraphics().drawString( //
				imageData.getAxisX().getName(), //
				AdaptPoint.x(imageData,
						(imageData.getAxisX().getPosMin().getX() + imageData.getAxisX().getPosMax().getX()
								- (int) rectangle2dTitle.getWidth()) / 2), //
				AdaptPoint.y(imageData, imageData.getAxisX().getPosMin().getY() - AXIS_COMMA_LENGTH - BORDER_SPACE
						- (int) rectangle2dLabel.getHeight() - BORDER_SPACE - (int) rectangle2dTitle.getHeight()));

		int n = (int) (imageData.getAxisX().getValueMin() / imageData.getAxisX().getStep());
		if (((double) n) * imageData.getAxisX().getStep() < imageData.getAxisX().getValueMin()) {
			n++;
		}

		imageData.getGraphics().setFont(FONT_LABEL_AXIS);
		for (double nd = (double) n; nd * imageData.getAxisX().getStep() <= imageData.getAxisX()
				.getValueMax(); nd += 1.) {
			double value = nd * imageData.getAxisX().getStep();
			int point = UtilsGraph.valueDataToPosition(imageData.getAxisX(), value);
			String label = UtilsGraph.valueToLabel(value);
			rectangle2dLabel = FONT_LABEL_AXIS.getStringBounds(label, fontRenderContext);
			imageData.getGraphics().setColor(Color.BLACK);
			imageData.getGraphics().drawLine( //
					AdaptPoint.x(imageData, point), //
					AdaptPoint.y(imageData, imageData.getAxisX().getPosMin().getY()), //
					AdaptPoint.x(imageData, point), //
					AdaptPoint.y(imageData, imageData.getAxisX().getPosMin().getY() - AXIS_COMMA_LENGTH));
			imageData.getGraphics().setColor(imageData.getAxisX().getColor());
			imageData.getGraphics().drawString( //
					label, //
					point - (int) rectangle2dLabel.getWidth() / 2, //
					AdaptPoint.y(imageData, imageData.getAxisX().getPosMin().getY() - AXIS_COMMA_LENGTH + BORDER_SPACE
							- (int) rectangle2dLabel.getHeight()));
		}
	}

	private static void createAxisY1(ImageData imageData) {
		FontRenderContext fontRenderContext = imageData.getGraphics().getFontRenderContext();

		int n = (int) (imageData.getAxisY1().getValueMin() / imageData.getAxisY1().getStep());
		if (((double) n) * imageData.getAxisY1().getStep() < imageData.getAxisY1().getValueMin()) {
			n++;
		}

		imageData.getGraphics().setColor(Color.BLACK);
		imageData.getGraphics().setFont(FONT_LABEL_AXIS);
		for (double nd = (double) n; nd * imageData.getAxisY1().getStep() <= imageData.getAxisY1()
				.getValueMax(); nd += 1.) {
			double value = nd * imageData.getAxisY1().getStep();
			int point = UtilsGraph.valueDataToPosition(imageData.getAxisY1(), value);
			String label = UtilsGraph.valueToLabel(value);
			Rectangle2D rectangle2dLabel = FONT_LABEL_AXIS.getStringBounds(label, fontRenderContext);
			imageData.getGraphics().setColor(Color.BLACK);
			imageData.getGraphics().drawLine( //
					AdaptPoint.x(imageData, imageData.getAxisY1().getPosMin().getX()), //
					AdaptPoint.y(imageData, point), //
					AdaptPoint.x(imageData, imageData.getAxisY1().getPosMin().getX() - AXIS_COMMA_LENGTH), //
					AdaptPoint.y(imageData, point));
			imageData.getGraphics().setColor(imageData.getAxisY1().getColor());
			imageData.getGraphics().drawString( //
					label, //
					AdaptPoint.x(imageData,
							imageData.getAxisY1().getPosMin().getX() - AXIS_COMMA_LENGTH - BORDER_SPACE
									- (int) rectangle2dLabel.getWidth()), //
					AdaptPoint.y(imageData, point - (int) rectangle2dLabel.getHeight() / 2));

			imageData.getGraphics().setColor(COLOR_GRID);
			imageData.getGraphics().drawLine( //
					AdaptPoint.x(imageData, imageData.getAxisX().getPosMin().getX()), //
					AdaptPoint.y(imageData, point), //
					AdaptPoint.x(imageData, imageData.getAxisX().getPosMax().getX()), //
					AdaptPoint.y(imageData, point));
		}

		imageData.getGraphics().setColor(Color.BLACK);
		imageData.getGraphics().drawLine( //
				AdaptPoint.x(imageData, imageData.getAxisY1().getPosMin().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisY1().getPosMin().getY()), //
				AdaptPoint.x(imageData, imageData.getAxisY1().getPosMax().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisY1().getPosMax().getY()));

		imageData.getGraphics().setColor(imageData.getAxisY1().getColor());
		imageData.getGraphics().setFont(FONT_TITLE_AXIS);
		imageData.getGraphics().drawString( //
				imageData.getAxisY1().getName(), //
				AdaptPoint.x(imageData, imageData.getAxisX().getPosMin().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisY1().getPosMax().getY() + BORDER_SPACE));
	}

	private static void createAxisY2(ImageData imageData) {
		FontRenderContext fontRenderContext = imageData.getGraphics().getFontRenderContext();

		Rectangle2D rectangle2dTitle = FONT_TITLE_AXIS.getStringBounds(imageData.getAxisY2().getName(),
				fontRenderContext);

		int n = (int) (imageData.getAxisY2().getValueMin() / imageData.getAxisY2().getStep());
		if (((double) n) * imageData.getAxisY2().getStep() < imageData.getAxisY2().getValueMin()) {
			n++;
		}

		imageData.getGraphics().setFont(FONT_LABEL_AXIS);
		for (double nd = (double) n; nd * imageData.getAxisY2().getStep() <= imageData.getAxisY2()
				.getValueMax(); nd += 1.) {
			double value = nd * imageData.getAxisY2().getStep();
			int point = UtilsGraph.valueDataToPosition(imageData.getAxisY2(), value);
			String label = UtilsGraph.valueToLabel(value);
			Rectangle2D rectangle2dLabel = FONT_LABEL_AXIS.getStringBounds(label, fontRenderContext);
			imageData.getGraphics().drawLine( //
					AdaptPoint.x(imageData, imageData.getAxisY2().getPosMin().getX()), //
					AdaptPoint.y(imageData, point), //
					AdaptPoint.x(imageData, imageData.getAxisY2().getPosMin().getX() + AXIS_COMMA_LENGTH), //
					AdaptPoint.y(imageData, point));
			imageData.getGraphics().setColor(imageData.getAxisY2().getColor());
			imageData.getGraphics()
					.drawString( //
							label, //
							AdaptPoint.x(imageData,
									imageData.getAxisY2().getPosMin().getX() + AXIS_COMMA_LENGTH + BORDER_SPACE
											+ (imageData.getAxisY2().getLabel().getX()
													- (int) rectangle2dLabel.getWidth())), //
					AdaptPoint.y(imageData, (point - (int) rectangle2dLabel.getHeight() / 2)));
		}

		imageData.getGraphics().setColor(Color.BLACK);
		imageData.getGraphics().drawLine( //
				AdaptPoint.x(imageData, imageData.getAxisY2().getPosMin().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisY2().getPosMin().getY()), //
				AdaptPoint.x(imageData, imageData.getAxisY2().getPosMax().getX()), //
				AdaptPoint.y(imageData, imageData.getAxisY2().getPosMax().getY()));

		imageData.getGraphics().setFont(FONT_TITLE_AXIS);
		imageData.getGraphics().setColor(imageData.getAxisY2().getColor());
		imageData.getGraphics().drawString( //
				imageData.getAxisY2().getName(), //
				AdaptPoint.x(imageData, imageData.getAxisX().getPosMax().getX() - (int) rectangle2dTitle.getWidth()), //
				AdaptPoint.y(imageData, imageData.getAxisY2().getPosMax().getY() + BORDER_SPACE));
	}
}
