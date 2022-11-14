package org.opensourcebim.levelout.intermediatemodel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.citygml4j.core.model.construction.CeilingSurface;
import org.citygml4j.core.model.construction.GroundSurface;
import org.citygml4j.core.model.construction.RoofSurface;
import org.citygml4j.core.model.construction.WallSurface;
import org.citygml4j.core.model.core.AbstractSpaceBoundaryProperty;
import org.citygml4j.core.model.core.AbstractThematicSurface;
import org.citygml4j.core.util.geometry.GeometryFactory;
import org.opensourcebim.levelout.samples.IndoorGmlBuilding;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;

import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;
import net.opengis.gml.v_3_2_1.DirectPositionType;
import net.opengis.gml.v_3_2_1.PointPropertyType;
import net.opengis.gml.v_3_2_1.PointType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceMemberType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.ExternalObjectReferenceType;
import net.opengis.indoorgml.core.v_1_0.ExternalReferenceType;
import net.opengis.indoorgml.core.v_1_0.StateMemberType;
import net.opengis.indoorgml.core.v_1_0.StateType;

public class GenericPolygon {
	private int id;
	private String name;
	private int dimension;
	private List<GenericNode> nodeList;
	//private GenericNode gn;  
	private IdCreator id2 = DefaultIdCreator.getInstance(); 
	private GeometryFactory geom = GeometryFactory.newInstance().withIdCreator(id2); ;
	public GenericPolygon(int id, String name, int dimension, List<GenericNode> nodeList) {
		super();
		this.id = id;
		this.name = name;
		this.dimension = dimension;
		this.nodeList = nodeList;
	}
	
	/*public GenericPolygon(int id, String name, List<GenericNode> nodeList) {
		super();
		this.id = id;
		this.name = name;
		this.nodeList = nodeList;
	}*/
	
	public GenericPolygon() {
		// TODO Auto-generated constructor stub
	}

	public OsmWay createosmWay() throws IOException
	{
		GenericBuilding gb = new GenericBuilding();
		String fileName = gb.fileName2;
	OutputStream output2 = new FileOutputStream(fileName);
	OsmOutputStream osmOutput = new OsmXmlOutputStream(output2, true);
	
		long idosm = (long)id*-1;
		long[] nodes =  new long[5];
		for (int i=0;i<4;i++)
		{
			OsmNode node = nodeList.get(i).createOsmnode();
			osmOutput.write(node);
			//System.out.println(node.getId());
			nodes[i]= node.getId(); // assuming we pass 5 coordinates for a polygon
			//System.out.printf("Executed");
		}
		System.out.println(nodes[0]);
		Array.set(nodes, 4, nodes[0]);
		
		for (long a : nodes)
		System.out.println(a);
		
		
	//	List <OsmWay> wayList = new ArrayList<OsmWay>();
		OsmWay ways = new Way(idosm, TLongArrayList.wrap(nodes));//, tags); // how to create and set tags , the name of the polygon is just one part of the tag 
	//	long[] nodeList = new long[5];
	//	wayList.add(ways);
		System.out.println(ways);
		osmOutput.write(ways);
		osmOutput.complete();
		return ways;
		
	}
	
	
public Polygon createCitygmlPoly()
{
	//id2 = DefaultIdCreator.getInstance(); // citygml id generator 
	//geom = GeometryFactory.newInstance().withIdCreator(id2);

	
	List<Double> doubleList2 = new ArrayList<Double>();
	for (int i=0;i< nodeList.size();i++)
	{
		 doubleList2.addAll(nodeList.get(i).createCitygmlnode()); // adding the list generated from citygmlnode
	}
		Polygon p= geom.createPolygon(doubleList2, getDimension());
	return p;

	
}


private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
		Polygon... polygons) {
	thematicSurface.setId(id2.createId());
	thematicSurface.setLod2MultiSurface(new MultiSurfaceProperty(geom.createMultiSurface(polygons)));
	return new AbstractSpaceBoundaryProperty(thematicSurface);
}

public  AbstractSpaceBoundaryProperty createBoundary(String name,  Polygon polygons) {
	
	AbstractSpaceBoundaryProperty bsp = null;
if (name.contains("ground"))
{
	bsp = processBoundarySurface(new GroundSurface(), polygons);
}
else if (name.contains("wall"))
{
	bsp=  processBoundarySurface(new WallSurface(), polygons);
}
else if (name.contains("roof"))
{
	bsp= processBoundarySurface(new RoofSurface(), polygons);
}
else if (name.contains("ceiling"))
{
	bsp= processBoundarySurface(new CeilingSurface(), polygons);
}
return bsp;

}

public void createIndoorgmlCellspace()
{
	IndoorGmlBuilding inb = new IndoorGmlBuilding();
	
	List<CellSpaceMemberType> cellspacemembers = new ArrayList<CellSpaceMemberType>();
	
	CellSpaceType cellspace = new CellSpaceType();
	cellspace.setId("cs"+ String.valueOf(id));
	ExternalObjectReferenceType extrefobj = new ExternalObjectReferenceType();
	String uri = null;
	extrefobj.setUri(uri);
	ExternalReferenceType extreftyp = new ExternalReferenceType();
	extreftyp.setExternalObject(extrefobj);
	List<ExternalReferenceType> extreflist = new ArrayList<ExternalReferenceType>();
	cellspace.setExternalReference(extreflist);
	
	inb.createCellspaceMember(cellspace, cellspacemembers);
	
	
	
	
}

public void setStatePos()
{
	IndoorGmlBuilding inb = new IndoorGmlBuilding();
	// computing centroid from nodeslist 
	double minx = (double) nodeList.get(0).getX();
	double miny = (double) nodeList.get(0).getY();
	double minz = (double) nodeList.get(0).getZ();
	
	
	double maxx = (double) nodeList.get(0).getX();
	double maxy = (double) nodeList.get(0).getY();
	double maxz = (double) nodeList.get(0).getZ();
	
	PointPropertyType pointProp = new PointPropertyType();
	 PointType point = new PointType();
	 
	 DirectPositionType dirPos = new DirectPositionType();	 
	 
	
	
	for (int i =0;i<3;i++)
		{
		if ((double)nodeList.get(i).getX()<= minx)
		{
			minx= (double)nodeList.get(i).getX();
		}
		else if ((double)nodeList.get(i).getX()>= maxx)
		{
			maxx=(double)nodeList.get(i).getX();
		}
		if ((double)nodeList.get(i).getY()<= miny)
		{
			minx= (double)nodeList.get(i).getY();
		}
		else if ((double)nodeList.get(i).getY()>= maxy)
		{
			maxx=(double)nodeList.get(i).getY();
		}
		if ((double)nodeList.get(i).getZ()<= minz)
		{
			minx= (double)nodeList.get(i).getZ();
		}
		else if ((double)nodeList.get(i).getZ()>= maxz)
		{
			maxx=(double)nodeList.get(i).getZ();
		}
				
		}
		double centroidx = (minx+maxx)/2;
		double centroidy = (miny+maxy)/2;
		double centroidz = (minz+maxz)/2;
	
	 dirPos.withValue(centroidx,centroidy,centroidz).withSrsDimension(BigInteger.valueOf(3));

		point.setPos(dirPos);
		pointProp.setPoint(point); 
		
		// creating and setting state geometry
		
		StateType state = new StateType();
		state.setId("st"+ String.valueOf(id));		
		state.setGeometry(pointProp);	
		
		// creating state member 
		
		List<StateMemberType> states = new ArrayList<StateMemberType>();
		
		inb.createStateMember(state, states);
		
}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
	public List<GenericNode> getNodeList() {
		return nodeList;
	}
	public void setNodeList(List<GenericNode> nodeList) {
		this.nodeList = nodeList;
	}
	
}
