package kkr.album.components.manager_graph.generic;

import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import kkr.album.components.manager_graph.data.AxisData;
import kkr.album.components.manager_graph.data.ImageData;
import kkr.album.components.manager_graph.data.LineData;
import kkr.album.components.manager_graph.data.LinePosition;
import kkr.album.components.manager_graph.data.PointData;
import kkr.album.components.manager_graph.data.PointPosition;
import kkr.album.components.manager_graph.utils.AdaptPoint;
import kkr.album.components.manager_graph.utils.UtilsGraph;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;

public class WorkData {
	private static final Logger LOG = Logger.getLogger(WorkData.class);

	public static void writeDataLine(ImageData imageData, AxisData axisDataY, Collection<PointData> pointDatas,
			Color color) {
		LOG.trace("BEGIN");
		try {
			Collection<LineData> lines = pointsToLines(imageData.getAxisX(), axisDataY, pointDatas);

			imageData.getGraphics().setColor(color);
			for (LineData lineData : lines) {
				writeLine(imageData, axisDataY, lineData);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public static void writeDataFill(ImageData imageData, AxisData axisDataY, Collection<PointData> pointDatas,
			Color color) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<LineData> lines = pointsToLines(imageData.getAxisX(), axisDataY, pointDatas);

			Polygon polygon = new Polygon();

			//
			// FIRST LAST
			//
			PointData pointDataFirst = null;
			PointData pointDataLast = null;
			for (LineData lineData : lines) {
				if (pointDataFirst == null) {
					pointDataFirst = lineData.getPoint1();
				}
				pointDataLast = lineData.getPoint2();
			}

			if (pointDataFirst == null) {
				throw new FunctionalException("No data");
			}

			//
			// FIRST
			//
			pointDataFirst = new PointData(pointDataFirst.getX(), axisDataY.getValueMin());
			PointPosition pointPositionFirst = UtilsGraph.pointDataToPosition(imageData.getAxisX(), axisDataY,
					pointDataFirst);
			polygon.addPoint( //
					AdaptPoint.x(imageData, pointPositionFirst.getX()), //
					AdaptPoint.y(imageData, pointPositionFirst.getY()));

			//
			// DATA
			//
			for (LineData lineData : lines) {
				LinePosition linePosition = UtilsGraph.lineDataToPosition(imageData.getAxisX(), axisDataY, lineData);

				polygon.addPoint( //
						AdaptPoint.x(imageData, linePosition.getPoint1().getX()), //
						AdaptPoint.y(imageData, linePosition.getPoint1().getY()));
				polygon.addPoint( //
						AdaptPoint.x(imageData, linePosition.getPoint2().getX()), //
						AdaptPoint.y(imageData, linePosition.getPoint2().getY()));
			}

			//
			// LAST FIRST
			//
			pointDataLast = new PointData(pointDataLast.getX(), axisDataY.getValueMin());
			PointPosition pointPositionLast = UtilsGraph.pointDataToPosition(imageData.getAxisX(), axisDataY,
					pointDataLast);
			polygon.addPoint( //
					AdaptPoint.x(imageData, pointPositionLast.getX()), //
					AdaptPoint.y(imageData, pointPositionLast.getY()));
			polygon.addPoint( //
					AdaptPoint.x(imageData, pointPositionFirst.getX()), //
					AdaptPoint.y(imageData, pointPositionFirst.getY()));

			//
			// WRITE
			//
			imageData.getGraphics().setColor(color);
			imageData.getGraphics().fillPolygon(polygon);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private static Collection<LineData> pointsToLines(AxisData axisX, AxisData axisY, Collection<PointData> points) {
		LOG.trace("BEGIN");
		try {
			Collection<LineData> lines = new ArrayList<LineData>();

			PointData pointDataLast = null;
			for (PointData pointData : points) {
				if (pointDataLast != null) {
					LineData linePosition = pointsToLine(axisX, axisY, pointDataLast, pointData);
					if (linePosition != null) {
						lines.add(linePosition);
					}
				}
				pointDataLast = pointData;
			}
			LOG.trace("OK");
			return lines;
		} finally {
			LOG.trace("END");
		}
	}

	private static void writeLine(ImageData imageData, AxisData axisDataY, LineData lineData) {
		LinePosition linePosition = UtilsGraph.lineDataToPosition(imageData.getAxisX(), axisDataY, lineData);

		imageData.getGraphics().drawLine( //
				AdaptPoint.x(imageData, linePosition.getPoint1().getX()), //
				AdaptPoint.y(imageData, linePosition.getPoint1().getY() - 1), //
				AdaptPoint.x(imageData, linePosition.getPoint2().getX()), //
				AdaptPoint.y(imageData, linePosition.getPoint2().getY() - 1));
		imageData.getGraphics().drawLine( //
				AdaptPoint.x(imageData, linePosition.getPoint1().getX()), //
				AdaptPoint.y(imageData, linePosition.getPoint1().getY()), //
				AdaptPoint.x(imageData, linePosition.getPoint2().getX()), //
				AdaptPoint.y(imageData, linePosition.getPoint2().getY()));
		imageData.getGraphics().drawLine( //
				AdaptPoint.x(imageData, linePosition.getPoint1().getX()), //
				AdaptPoint.y(imageData, linePosition.getPoint1().getY() + 1), //
				AdaptPoint.x(imageData, linePosition.getPoint2().getX()), //
				AdaptPoint.y(imageData, linePosition.getPoint2().getY() + 1));
	}

	private static LineData pointsToLine(AxisData axisX, AxisData axisY, PointData pointData1, PointData pointData2) {
		//
		// IGNOR
		//
		if (pointData1.getX() < axisX.getValueMin() && pointData2.getX() < axisX.getValueMin()) {
			return null;
		}
		if (pointData1.getX() > axisX.getValueMax() && pointData2.getX() > axisX.getValueMax()) {
			return null;
		}
		if (pointData1.getY() < axisY.getValueMin() && pointData2.getY() < axisY.getValueMin()) {
			return null;
		}
		if (pointData1.getY() > axisY.getValueMax() && pointData2.getY() > axisY.getValueMax()) {
			return null;
		}

		//
		// INTERPOLATE
		//
		{
			if (pointData1.getX() < axisX.getValueMin() && pointData2.getX() > axisX.getValueMin()) {
				double y = interpolation(pointData1.getX(), pointData2.getX(), pointData1.getY(), pointData2.getY(),
						axisX.getValueMin());
				pointData1 = new PointData(axisX.getValueMin(), y);
			}
			if (pointData1.getX() < axisX.getValueMax() && pointData2.getX() > axisX.getValueMax()) {
				double y = interpolation(pointData1.getX(), pointData2.getX(), pointData1.getY(), pointData2.getY(),
						axisX.getValueMax());
				pointData2 = new PointData(axisX.getValueMax(), y);
			}
			if (pointData1.getY() < axisY.getValueMin() && pointData2.getY() > axisY.getValueMin()) {
				double x = interpolation(pointData1.getY(), pointData2.getY(), pointData1.getX(), pointData2.getX(),
						axisY.getValueMin());
				pointData1 = new PointData(x, axisY.getValueMin());
			}
			if (pointData1.getY() < axisY.getValueMin() && pointData2.getY() > axisY.getValueMin()) {
				double x = interpolation(pointData1.getY(), pointData2.getY(), pointData1.getX(), pointData2.getX(),
						axisY.getValueMax());
				pointData2 = new PointData(x, axisY.getValueMax());
			}
		}

		//
		// NORMAL
		//
		{
			LineData retval = new LineData(pointData1, pointData2);
			return retval;
		}
	}

	private static double interpolation(double min1, double max1, double min2, double max2, double v1) {
		return min2 + (v1 - min1) * (max2 - min2) / (max1 - min1);
	}
}
