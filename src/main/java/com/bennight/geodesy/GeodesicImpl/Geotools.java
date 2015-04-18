package com.bennight.geodesy.GeodesicImpl;

import com.bennight.geodesy.DirectResults;
import com.bennight.geodesy.InverseResults;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.datum.DefaultEllipsoid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.geom.Point2D;

public class Geotools extends AbstractCalculatorImpl
{
	GeodeticCalculator gc = new GeodeticCalculator();

	@Override void direct( double lat1, double lon1, double azimuth1, double distance1, double lat2, double lon2, double azimunth2, DirectResults dr ) {
		gc.setStartingGeographicPoint(lon1, lat1);
		gc.setDirection(azimuth1, distance1);
		Point2D dest = gc.getDestinationGeographicPoint();
		dr.Azimuth2Error = -1;
		dr.Lattitude2Error = Math.abs(lat2 - dest.getY());
		dr.Longtidue2Error = Math.abs(lon2 - dest.getX());
	}


	@Override void inverse( double lat1, double lon1, double azimuth1, double distance1, double lat2, double lon2, double azimuth2, InverseResults ir ) {
		gc.setStartingGeographicPoint(lon1, lat1);
		gc.setDestinationGeographicPoint(lon2, lat2);
		double distance = gc.getOrthodromicDistance();
		double azimuth = gc.getAzimuth();

		ir.Azimuth1Error = Math.abs(azimuth1 - azimuth);
		ir.GeodesicDistanceError = Math.abs(distance1 - distance);

	}

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
