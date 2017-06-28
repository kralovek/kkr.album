package kkr.album.components.manager_gpx;

public class Attribute implements Cloneable, Comparable<Attribute> {
	private String prefix = null;
	private String name = null;
	private String value = null;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Object clone() {
		Attribute attribute = new Attribute();
		attribute.name = name;
		attribute.prefix = prefix;
		attribute.value = value;
		return attribute;
	}

	public int hash() {
		int hash = ((prefix != null ? prefix + ":" : "") + name).hashCode();
		return hash;
	}

	public int compareTo(Attribute attribute) {
		if (prefix != null && attribute.prefix != null) {
			int result = prefix.compareTo(attribute.prefix);
			if (result != 0) {
				return result;
			}
		} else if (prefix != null) {
			return -1;
		} else if (attribute.prefix != null) {
			return +1;
		} else if (name != null && attribute.name != null) {
			int result = name.compareTo(attribute.name);
			if (result != 0) {
				return result;
			}
		} else if (name != null) {
			return -1;
		} else if (attribute.name != null) {
			return +1;
		}
		return 0;
	}

	public boolean equals(Object object) {
		return compareTo((Attribute) object) == 0;
	}

	public String toString() {
		return (prefix != null ? prefix + ":" : "") + name + "=" + value;
	}

}