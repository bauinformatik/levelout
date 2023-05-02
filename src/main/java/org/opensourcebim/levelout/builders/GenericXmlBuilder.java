package org.opensourcebim.levelout.builders;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlobjects.gml.model.common.GenericElement;
import org.xmlobjects.gml.util.GMLConstants;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GenericXmlBuilder {
	private final Document document;

	public GenericXmlBuilder() throws ParserConfigurationException {
		document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}

	public Node root(String tagName){
		return new Node(document.createElementNS(GMLConstants.GML_3_2_NAMESPACE, tagName));
	}

	public class Node{
		private final Element element;

		public Node(Element element){
			this.element = element;
		}

		public Node node(String tagName){
			Element element = document.createElementNS(GMLConstants.GML_3_2_NAMESPACE, tagName);
			this.element.appendChild(element);
			return new Node(element);
		}

		public Node attribute(String attribute, String value){
			// element.setAttributeNS(GMLConstants.GML_3_2_NAMESPACE, attribute, value);
			element.setAttribute( attribute, value); // namespace of element
			return this;
		}
		public Node text(String text) {
			element.setTextContent(text);
			return this;
		}

		public GenericElement generic() {
			return GenericElement.of(element);
		}
	}
}
