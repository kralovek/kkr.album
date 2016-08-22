package kkr.album.components.manager_graph.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;

import kkr.album.components.manager_graph.utils.UtilsGraph;

public class ImageData implements Constants {

	private BufferedImage image;
	private Graphics2D graphics2d;
	private FontRenderContext fontRenderContext;

	private String title;

	private AxisData axisX;
	private AxisData axisY1;
	private AxisData axisY2;

	public ImageData(BufferedImage image, //
			String title, String xName, String y1Name, String y2Name, //
			Color colorX, Color colorY1, Color colorY2, //
			double xValMin, double xValMax, //
			double y1ValMin, double y1ValMax, //
			Double y2ValMin, Double y2ValMax) {

		this.image = image;
		this.graphics2d = image.createGraphics();
		this.fontRenderContext = this.graphics2d.getFontRenderContext();

		////////////////////////////////////////
		// TITLES
		//
		this.title = title;

		////////////////////////////////////////
		// VALUES
		//

		//
		// X
		//
		{
			double step = (xValMax - xValMin) / (double) Constants.COUNT_POINTS_X;
			if (step == 0.) {
				throw new IllegalStateException("Step may not be 0");
			}
			step = UtilsGraph.adaptStep(step);
			PointPosition pointPosition = UtilsGraph.labelSize(fontRenderContext, xValMin, xValMax, step);
			axisX = new AxisData(xName, colorX, AxisType.X, xValMin, xValMax, step, pointPosition);
		}

		//
		// Y1
		//
		{
			double step = (y1ValMax - y1ValMin) / (double) Constants.COUNT_POINTS_Y;
			if (step == 0.) {
				throw new IllegalStateException("Step may not be 0");
			}
			step = UtilsGraph.adaptStep(step);
			PointPosition pointPosition = UtilsGraph.labelSize(fontRenderContext, y1ValMin, y1ValMax, step);
			axisY1 = new AxisData(y1Name, colorY1, AxisType.Y, y1ValMin, y1ValMax, step, pointPosition);
		}

		//
		// Y2
		//
		if (y2Name != null) {
			double step = (y2ValMax - y2ValMin) / (double) Constants.COUNT_POINTS_Y;
			if (step == 0.) {
				throw new IllegalStateException("Step may not be 0");
			}
			step = UtilsGraph.adaptStep(step);
			PointPosition pointPosition = UtilsGraph.labelSize(fontRenderContext, y2ValMin, y2ValMax, step);
			axisY2 = new AxisData(y2Name, colorY2, AxisType.Y, y2ValMin, y2ValMax, step, pointPosition);
		}

		////////////////////////////////////////
		// BORDERS
		//

		int xPosMin = BORDER_SPACE //
				+ axisY1.getLabel().getX() //
				+ BORDER_SPACE //
				+ AXIS_COMMA_LENGTH;

		int xPosMax = image.getWidth() - (0 //
				+ AXIS_COMMA_LENGTH //
				+ BORDER_SPACE //
				+ (axisY2 != null ? axisY2.getLabel().getX() : 10) //
				+ BORDER_SPACE);

		int yPosMin = BORDER_SPACE //
				+ (int) FONT_TITLE_AXIS.getStringBounds(xName, fontRenderContext).getHeight() //
				+ BORDER_SPACE //
				+ axisX.getLabel().getY() //
				+ BORDER_SPACE //
				+ AXIS_COMMA_LENGTH;

		int yPosMax = image.getHeight() - (0 //
				+ BORDER_SPACE //
				+ (int) FONT_TITLE_AXIS.getStringBounds(y1Name + y2Name, fontRenderContext).getHeight() //
				+ BORDER_SPACE //
				+ (int) FONT_TITLE_GRAPH.getStringBounds(title, fontRenderContext).getHeight() //
				+ BORDER_SPACE);

		PointPosition pointXMinYMin = new PointPosition(xPosMin, yPosMin);
		PointPosition pointXMaxYMin = new PointPosition(xPosMax, yPosMin);
		PointPosition pointXMinYMax = new PointPosition(xPosMin, yPosMax);
		PointPosition pointXMaxYMax = new PointPosition(xPosMax, yPosMax);

		axisX.setPosMin(pointXMinYMin);
		axisX.setPosMax(pointXMaxYMin);

		axisY1.setPosMin(pointXMinYMin);
		axisY1.setPosMax(pointXMinYMax);

		if (axisY2 != null) {
			axisY2.setPosMin(pointXMaxYMin);
			axisY2.setPosMax(pointXMaxYMax);
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	public Graphics2D getGraphics() {
		return graphics2d;
	}

	public FontRenderContext getFontRenderContext() {
		return fontRenderContext;
	}

	public String getTitle() {
		return title;
	}

	public AxisData getAxisX() {
		return axisX;
	}

	public AxisData getAxisY1() {
		return axisY1;
	}

	public AxisData getAxisY2() {
		return axisY2;
	}
}
