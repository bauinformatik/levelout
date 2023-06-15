package org.opensourcebim.levelout.util;

import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Storey;
import org.opensourcebim.levelout.intermediatemodel.geo.CartesianPoint;
import org.opensourcebim.levelout.intermediatemodel.geo.CoordinateReference;
import org.opensourcebim.levelout.intermediatemodel.geo.GeodeticPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Geometry {
	
	public static List<Double> asCoordinateList(List<Corner> corners, double z) {
		List<Double> coordinates = new ArrayList<>();
		for (Corner corner : corners) {
			coordinates.add(corner.getX());
			coordinates.add(corner.getY());
			coordinates.add(z);
		}
		if (coordinates.size() > 0)
			coordinates.addAll(Arrays.asList(coordinates.get(0),coordinates.get(1), coordinates.get(2)));
		
		return coordinates;
	}
	

	public static List<Double> asCoordinateList(List<Corner> corners, double z, CoordinateReference crs){
		List<Corner> rotated = new ArrayList<>();
		for(Corner corner: corners)	{
			CartesianPoint rotatedPt = crs.rotateAndScale(new CartesianPoint(corner.getX(), corner.getY()));
			rotated.add(new Corner(rotatedPt.x, rotatedPt.y));
		}
		return asCoordinateList(rotated, crs.scale(z));
	}

	public static double minimumLevel(List<Storey> storeys) {
		double minElevation = Double.MAX_VALUE;
		for (Storey storey : storeys) {
			if (storey.getZ() < minElevation)
				minElevation = storey.getZ();
		}
		return minElevation;
	}

	public static Corner computeCentroid(List<Corner> corners) {
		double cenX = 0;
		double cenY = 0;
		for (Corner node : corners) {
			cenX += node.getX();
			cenY += node.getY();
		}
		return new Corner(cenX / corners.size(), cenY / corners.size());
	}

	public static double angleDegrees(double lat1d, double lon1d, double lat2d, double lon2d){
		Direction direction = angleAbsOrd(lat1d, lon1d, lat2d,lon2d);
		return Math.toDegrees(Math.atan2(direction.y, direction.x));
	}

	public static GeodeticPoint midPoint(GeodeticPoint p1, GeodeticPoint p2){
		double lat1 = Math.toRadians(p1.latitude);
		double lat2 = Math.toRadians(p2.latitude);
		double lon1 = Math.toRadians(p1.longitude);
		double lon2 = Math.toRadians(p2.longitude);
		var Bx = Math.cos(lat2) * Math.cos(lon2-lon1);
		var By = Math.cos(lat2) * Math.sin(lon2-lon1);
		var lat = Math.atan2(
			Math.sin(lat1)+Math.sin(lat2),
			Math.sqrt( (Math.cos(lat1)+Bx)*(Math.cos(lat1)+Bx) + By*By )
		);
		var lon = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
		return new GeodeticPoint(Math.toDegrees(lat), Math.toDegrees(lon));
	}
	public static Direction angleAbsOrd(double lat1d, double lon1d, double lat2d, double lon2d){
		Direction direction = bearingAbsOrd(lat1d, lon1d, lat2d,lon2d);
		return new Direction(direction.y, direction.x);
	}
	public static double bearingDegrees(double lat1d, double lon1d, double lat2d, double lon2d){
		Direction direction = bearingAbsOrd(lat1d, lon1d, lat2d,lon2d);
		return Math.toDegrees(Math.atan2(direction.y, direction.x));
	}

	public static Direction bearingAbsOrd(double lat1Deg, double lon1Deg, double lat2Deg, double lon2Deg){
		double lat1 = Math.toRadians(lat1Deg), lon1 = Math.toRadians(lon1Deg);
		double lat2 = Math.toRadians(lat2Deg), lon2 = Math.toRadians(lon2Deg);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2-lon1);
		double y = Math.sin(lon2-lon1) * Math.cos(lat2);
		return new Direction(x, y);
	}

	public static List<Integer> DegMinSecFromFloat(double degreesF){
		int degrees = (int) Math.floor(degreesF);
		double minutesF = (degreesF-degrees) * 60;
		int minutes = (int) Math.floor(minutesF);
		double secondsF = (minutesF-minutes) * 60;
		int seconds = (int) Math.floor(secondsF);
		double fractionsF = (secondsF-seconds) * 1000000;
		int fractions = (int) Math.floor(fractionsF);
		return List.of(degrees, minutes, seconds, fractions);
	}

	public static class Direction {
		public final double x;
		public final double y;

		public Direction(double x, double y) {
			this.y = y;
			this.x = x;
		}

		@Override
		public String toString() {
			return "( " + this.x + ", " + this.y + " )";
		}
	}
}
