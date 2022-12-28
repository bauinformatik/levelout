
package org.opensourcebim.levelout.intermediatemodel;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Longs;
import org.citygml4j.core.model.construction.CeilingSurface;
import org.citygml4j.core.model.construction.DoorSurface;
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

public class Room {
	private final long id;
	private final String type;
	private final int dimension;
	private final List<Corner> nodeList;
	private final IdCreator idCreator = DefaultIdCreator.getInstance();
	private final GeometryFactory geometryFactory = GeometryFactory.newInstance().withIdCreator(idCreator);

	public Room(long id, String type, int dimension, List<Corner> nodeList) {
		this.id = id; // TODO auto-increment
		this.type = type; // TODO change to enum if needed at all
		this.dimension = dimension; // TODO static value, always 3
		this.nodeList = nodeList;
	}

	public void createosmWay(OsmOutputStream osmOutput) throws IOException {
		long id = this.id * -1;
		List<Long>nodes = new ArrayList<>();
		for (Corner genericNode : nodeList) {
			OsmNode node = genericNode.createOsmNode();
			osmOutput.write(node);
			nodes.add(node.getId());
		}
		nodes.add(nodes.get(0));
		OsmWay way = new Way(id, TLongArrayList.wrap(Longs.toArray(nodes))); // TODO: how to create and set tags, the name of the polygon is just one part of the tag
		osmOutput.write(way);
	}

	public Polygon createCitygmlPoly() {
		List<Double> coordinates = new ArrayList<>();
		for (Corner genericNode : nodeList) {
			coordinates.addAll(genericNode.createCityGmlNode()); // adding the list generated from citygmlnode
		}
		return geometryFactory.createPolygon(coordinates, getDimension());
	}

	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
																 Polygon polygon) {
		thematicSurface.setId(idCreator.createId());
		thematicSurface.setLod0MultiSurface(new MultiSurfaceProperty(geometryFactory.createMultiSurface(polygon)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
	}

	public AbstractSpaceBoundaryProperty createBoundary(Polygon polygon) {
		if (type.contains("ground")) {
			return processBoundarySurface(new GroundSurface(), polygon);
		} else if (type.contains("wall")) {
			return processBoundarySurface(new WallSurface(), polygon);
		} else if (type.contains("roof")) {
			return processBoundarySurface(new RoofSurface(), polygon);
		} else if (type.contains("ceiling")) {
			return processBoundarySurface(new CeilingSurface(), polygon);
		} else if (type.contains("door")) {
				return processBoundarySurface(new DoorSurface(), polygon);
		} else if (type.contains("floor")) {
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

	public StateType createIndoorGmlState() {
		// computing centroid from nodeslist
		double minX = nodeList.get(0).getX();
		double minY = nodeList.get(0).getY();
		double minZ = nodeList.get(0).getZ();
		double maxX = minX, maxY = minY, maxZ = minZ;

		// TODO: check formula for centroid calculation

		PointPropertyType pointProperty = new PointPropertyType();
		PointType point = new PointType();
		DirectPositionType dirPos = new DirectPositionType();
		for (Corner node : nodeList) {
			if (node.getX() < minX) {
				minX = node.getX();
			} else if (node.getX() > maxX) {
				maxX = node.getX();
			}
			if (node.getY() < minY) {
				minY = node.getY();
			} else if (node.getY() > maxY) {
				maxY = node.getY();
			}
			if (node.getZ() < minZ) {
				minZ = node.getZ();
			} else if (node.getZ() > maxZ) {
				maxZ = node.getZ();
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

	public int getDimension() {
		return dimension;
	}
}
