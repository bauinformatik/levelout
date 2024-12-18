package org.opensourcebim.levelout.intermediatemodel.geo;

import org.junit.Assert;
import org.junit.Test;

public class CoordinateConversionTest {

	@Test
	public void testGridConvergenceCentralMeridian(){
		GeodeticPoint centralMeridian = new GeodeticPoint(45, 63);
		Assert.assertEquals(0, centralMeridian.getUtmGridConvergence(),0.00000001);
	}
	@Test
	public void testGridConvergenceEquator() {
		GeodeticPoint equator = new GeodeticPoint(0, 61);
		Assert.assertEquals(0, equator.getUtmGridConvergence(),0.00000001);
	}
	@Test
	public void testGridConvergenceBorder() {
		GeodeticPoint segmentBorder = new GeodeticPoint(45, 60); // belongs to the right segment?
		Assert.assertEquals(-0.03704094636470207, segmentBorder.getUtmGridConvergence(), 0.00000001);
	}
	@Test
	public void testGridConvergence() {
		GeodeticPoint point = new GeodeticPoint(45, 61); // west of central, north of equator
		Assert.assertEquals(-0.024687696117208394, point.getUtmGridConvergence(), 0.00000001);
	}

	@Test
	public void testHorizontal(){
		CartesianPoint p1 = new CartesianPoint(10, 5);
		CartesianPoint p2 = new CartesianPoint(20, 5);
		GeodeticOriginCRS crs = new GeodeticOriginCRS(new GeodeticPoint(45, 56), 0);
		Assert.assertEquals(crs.cartesianToGeodetic(p1, false).latitude, crs.cartesianToGeodetic(p2, false).latitude, 0.00000001); // ~1 mm
	}

	@Test
	public void testVertical(){
		CartesianPoint p1 = new CartesianPoint(5, 10);
		CartesianPoint p2 = new CartesianPoint(5, 20);
		GeodeticOriginCRS crs = new GeodeticOriginCRS(new GeodeticPoint(45, 56), Math.PI/2);
		Assert.assertEquals(crs.cartesianToGeodetic(p1, false).latitude, crs.cartesianToGeodetic(p2, false).latitude, 0.000000001); // ~1 mm
	}
	@Test
	public void testEpsgNorth(){
		GeodeticPoint weimar = new GeodeticPoint(50.9795, 11.3235);
		Assert.assertEquals("32632", new GeodeticOriginCRS(weimar, 0).origin.getUtmEpsg());
	}

	@Test
	public void testEpsgSouth(){
		GeodeticPoint sydney= new GeodeticPoint(-33.90632062825244, 151.20215639320887);
		Assert.assertEquals("32756", new GeodeticOriginCRS(sydney, 0).origin.getUtmEpsg());
	}
	@Test
	public void testEpsgSingleDigitLongZone(){
		GeodeticPoint anchorage = new GeodeticPoint(61.2163129, -149.8948520);
		Assert.assertEquals("32606", new GeodeticOriginCRS(anchorage, 0).origin.getUtmEpsg());
	}
	@Test
	public void testOriginWGS84viaUTMconvertOrigin() {
		// http://rcn.montana.edu/resources/Converter.aspx
		// Ref1 E = 333,780.622 N = 6,246,775.891 MGA GRS80 , Zone : 56 South
		// Lat = -33.90632062825244, Long = 151.20215639320887

		CartesianPoint pointToTransform = new CartesianPoint(0, 0);
		GeodeticPoint csOrigin = new GeodeticPoint(-33.90632062825244, 151.20215639320887);
		CoordinateReference crs = new GeodeticOriginCRS(csOrigin, -0.13918031137);
		GeodeticPoint transformed = crs.cartesianToGeodetic(pointToTransform, false);
		Assert.assertEquals(csOrigin.longitude, transformed.longitude, 0.000000001);
		Assert.assertEquals(csOrigin.latitude, transformed.latitude, 0.000000001);
	}

	@Test
	public void testOriginWGS84viaUTMconvertOtherPoint() {
		CartesianPoint pointToTransform = new CartesianPoint(116.611, 75.960);
		GeodeticPoint csOrigin = new GeodeticPoint(-33.90632062825244, 151.20215639320887);
		CoordinateReference crs = new GeodeticOriginCRS(csOrigin, -0.13918031137);
		GeodeticPoint transformed = crs.cartesianToGeodetic(pointToTransform, false);
		Assert.assertEquals(-33.9058082188758, transformed.latitude, 0.000000001);
		Assert.assertEquals(151.203530034203, transformed.longitude, 0.000000001);

	}

	@Test
	public void testOriginArbitraryCRSconvertOrigin() {
		// origin WGS84 lat=56.336468, long=150.311068
		// rotation xAxis abscissa=0.990330045, ordinate=-0.138731399
		// point x=116.611, y=75.960

		// Map Grid of Australia Zone 56
		// Ref1 E = 333,780.622 N = 6,246,775.891 H = 97.457
		// Ref2 E = 333,906.644 N = 6,246,834.938 H = 98.291
		// Ref1 X = 0.000 Y = 0.000 Z = 0.000
		// Ref2 X = 116.611 Y = 75.960 Z = 0.834
		// Eastings = 333,780.622
		// Northings = 6,246,775.891
		// OrthogonalHeight = 97.457
		// XAxisAbscissa = 0.990330045
		// XAxisOrdinate = -0.138731399
		// Scale = 0.999998
		CartesianPoint pointToTransform = new CartesianPoint(0, 0);
		ProjectedPoint csOrigin = new ProjectedPoint(333780.622, 6246775.891);
		CoordinateReference crs = new ProjectedOriginCRS(csOrigin,0.990330045, -0.138731399, "epsg:28356" );
		GeodeticPoint transformed = crs.cartesianToGeodetic(pointToTransform, false);
		Assert.assertEquals(151.20215639320887, transformed.longitude, 0.000000001);
		Assert.assertEquals(-33.90632062825244, transformed.latitude, 0.000000001);
	}
	@Test
	public void testOriginArbitraryCRSconvertOtherPoint(){
		CartesianPoint pointToTransform = new CartesianPoint(116.611, 75.960);
		ProjectedPoint csOrigin = new ProjectedPoint(333780.622, 6246775.891);
		CoordinateReference crs = new ProjectedOriginCRS(csOrigin,0.990330045, -0.138731399, "epsg:28356" );
		GeodeticPoint transformed = crs.cartesianToGeodetic(pointToTransform, false);
		Assert.assertEquals(151.203530034203, transformed.longitude, 0.000000001);
		Assert.assertEquals(-33.9058082188758, transformed.latitude, 0.000000001);

	}

}
