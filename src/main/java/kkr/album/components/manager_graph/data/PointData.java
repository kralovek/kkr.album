package kkr.album.components.manager_graph.data;

public class PointData {
	private double x;
	private double y;

	public PointData(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public String toString() {
		return "[" + x + "," + y + "]";
	}
}
