package kkr.album.components.manager_gpx;

import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.util.Collection;

import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;

public class XmlReader {
	protected XMLStreamReader xmlReader;

	public XmlReader(XMLStreamReader xmlReader) {
		this.xmlReader = xmlReader;
	}

	public static class Tag {
		protected String prefix = null;
		protected String name = null;

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
	}

	public static class StartTag extends Tag {
		private Attribute[] attributes = null;

		public Attribute[] getAttributes() {
			return attributes;
		}

		public void setAttributes(Attribute[] attributes) {
			this.attributes = attributes;
		}

		public String getAttribute(String name) {
			if (attributes != null) {
				for (int i = 0; i < attributes.length; i++) {
					if (attributes[i].getName().equals(name)) {
						return attributes[i].getValue();
					}
				}
			}
			return null;
		}
	}

	public static class Attribute {
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
	}

	protected StartTag readCompleteTag() throws BaseException,
			XMLStreamException {
		if (xmlReader.getEventType() != XMLStreamConstants.START_ELEMENT) {
			throw new FunctionalException("Start element expected");
		}

		StartTag retval = new StartTag();

		String prefix = xmlReader.getPrefix();
		retval.setPrefix(prefix.length() != 0 ? prefix : null);
		retval.setName(xmlReader.getLocalName());
		Collection<Attribute> attributes = new ArrayList<Attribute>();

		int namespaceCount = xmlReader.getNamespaceCount();
		if (namespaceCount != 0) {
			NamespaceContext namespaceContext = xmlReader.getNamespaceContext();
			for (int i = 0; i < namespaceCount; i++) {
				String namespacePrefix = xmlReader.getNamespacePrefix(i);
				String namespaceUri = xmlReader.getNamespaceURI(i);
				Attribute attribute = new Attribute();
				attribute.setValue(namespaceUri);
				if (namespacePrefix == null) {
					attribute.setName(XMLConstants.XMLNS_ATTRIBUTE);
				} else {
					attribute.setPrefix(XMLConstants.XMLNS_ATTRIBUTE);
					attribute.setName(namespacePrefix);
				}
				attributes.add(attribute);
			}
		}

		int attrubutesCount = xmlReader.getAttributeCount();
		for (int i = 0; i < attrubutesCount; i++) {
			String attributeName = xmlReader.getAttributeLocalName(i);
			String attributePrefix = xmlReader.getAttributePrefix(i);
			String attributeValue = xmlReader.getAttributeValue(i);
			Attribute attribute = new Attribute();
			if (attributePrefix != null && attributePrefix.length() != 0) {
				attribute.setPrefix(attributePrefix);
			}
			attribute.setName(attributeName);
			attribute.setValue(attributeValue);
			attributes.add(attribute);
		}
		retval.setAttributes(attributes.toArray(new Attribute[attributes.size()]));

		return retval;
	}

	protected Tag readTag() throws BaseException, XMLStreamException {
		if (xmlReader.getEventType() != XMLStreamConstants.START_ELEMENT
				&& xmlReader.getEventType() != XMLStreamConstants.END_ELEMENT) {
			throw new FunctionalException("Start element expected");
		}

		Tag retval = new Tag();

		String prefix = xmlReader.getPrefix();
		retval.setPrefix(prefix.length() != 0 ? prefix : null);
		retval.setName(xmlReader.getLocalName());

		return retval;
	}

	public String getTextValue() throws XMLStreamException {
		if (xmlReader.hasNext()) {
			xmlReader.next();
			switch (xmlReader.getEventType()) {
			case XMLStreamConstants.CDATA:
			case XMLStreamConstants.CHARACTERS: {
				String value = xmlReader.getText();
				return value;
			}
			case XMLStreamConstants.COMMENT:
			case XMLStreamConstants.ENTITY_REFERENCE:
			case XMLStreamConstants.DTD:
			case XMLStreamConstants.SPACE: {
				xmlReader.getText();
				return null;
			}
			default:
				return null;
			}
		} else {
			return null;
		}
	}

	protected boolean isEmpty(String value) {
		return value == null || "".equals(value);
	}
}
