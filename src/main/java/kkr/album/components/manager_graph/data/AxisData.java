package kkr.album.components.manager_graph.data;

import java.awt.Color;

public class AxisData {
	private String name;
	private Color color;

	private double valueMin;
	private double valueMax;
	private double step;

	private PointPosition label;
	private PointPosition posMin;
	private PointPosition posMax;

	private AxisType type;

	public AxisData(String name, Color color, AxisType type, double valueMin, double valueMax, double step,
			PointPosition label) {
		this.name = name;
		this.type = type;
		this.color = color != null ? color : Color.BLACK;

		this.valueMin = valueMin;
		this.valueMax = valueMax;

		this.step = step;

		this.label = label;
	}

	public String getName() {
		return name;
	}

	public double getValueMin() {
		return valueMin;
	}

	public double getValueMax() {
		return valueMax;
	}

	public double getStep() {
		return step;
	}

	public PointPosition getLabel() {
		return label;
	}

	public PointPosition getPosMin() {
		return posMin;
	}

	public void setPosMin(PointPosition min) {
		this.posMin = min;
	}

	public PointPosition getPosMax() {
		return posMax;
	}

	public void setPosMax(PointPosition max) {
		this.posMax = max;
	}

	public AxisType getType() {
		return type;
	}

	public Color getColor() {
		return color;
	}
}
