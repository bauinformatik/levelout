package org.opensourcebim.levelout.samples;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import net.opengis.gml.v_3_2.*;
import net.opengis.indoorgml.core.v_1_0.*;

import java.lang.Boolean;

public class IndoorGmlBasic {

	static final net.opengis.indoorgml.core.v_1_0.ObjectFactory indoorFactory = new net.opengis.indoorgml.core.v_1_0.ObjectFactory();
	static final net.opengis.gml.v_3_2.ObjectFactory gmlFactory = new net.opengis.gml.v_3_2.ObjectFactory();
	public static void main(String[] args) throws JAXBException {
		IndoorFeaturesType indoorFeatures = new IndoorFeaturesType().withPrimalSpaceFeatures(
			new PrimalSpaceFeaturesPropertyType().withPrimalSpaceFeatures(
				new PrimalSpaceFeaturesType().withCellSpaceMember(
					new CellSpaceMemberType().withCellSpace(
						indoorFactory.createCellSpace(new CellSpaceType().withCellSpaceGeometry(
							new CellSpaceGeometryType().withGeometry2D(
								new SurfacePropertyType().withAbstractSurface(
									gmlFactory.createPolygon(new PolygonType().withExterior(
										new AbstractRingPropertyType().withAbstractRing(
											gmlFactory.createLinearRing(new LinearRingType().withCoordinates(
												new CoordinatesType()
											))
										)
									))
								)
							)
						))
					)
				)
			)
		).withMultiLayeredGraph(
			new MultiLayeredGraphPropertyType().withMultiLayeredGraph(
				new MultiLayeredGraphType().withSpaceLayers(
					new SpaceLayersType().withSpaceLayerMember(
						new SpaceLayerMemberType().withSpaceLayer(
							new SpaceLayerType().withNodes(
								new NodesType().withStateMember(
									new StateMemberType().withState(
										new StateType()
									)
								)
							).withEdges(
								new EdgesType().withTransitionMember(
									new TransitionMemberType().withTransition(
										new TransitionType()
									)
								)
							)
						)
					)
				)
			)

		);

		JAXBContext context = JAXBContext.newInstance(IndoorFeaturesType.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				IndoorGMLNameSpaceMapper.DEFAULT_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd " +
				IndoorGMLNameSpaceMapper.NAVIGATION_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlnavi.xsd" +
				IndoorGMLNameSpaceMapper.XLINK_URI + " https://www.w3.org/XML/2008/06/xlink.xsd");
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new IndoorGMLNameSpaceMapper());
		marshaller.marshal(indoorFactory.createIndoorFeatures(indoorFeatures), System.out);
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
			return new String[]{DEFAULT_URI, NAVIGATION_URI, GML_URI, XLINK_URI};
		}
	}


}
