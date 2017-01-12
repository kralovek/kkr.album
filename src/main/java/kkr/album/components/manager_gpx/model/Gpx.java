package kkr.album.components.manager_gpx.model;

import java.util.ArrayList;
import java.util.List;

import kkr.album.components.manager_gpx.XmlReader;
import kkr.album.model.DateNZ;

public class Gpx implements Cloneable {
	private DateNZ time;
	private List<XmlReader.Attribute> attributes = new ArrayList<XmlReader.Attribute>();
	private List<Trace> traces = new ArrayList<Trace>();

	public DateNZ getTime() {
		return time;
	}

	public void setTime(DateNZ time) {
		this.time = time;
	}

	public List<Trace> getTraces() {
		return traces;
	}

	public void setTraces(List<Trace> traces) {
		this.traces = traces;
	}

	public List<XmlReader.Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<XmlReader.Attribute> attributes) {
		this.attributes = attributes;
	}

	public Object clone() {
		Gpx gpx = new Gpx();
		gpx.time = time;
		if (attributes != null) {
			for (XmlReader.Attribute attribute : attributes) {
				gpx.attributes.add((XmlReader.Attribute) attribute.clone());
			}
		}
		if (traces != null) {
			for (Trace trace : traces) {
				gpx.traces.add((Trace) trace.clone());
			}
		}
		return gpx;
	}
}
