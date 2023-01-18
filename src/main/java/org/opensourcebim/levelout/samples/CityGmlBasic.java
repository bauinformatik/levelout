package org.opensourcebim.levelout.samples;

import java.util.List;

import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.building.AbstractBuildingSubdivisionProperty;
import org.citygml4j.core.model.building.Building;
import org.citygml4j.core.model.building.Storey;
import org.citygml4j.core.model.construction.Elevation;
import org.citygml4j.core.model.construction.ElevationProperty;
import org.citygml4j.core.model.core.AbstractCityObjectProperty;
import org.citygml4j.core.model.core.CityModel;
import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.writer.CityGMLWriteException;
import org.opensourcebim.levelout.builders.CityGmlBuilder;
import org.xmlobjects.gml.model.basictypes.Code;
import org.xmlobjects.gml.model.geometry.DirectPosition;

public class CityGmlBasic {
	public static void main(String[] args) throws CityGMLWriteException, CityGMLContextException {
		Building building = new Building();
		CityModel cityModel = new CityModel();
		cityModel.getCityObjectMembers().add(new AbstractCityObjectProperty(building));
		Storey storey = new Storey();
		Elevation elevation = new Elevation(new Code("floorFinish"), new DirectPosition(3.00));
		storey.setElevations(List.of(new ElevationProperty(elevation)));
		building.getBuildingSubdivisions().add(new AbstractBuildingSubdivisionProperty(storey));
		new CityGmlBuilder().write(System.out, building);
	}
}

