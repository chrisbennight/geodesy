package com.bennight.geodesy.GeodesicImpl;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;

public class GeographicLib extends AbstractCalculatorImpl
{

	GeodesicData g = null;

	public String getName() {
		return "GeographicLib";
	}

	@Override public double[] Direct( double lat, double lon, double azimuth, double distance ) {
		GeodesicData g = Geodesic.WGS84.Direct(lat, lon, azimuth, distance);
		return new double[] {g.lon2, g.lat2, g.azi1};
	}

	@Override public double[] Inverse( double lat1, double lon1, double lat2, double lon2 ) {
		GeodesicData g = Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2);
		return new double[] {g.s12, g.azi1};
	}
}
