package kkr.album.components.manager_graph.data;

public class LinePosition {

	private PointPosition point1;
	private PointPosition point2;

	public LinePosition(PointPosition point1, PointPosition point2) {
		super();
		this.point1 = point1;
		this.point2 = point2;
	}

	public PointPosition getPoint1() {
		return point1;
	}

	public PointPosition getPoint2() {
		return point2;
	}

	public String toString() {
		return point1 + "-" + point2;
	}
}
