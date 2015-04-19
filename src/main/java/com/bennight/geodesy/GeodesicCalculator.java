package com.bennight.geodesy;

import org.apache.commons.math3.stat.descriptive.AggregateSummaryStatistics;

import java.io.IOException;
import java.util.Map;

public interface GeodesicCalculator
{

	public String getName();

	public DirectResults Direct(String inputFile, double latitudeClipAbs, double longitudeClipAbs, Map<String, AggregateSummaryStatistics> aggStats)
			throws IOException;

	public double[] Direct(double lat, double lon, double azimuth, double distance);

	public InverseResults Inverse(String inputFile, double latitudeClipAbs, double longitudeClipAbs, Map<String, AggregateSummaryStatistics> aggStats)
			throws IOException;

	public double[] Inverse(double lat1, double lon1, double lat2, double lon2);

}
