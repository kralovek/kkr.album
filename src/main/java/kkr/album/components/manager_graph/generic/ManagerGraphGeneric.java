package kkr.album.components.manager_graph.generic;

import java.awt.Color;
import java.io.File;

import org.apache.log4j.Logger;

import kkr.album.components.manager_graph.ManagerGraph;
import kkr.album.components.manager_graph.data.ImageData;
import kkr.album.components.manager_graph.data.LineData;
import kkr.album.components.manager_graph.data.PointData;
import kkr.album.components.manager_graph.data.SetData;
import kkr.album.components.manager_graph.utils.UtilsGraph;
import kkr.album.exception.BaseException;

public class ManagerGraphGeneric extends ManagerGraphGenericFwk implements ManagerGraph {
	private static final Logger LOG = Logger.getLogger(ManagerGraphGeneric.class);

	public void createGraph(File file, int width, int height, String titleGraph, String titleX, SetData setData1,
			SetData setData2) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			LineData lineDataExact1 = UtilsGraph.minMax(setData1.getPoints());
			LineData lineData1 = adaptMargeY(setData1, lineDataExact1);

			LineData lineDataExact2 = UtilsGraph.minMax(setData2.getPoints());
			LineData lineData2 = adaptMargeY(setData2, lineDataExact2);

			double minX = min(lineData1.getPoint1().getX(), lineData2.getPoint1().getX());
			double maxX = max(lineData1.getPoint2().getX(), lineData2.getPoint2().getX());

			if (setData1.getMinExplicit() != null) {

			}

			ImageData imageData = WorkImage.createImage( //
					width, height, //
					titleGraph, titleX, setData1.getTitle(), setData2.getTitle(), //
					Color.BLACK, setData1.getColorLine(), setData2.getColorLine(), //
					minX, maxX, //
					lineData1.getPoint1().getY(), lineData1.getPoint2().getY(), //
					lineData2.getPoint1().getY(), lineData2.getPoint2().getY());

			if (setData1.getColorFill() != null) {
				WorkData.writeDataFill(imageData, imageData.getAxisY1(), setData1.getPoints(), setData1.getColorFill());
			}

			if (setData2.getColorFill() != null) {
				WorkData.writeDataFill(imageData, imageData.getAxisY2(), setData2.getPoints(), setData2.getColorFill());
			}

			WorkStructure.writeStructure(imageData);

			if (setData1.getColorLine() != null) {
				WorkData.writeDataLine(imageData, imageData.getAxisY1(), setData1.getPoints(), setData1.getColorLine());
			}

			if (setData2.getColorLine() != null) {
				WorkData.writeDataLine(imageData, imageData.getAxisY2(), setData2.getPoints(), setData2.getColorLine());
			}

			WorkImage.saveImage(imageData, file);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private double min(double value1, double value2) {
		return value1 < value2 ? value1 : value2;
	}

	private double max(double value1, double value2) {
		return value1 > value2 ? value1 : value2;
	}

	private LineData adaptMargeY(SetData setData, LineData lineData) {
		double delta = lineData.getPoint2().getY() - lineData.getPoint1().getY();

		double x1 = lineData.getPoint1().getX();
		double y1 = lineData.getPoint1().getY() - delta / 15;
		double x2 = lineData.getPoint2().getX();
		double y2 = lineData.getPoint2().getY() + delta / 15;

		if (setData.getMinExplicit() != null) {
			y1 = min(y1, setData.getMinExplicit());
		}
		if (setData.getMaxExplicit() != null) {
			y2 = max(y2, setData.getMaxExplicit());
		}

		LineData retval = new LineData( //
				new PointData(x1, y1), //
				new PointData(x2, y2));
		return retval;
	}

	public void createGraph(File file, int width, int height, String titleGraph, String titleX, SetData setData)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			LineData lineDataExact = UtilsGraph.minMax(setData.getPoints());
			LineData lineData = adaptMargeY(setData, lineDataExact);

			ImageData imageData = WorkImage.createImage( //
					width, height, //
					titleGraph, titleX, setData.getTitle(), null, //
					Color.BLACK, setData.getColorLine(), null, //
					lineData.getPoint1().getX(), lineData.getPoint2().getX(), //
					lineData.getPoint1().getY(), lineData.getPoint2().getY(), //
					null, null);

			if (setData.getColorFill() != null) {
				WorkData.writeDataFill(imageData, imageData.getAxisY1(), setData.getPoints(), setData.getColorFill());
			}

			WorkStructure.writeStructure(imageData);

			if (setData.getColorLine() != null) {
				WorkData.writeDataLine(imageData, imageData.getAxisY1(), setData.getPoints(), setData.getColorLine());
			}

			WorkImage.saveImage(imageData, file);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
