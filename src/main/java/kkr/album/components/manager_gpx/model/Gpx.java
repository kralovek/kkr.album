package kkr.album.components.manager_gpx.model;

import kkr.album.components.manager_gpx.XmlReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Gpx implements Cloneable {
	private Date time;
	private List<XmlReader.Attribute> attributes = new ArrayList<XmlReader.Attribute>();
	private List<Trace> traces = new ArrayList<Trace>();

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
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
				attributes.add((XmlReader.Attribute) attribute.clone()); 
			}
		}
		if (traces != null) {
			for (Trace trace : traces) {
				traces.add((Trace) trace.clone());
			}
		}
		return gpx;
	}
}
