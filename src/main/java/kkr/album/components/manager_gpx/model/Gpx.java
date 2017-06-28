package kkr.album.components.manager_gpx.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import kkr.album.components.manager_gpx.Attribute;
import kkr.album.model.DateNZ;

public class Gpx implements Cloneable {
	private DateNZ time;
	private Collection<Attribute> attributes = new LinkedHashSet<Attribute>();
	private Collection<Trace> traces = new ArrayList<Trace>();

	public DateNZ getTime() {
		return time;
	}

	public void setTime(DateNZ time) {
		this.time = time;
	}

	public Collection<Trace> getTraces() {
		return traces;
	}

	public Collection<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public Object clone() {
		Gpx gpx = new Gpx();
		gpx.time = time;
		if (attributes != null) {
			for (Attribute attribute : attributes) {
				gpx.attributes.add((Attribute) attribute.clone());
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
