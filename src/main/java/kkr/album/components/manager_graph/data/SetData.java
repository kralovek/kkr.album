package kkr.album.components.manager_graph.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

public class SetData {

	private Collection<PointData> points = new ArrayList<PointData>();
	private Color colorFill = Color.GRAY;
	private Color colorLine = Color.BLACK;
	private String title;
	private Double minExplicit;
	private Double maxExplicit;

	public SetData(Collection<PointData> points, String title) {
		super();
		this.points = points;
		this.title = title;
	}

	public Collection<PointData> getPoints() {
		return points;
	}

	public void setColorFill(Color colorFill) {
		this.colorFill = colorFill;
	}

	public void setColorLine(Color colorLine) {
		this.colorLine = colorLine;
	}

	public Color getColorFill() {
		return colorFill;
	}

	public Color getColorLine() {
		return colorLine;
	}

	public String getTitle() {
		return title;
	}

	public Double getMinExplicit() {
		return minExplicit;
	}

	public void setMinExplicit(Double minExplicit) {
		this.minExplicit = minExplicit;
	}

	public Double getMaxExplicit() {
		return maxExplicit;
	}

	public void setMaxExplicit(Double maxExplicit) {
		this.maxExplicit = maxExplicit;
	}
}
