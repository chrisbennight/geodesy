package com.bennight.geodesy;

import java.io.IOException;

public interface GeodesicCalculator
{

	public String getName();

	public DirectResults Direct(String inputFile, double latitudeClipAbs, double longitudeClipAbs)
			throws IOException;

	public double[] Direct(double lat, double lon, double azimuth, double distance);

	public InverseResults Inverse(String inputFile, double latitudeClipAbs, double longitudeClipAbs)
			throws IOException;

	public double[] Inverse(double lat1, double lon1, double lat2, double lon2);

}
