package kkr.album.components.manager_graph.utils;

import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import kkr.album.components.manager_graph.data.AxisData;
import kkr.album.components.manager_graph.data.Constants;
import kkr.album.components.manager_graph.data.LineData;
import kkr.album.components.manager_graph.data.LinePosition;
import kkr.album.components.manager_graph.data.PointData;
import kkr.album.components.manager_graph.data.PointPosition;

public class UtilsGraph implements Constants {

	public static PointPosition labelSize(FontRenderContext fontRenderContext, double min, double max, double step) {
		int widthMax = 0;
		int heightMax = 0;

		int n = (int) (min / step);
		if (((double) n) * step < min) {
			n++;
		}

		for (double nd = (double) n; nd * step <= max; nd += 1.) {
			double value = nd * step;
			String label = UtilsGraph.valueToLabel(value);
			Rectangle2D rectangle2d = FONT_LABEL_AXIS.getStringBounds(label, fontRenderContext);
			int width = (int) rectangle2d.getWidth();
			if (width > widthMax) {
				widthMax = width;
			}
			int height = (int) rectangle2d.getHeight();
			if (height > heightMax) {
				heightMax = height;
			}
		}

		PointPosition retval = new PointPosition(widthMax, heightMax);
		return retval;
	}

	public static String valueToLabel(double value) {
		String str = String.format("%f", value);
		String retval = str.replaceAll("[\\.,]0*$", "");
		return retval;
	}

	public static double adaptStep(double step) {
		String strStep = String.format("%f", step);

		String strStepRed = null;
		String strStepOne = null;
		Integer mainDigit = null;
		boolean point = false;
		for (int i = 0; i < strStep.length(); i++) {
			char c = strStep.charAt(i);
			if (Character.isDigit(c) && c != '0') {
				mainDigit = Integer.parseInt("" + c);
				if (point) {
					strStepRed = strStep.substring(0, i + 1);
					strStepOne = strStep.substring(0, i) + '1';
				} else {
					strStepRed = strStep.replaceAll("[,\\.].*", "");
					strStepRed = strStepRed.substring(0, 1) + strStepRed.substring(1).replaceAll("[0-9]", "0");
					strStepOne = '1' + strStepRed.substring(i + 1);
				}
				break;
			} else if (c == '.' || c == ',') {
				point = true;
			}
		}

		if (strStepRed == null) {
			return step;
		}

		strStepRed = strStepRed.replaceAll("[,\\.]", ".");
		strStepOne = strStepOne.replaceAll("[,\\.]", ".");

		double stepMin = Double.parseDouble(strStepRed);
		double stepDif = Double.parseDouble(strStepOne);
		double stepMax = stepMin + stepDif;

		if (mainDigit < 3) {
			double stepMid = (stepMin + stepMax) / 2.0;
			if (step < stepMid) {
				stepMax = stepMid;
			} else {
				stepMin = stepMid;
			}
		}

		if ((step - stepMin) < (stepMax - step)) {
			return stepMin;
		} else {
			return stepMax;
		}
	}

	public static LinePosition lineDataToPosition(AxisData axisDataX, AxisData axisDataY, LineData line) {
		PointPosition pointPosition1 = pointDataToPosition(axisDataX, axisDataY, line.getPoint1());
		PointPosition pointPosition2 = pointDataToPosition(axisDataX, axisDataY, line.getPoint2());
		LinePosition retval = new LinePosition(pointPosition1, pointPosition2);
		return retval;
	}

	public static PointPosition pointDataToPosition(AxisData axisDataX, AxisData axisDataY, PointData point) {
		int posX = UtilsGraph.valueDataToPosition(axisDataX, point.getX());
		int posY = UtilsGraph.valueDataToPosition(axisDataY, point.getY());
		PointPosition retval = new PointPosition(posX, posY);
		return retval;
	}

	public static int valueDataToPosition(AxisData axisData, double value) {
		double valueMin = 0.;
		double valueMax = 0.;
		double pointMin = 0.;
		double pointMax = 0.;
		switch (axisData.getType()) {
		case X:
			valueMin = axisData.getValueMin();
			valueMax = axisData.getValueMax();
			pointMin = axisData.getPosMin().getX();
			pointMax = axisData.getPosMax().getX();
			break;
		case Y:
			valueMin = axisData.getValueMin();
			valueMax = axisData.getValueMax();
			pointMin = axisData.getPosMin().getY();
			pointMax = axisData.getPosMax().getY();
			break;
		}

		double point = ((double) pointMin)
				+ ((double) (pointMax - pointMin)) * (value - valueMin) / (valueMax - valueMin);
		return (int) point;
	}

	public static LineData minMax(Collection<PointData> datas) {
		if (datas == null || datas.isEmpty()) {
			throw new IllegalStateException("No data");
		}
		Double minX = null;
		Double maxX = null;
		Double minY = null;
		Double maxY = null;

		for (PointData pointData : datas) {
			if (minX == null) {
				minX = pointData.getX();
			}
			if (maxX == null) {
				maxX = pointData.getX();
			}
			if (minY == null) {
				minY = pointData.getY();
			}
			if (maxY == null) {
				maxY = pointData.getY();
			}

			if (pointData.getX() < minX) {
				minX = pointData.getX();
			}
			if (pointData.getX() > maxX) {
				maxX = pointData.getX();
			}
			if (pointData.getY() < minY) {
				minY = pointData.getY();
			}
			if (pointData.getY() > maxY) {
				maxY = pointData.getY();
			}
		}

		LineData retval = new LineData(new PointData(minX, minY), new PointData(maxX, maxY));

		return retval;
	}
}
