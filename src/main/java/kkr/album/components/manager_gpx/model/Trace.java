package kkr.album.components.manager_gpx.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trace implements Cloneable {
	private String name;
	private Date time;
	private String color;
	private List<Point> points = new ArrayList<Point>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	protected Object clone() {
		Trace trace = new Trace();
		trace.color = color;
		trace.name = name;
		trace.time = time;
		if (points != null) {
			for (Point point : points) {
				trace.points.add((Point) point.clone());
			}
		}
		return trace;
	}
}
