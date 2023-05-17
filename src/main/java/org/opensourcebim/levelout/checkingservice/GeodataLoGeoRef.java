package org.opensourcebim.levelout.checkingservice;

import org.bimserver.models.ifc4.*;
import org.bimserver.utils.IfcUtils;

import java.util.Optional;

public class GeodataLoGeoRef {
    protected void validateUnits(StringBuilder txt, IfcProject project){
        if(project.getUnitsInContext()==null || project.getUnitsInContext().getUnits() == null){
            // no units at all
        }
        Optional<IfcUnit> unit = project.getUnitsInContext().getUnits().stream().filter(u -> u instanceof IfcNamedUnit && ((IfcNamedUnit) u).getUnitType() == IfcUnitEnum.LENGTHUNIT).findAny();
        if(unit.isEmpty()){
            // no length unit found
        } else {
            IfcSIUnit ifcUnit = (IfcSIUnit) unit.get();
            IfcSIUnitName name = ifcUnit.getName();  // METRE?
            IfcSIPrefix prefix = ifcUnit.getPrefix();
            String fullUnit = prefix.getName()  +  name.getName(); // TODO report
            float factor = IfcUtils.getLengthUnitPrefixMm(ifcUnit.getPrefix().getName());
        }

    }
}
