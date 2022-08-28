package org.opensourcebim.levelout;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import net.opengis.gml.v_3_2_1.BoundingShapeType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceGeometryType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceMemberType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.EdgesType;
import net.opengis.indoorgml.core.v_1_0.IndoorFeaturesType;
import net.opengis.indoorgml.core.v_1_0.NodesType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesPropertyType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesType;
import net.opengis.indoorgml.core.v_1_0.StateMemberType;
import net.opengis.indoorgml.core.v_1_0.StateType;
import net.opengis.indoorgml.navigation.v_1_0.GeneralSpaceType;
import net.opengis.indoorgml.navigation.v_1_0.ObjectFactory;

public class IndoorGmlSample {
	public static void main(String[] args) throws JAXBException {
		CellSpaceType cellspace = new CellSpaceType();
		IndoorFeaturesType indoorFeatures = new IndoorFeaturesType();
		indoorFeatures.setPrimalSpaceFeatures(
				new PrimalSpaceFeaturesPropertyType().withPrimalSpaceFeatures(
						new PrimalSpaceFeaturesType().withCellSpaceMember(
								new CellSpaceMemberType().withCellSpace(
										new ObjectFactory().createGeneralSpace(new GeneralSpaceType())
								)
						)
				)
		);
		BoundingShapeType boundary = new BoundingShapeType();
		cellspace.setBoundedBy(boundary);
		CellSpaceGeometryType geometry = new CellSpaceGeometryType();
		cellspace.setCellSpaceGeometry(geometry);
		EdgesType edges = new EdgesType();
		NodesType nodes = new NodesType();
		StateMemberType stateMember = new StateMemberType();
		stateMember.setState(new StateType());
		nodes.getStateMember().add(stateMember);
		JAXBContext context = JAXBContext.newInstance(IndoorFeaturesType.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				IndoorGMLNameSpaceMapper.DEFAULT_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd " +
				IndoorGMLNameSpaceMapper.NAVIGATION_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlnavi.xsd" +
				IndoorGMLNameSpaceMapper.XLINK_URI + " https://www.w3.org/XML/2008/06/xlink.xsd");
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new IndoorGMLNameSpaceMapper());
		marshaller.marshal(new net.opengis.indoorgml.core.v_1_0.ObjectFactory().createIndoorFeatures(indoorFeatures), System.out);
	}

	public static class IndoorGMLNameSpaceMapper extends NamespacePrefixMapper {
		private static final String DEFAULT_URI = "http://www.opengis.net/indoorgml/1.0/core";
		private static final String NAVIGATION_URI = "http://www.opengis.net/indoorgml/1.0/navigation";
		private static final String GML_URI = "http://www.opengis.net/gml/3.2";
		private static final String XLINK_URI = "http://www.w3.org/1999/xlink";

		@Override
		public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
			if (DEFAULT_URI.equals(namespaceUri)) {
				return "core";
			} else if (NAVIGATION_URI.equals(namespaceUri)) {
				return "navi";
			} else if (GML_URI.equals(namespaceUri)) {
				return "gml";
			} else if (XLINK_URI.equals(namespaceUri)) {
				return "xlink";
			}
			return suggestion;
		}

		@Override
		public String[] getPreDeclaredNamespaceUris() {
			return new String[] { DEFAULT_URI, NAVIGATION_URI, GML_URI, XLINK_URI };
		}
	}


}
