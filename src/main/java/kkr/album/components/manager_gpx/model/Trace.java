package kkr.album.components.manager_gpx.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
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

	public Collection<Point> getPoints() {
		return points;
	}

	public void sort() {
		Collections.sort(points);
		Point pointLast = null;

		Iterator<Point> iterator = points.iterator();
		while (iterator.hasNext()) {
			Point point = iterator.next();
			if (pointLast != null && pointLast.compareTo(point) == 0) {
				iterator.remove();
			}
			pointLast = point;
		}
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
