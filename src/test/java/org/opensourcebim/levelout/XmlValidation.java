package org.opensourcebim.levelout;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class XmlValidation {
	public static void main(String[] args) throws SAXException, IOException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		File xsdFile = new File(args[1]);
		File[] xsds = xsdFile.isDirectory() ? xsdFile.listFiles((dir, name) -> name.endsWith("xsd")) : new File[]{xsdFile};
		Schema schema = factory.newSchema(Arrays.stream(xsds).map(StreamSource::new).toArray(StreamSource[]::new));
		Validator validator = schema.newValidator();
		File xmlFile = new File(args[0]);
		validator.validate(new StreamSource(xmlFile));
	}
}
