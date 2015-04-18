package com.bennight.geodesy.GeodesicImpl;

import com.bennight.geodesy.DirectResults;
import com.bennight.geodesy.InverseResults;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;

public class GeographicLib extends AbstractCalculatorImpl
{

	GeodesicData g = null;

	@Override void direct( double lat1, double lon1, double azimuth1, double distance1, double lat2, double lon2, double azimunth2, DirectResults dr ) {
		g = Geodesic.WGS84.Direct(lat1, lon1, azimuth1, distance1);
		dr.Lattitude2Error = Math.abs(lat2 - g.lat2);
		dr.Longtidue2Error = Math.abs(lon2 - g.lon2);
		dr.Azimuth2Error = Math.abs(azimunth2  - g.azi2);
	}

	@Override void inverse( double lat1, double lon1, double azimuth1, double distance1, double lat2, double lon2, double azimuth2, InverseResults ir ) {
		g = Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2);
		ir.Azimuth1Error = Math.abs(azimuth1 - g.azi1);
		ir.Azimuth2Error = Math.abs(azimuth2 - g.azi2);
		ir.GeodesicDistanceError = Math.abs(distance1 - g.s12);
	}

	public String getName() {
		return "GeographicLib";
	}

	@Override public double[] Direct( double lat, double lon, double azimuth, double distance ) {
		GeodesicData g = Geodesic.WGS84.Direct(lat, lon, azimuth, distance);
		return new double[] {g.lon2, g.lat2, g.azi1};
	}

	@Override public double[] Inverse( double lat1, double lon1, double lat2, double lon2 ) {
		GeodesicData g = Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2);
		return new double[] {g.s12, g.azi2};
	}
}
