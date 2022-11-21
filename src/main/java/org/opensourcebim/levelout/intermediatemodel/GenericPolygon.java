
package org.opensourcebim.levelout.intermediatemodel;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.citygml4j.core.model.construction.CeilingSurface;
import org.citygml4j.core.model.construction.FloorSurface;
import org.citygml4j.core.model.construction.GroundSurface;
import org.citygml4j.core.model.construction.RoofSurface;
import org.citygml4j.core.model.construction.WallSurface;
import org.citygml4j.core.model.core.AbstractSpaceBoundaryProperty;
import org.citygml4j.core.model.core.AbstractThematicSurface;
import org.citygml4j.core.util.geometry.GeometryFactory;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;

import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Way;
import net.opengis.gml.v_3_2_1.DirectPositionType;
import net.opengis.gml.v_3_2_1.PointPropertyType;
import net.opengis.gml.v_3_2_1.PointType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.ExternalObjectReferenceType;
import net.opengis.indoorgml.core.v_1_0.ExternalReferenceType;
import net.opengis.indoorgml.core.v_1_0.StateType;

public class GenericPolygon {
	private int id;
	private String name;
	private int dimension;
	private List<GenericNode> nodeList;
	private final IdCreator idCreator = DefaultIdCreator.getInstance();
	private final GeometryFactory geometryFactory = GeometryFactory.newInstance().withIdCreator(idCreator);

	public GenericPolygon(int id, String type, int dimension, List<GenericNode> nodeList) {
		this.id = id;
		this.name = type; // TODO change to enum if needed at all
		this.dimension = dimension;
		this.nodeList = nodeList;
	}

	public GenericPolygon() {
	}

	public OsmWay createosmWay(OsmOutputStream osmOutput) throws IOException {
		long id = (long) this.id * -1;
		long[] nodes = new long[5];
		for (int i = 0; i < nodeList.size(); i++) {
			OsmNode node = nodeList.get(i).createOsmNode();
			osmOutput.write(node);
			nodes[i] = node.getId(); // assuming we pass 5 coordinates for a polygon
		}
		Array.set(nodes, 4, nodes[0]);
		OsmWay way = new Way(id, TLongArrayList.wrap(nodes)); // how to create and set tags, the name of the polygon is just one part of the tag
		osmOutput.write(way);
		return way;
	}

	public Polygon createCitygmlPoly() {
		List<Double> coordinates = new ArrayList<>();
		for (GenericNode genericNode : nodeList) {
			coordinates.addAll(genericNode.createCityGmlNode()); // adding the list generated from citygmlnode
		}
		return geometryFactory.createPolygon(coordinates, getDimension());
	}

	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
																 Polygon polygon) {
		thematicSurface.setId(idCreator.createId());
		thematicSurface.setLod2MultiSurface(new MultiSurfaceProperty(geometryFactory.createMultiSurface(polygon)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
	}

	public AbstractSpaceBoundaryProperty createBoundary(String name, Polygon polygon) {
		if (name.contains("ground")) {
			return processBoundarySurface(new GroundSurface(), polygon);
		} else if (name.contains("wall")) {
			return processBoundarySurface(new WallSurface(), polygon);
		} else if (name.contains("roof")) {
			return processBoundarySurface(new RoofSurface(), polygon);
		} else if (name.contains("ceiling")) {
			return processBoundarySurface(new CeilingSurface(), polygon);
		} else if (name.contains("floor")) {
			return processBoundarySurface(new FloorSurface(), polygon);
		} else return null;  // TODO: not nice
	}

	public CellSpaceType createIndoorGmlCellSpace() {
		CellSpaceType cellspace = new CellSpaceType();
		cellspace.setId("cs" + id);
		ExternalObjectReferenceType extrefobj = new ExternalObjectReferenceType();
		extrefobj.setUri(null); // that's the default anyway, I guess
		ExternalReferenceType extreftyp = new ExternalReferenceType();
		extreftyp.setExternalObject(extrefobj);
		List<ExternalReferenceType> extreflist = new ArrayList<>();
		extreflist.add(extreftyp);
		cellspace.setExternalReference(extreflist);
		return cellspace;
	}

	public StateType setStatePos() {
		// computing centroid from nodeslist
		double minX = nodeList.get(0).getX().doubleValue();
		double minY = nodeList.get(0).getY().doubleValue();
		double minZ = nodeList.get(0).getZ().doubleValue();

		double maxX = nodeList.get(0).getX().doubleValue();
		double maxY = nodeList.get(0).getY().doubleValue();
		double maxZ = nodeList.get(0).getZ().doubleValue();

		// TODO: check formula for centroid calculation

		PointPropertyType pointProperty = new PointPropertyType();
		PointType point = new PointType();
		DirectPositionType dirPos = new DirectPositionType();
		for (GenericNode node : nodeList) {
			if (node.getX().doubleValue() < minX) {
				minX = node.getX().doubleValue();
			} else if (node.getX().doubleValue() > maxX) {
				maxX = node.getX().doubleValue();
			}
			if (node.getY().doubleValue() < minY) {
				minY = node.getY().doubleValue();
			} else if (node.getY().doubleValue() > maxY) {
				maxY = node.getY().doubleValue();
			}
			if (node.getZ().doubleValue() < minZ) {
				minZ = node.getZ().doubleValue();
			} else if (node.getZ().doubleValue() > maxZ) {
				maxZ = node.getZ().doubleValue();
			}
		}

		double centroidX = (minX + maxX) / 2;
		double centroidY = (minY + maxY) / 2;
		double centroidZ = (minZ + maxZ) / 2;

		dirPos.withValue(centroidX, centroidY, centroidZ).withSrsDimension(BigInteger.valueOf(3));
		point.setPos(dirPos);
		pointProperty.setPoint(point);

		StateType state = new StateType();
		state.setId("st" + id);
		state.setGeometry(pointProperty);
		return state;
	}

	public String getName() {
		return name;
	}

	public int getDimension() {
		return dimension;
	}
}






























