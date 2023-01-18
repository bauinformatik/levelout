package org.opensourcebim.levelout.samples;

import javax.xml.bind.JAXBException;

import net.opengis.gml.v_3_2.*;
import net.opengis.indoorgml.core.v_1_0.*;
import org.opensourcebim.levelout.builders.IndoorGmlBuilder;

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
		new IndoorGmlBuilder().write(System.out, indoorFeatures);
	}

}
