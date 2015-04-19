package com.bennight.geodesy;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class DirectResults
{
	public long Milliseconds = 0;
	public SummaryStatistics MillisecondsStats = null;

	public double Lattitude2Error = 0;
	public SummaryStatistics Lattitude2ErrorStats = null;

	public double Longtidue2Error = 0;
	public SummaryStatistics Longitude2ErrorStats = null;

	public double Azimuth2Error = 0;
	public SummaryStatistics Azimuth2ErrorStats = null;

	public long Count = 0;
}
