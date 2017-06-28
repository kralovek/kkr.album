package kkr.album.components.manager_graph.data;

public class PointData {
	private double x;
	private Double y;

	public PointData(double x, Double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Double getY() {
		return y;
	}

	public String toString() {
		return "[" + x + "," + y + "]";
	}
}
