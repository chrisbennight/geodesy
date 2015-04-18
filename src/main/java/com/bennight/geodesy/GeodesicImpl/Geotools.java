package com.bennight.geodesy.GeodesicImpl;

import org.geotools.referencing.GeodeticCalculator;

import java.awt.geom.Point2D;

public class Geotools extends AbstractCalculatorImpl
{
	GeodeticCalculator gc = new GeodeticCalculator();


	@Override public String getName() {
		return "Geotools";
	}

	@Override public double[] Direct( double lat, double lon, double azimuth, double distance ) {
		gc.setStartingGeographicPoint(lon, lat);
		gc.setDirection(azimuth, distance);
		Point2D dest = gc.getDestinationGeographicPoint();
		return new double[] {dest.getX(), dest.getY(), azimuth};
	}

	@Override public double[] Inverse( double lat1, double lon1, double lat2, double lon2 ) {
		gc.setStartingGeographicPoint(lon1, lat1);
		gc.setDestinationGeographicPoint(lon2, lat2);
		return new double[] {gc.getOrthodromicDistance(), gc.getAzimuth()};
	}
}
