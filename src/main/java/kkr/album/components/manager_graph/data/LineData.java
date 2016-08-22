package kkr.album.components.manager_graph.data;

public class LineData {

	private PointData point1;
	private PointData point2;

	public LineData(PointData point1, PointData point2) {
		super();
		this.point1 = point1;
		this.point2 = point2;
	}

	public PointData getPoint1() {
		return point1;
	}

	public PointData getPoint2() {
		return point2;
	}

	public String toString() {
		return point1 + "-" + point2;
	}
}
