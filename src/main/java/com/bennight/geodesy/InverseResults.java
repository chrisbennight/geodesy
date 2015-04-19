package com.bennight.geodesy;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class InverseResults
{
	public long Milliseconds = 0;
	public SummaryStatistics MillisecondsStats = null;

	public double Azimuth1Error = 0;
	public SummaryStatistics Azimuth1ErrorStats = null;

	public double Azimuth2Error = 0;
	public SummaryStatistics Azimuth2ErrorStats = null;

	public double GeodesicDistanceError;
	public SummaryStatistics GeodesicDistanceErrorStats = null;

	public long Count = 0;

}
