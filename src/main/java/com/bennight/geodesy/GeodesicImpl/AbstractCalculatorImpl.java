package com.bennight.geodesy.GeodesicImpl;

import com.bennight.geodesy.DirectResults;
import com.bennight.geodesy.GeodesicCalculator;
import com.bennight.geodesy.InverseResults;
import org.apache.commons.math3.stat.descriptive.AggregateSummaryStatistics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public abstract class AbstractCalculatorImpl
		implements GeodesicCalculator
{
	//Ref: http://geographiclib.sourceforge.net/html/geodesic.html
	// format is, per line (space delimeted)
	// latitude for point 1, lat1 (degrees, exact)
	// longitude for point 1, lon1 (degrees, always 0)
	// azimuth for point 1, azi1 (clockwise from north in degrees, exact)
	// latitude for point 2, lat2 (degrees, accurate to 10?18 deg)
	// longitude for point 2, lon2 (degrees, accurate to 10?18 deg)
	// azimuth for point 2, azi2 (degrees, accurate to 10?18 deg)
	// geodesic distance from point 1 to point 2, s12 (meters, exact)
	// arc distance on the auxiliary sphere, a12 (degrees, accurate to 10?18 deg)
	// reduced length of the geodesic, m12 (meters, accurate to 0.1 pm)
	// the area under the geodesic, S12 (m2, accurate to 1 mm2)
	private static final int LAT1 = 0;
	private static final int LON1 = 1;
	private static final int AZIMUTH1= 2;
	private static final int LAT2= 3;
	private static final int LON2= 4;
	private static final int AZIMUTH2= 5;
	private static final int GEODESIC_DISTANCE= 6;
	private static final int ARC_DISTANCE= 7;
	private static final int REDUCED_LENGTH_GEODESIC= 8;
	private static final int AREA_GEODESIC= 9;


	//abstract void direct(double lat1, double lon1, double azimuth1, double distance1,  double lat2, double lon2, double azimunth2, DirectResults dr);


	private void direct( double lat1, double lon1, double azimuth1, double distance1, double lat2, double lon2, double azimunth2, DirectResults dr ) {
		double[] vals = Direct(lat1, lon1, azimuth1, distance1);
		dr.Lattitude2Error = Math.abs(lat2 - vals[1]);
		dr.Longtidue2Error = Math.abs(lon2 - vals[0]);
		dr.Azimuth2Error = Math.abs(azimunth2  - vals[2]);
	}

	/***
	 *
	 * @param lat
	 * @param lon
	 * @param azimuth
	 * @param distance
	 * @return double[] {lon, lat, azimuth}
	 */
	 public abstract double[] Direct( double lat, double lon, double azimuth, double distance );


	private static double[] parseLines(String line){
		String[] s = line.split(" ");
		double[] d = new double[10];
		for (int i = 0; i < 10; i++){
			d[i] = Double.valueOf(s[i]);
		}
		return d;
	}

	public DirectResults Direct( String inputFile, double latitudeClipAbs, double longitudeClipAbs, Map<String, AggregateSummaryStatistics> aggStats )
			throws IOException {

		DirectResults dr = new DirectResults();
		dr.MillisecondsStats = aggStats.get("time").createContributingStatistics();
		dr.Lattitude2ErrorStats = aggStats.get("latitude").createContributingStatistics();
		dr.Longitude2ErrorStats = aggStats.get("longitude").createContributingStatistics();
		dr.Azimuth2ErrorStats = aggStats.get("azimuth").createContributingStatistics();
		DirectResults tmp = new DirectResults();

		for (String l :Files.readAllLines(Paths.get(inputFile))){
			double[] items = parseLines(l);
			if (Math.abs(items[LAT1]) > latitudeClipAbs || Math.abs(items[LAT2]) > latitudeClipAbs){
				continue;
			}
			if (Math.abs(items[LON1]) > longitudeClipAbs || Math.abs(items[LON2]) > longitudeClipAbs){
				continue;
			}
			long time = System.currentTimeMillis();
			direct(items[LAT1], items[LON1], items[AZIMUTH1], items[GEODESIC_DISTANCE], items[LAT2], items[LON2], items[AZIMUTH2], tmp);
			dr.MillisecondsStats.addValue(System.currentTimeMillis() - time);
			dr.Lattitude2ErrorStats.addValue(tmp.Lattitude2Error);
			dr.Longitude2ErrorStats.addValue(tmp.Longtidue2Error);
			dr.Azimuth2ErrorStats.addValue(tmp.Azimuth2Error);
			dr.Count++;
		}
		return dr;
	}

	private void inverse(double lat1, double lon1, double azimuth1, double distance1,  double lat2, double lon2, double azimuth2, InverseResults ir){
		double[] vals = Inverse(lat1, lon1, lat2, lon2);
		ir.Azimuth1Error = Math.abs(azimuth1 - vals[1]);
		ir.GeodesicDistanceError = Math.abs(distance1 - vals[0]);
	}

	/***
	 *
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return double[] {geodetic distance, azimuth};
	 */
	public abstract double[] Inverse( double lat1, double lon1, double lat2, double lon2 );

	public InverseResults Inverse( String inputFile, double latitudeClipAbs, double longitudeClipAbs, Map<String, AggregateSummaryStatistics> aggStats  )
		throws IOException {
		InverseResults ir = new InverseResults();
		ir.MillisecondsStats = aggStats.get("time").createContributingStatistics();
		ir.GeodesicDistanceErrorStats = aggStats.get("distance").createContributingStatistics();
		ir.Azimuth1ErrorStats = aggStats.get("azimuth1").createContributingStatistics();
		ir.Azimuth2ErrorStats = aggStats.get("azimuth2").createContributingStatistics();
		InverseResults tmp = new InverseResults();
		for (String l :Files.readAllLines(Paths.get(inputFile))){
			double[] items = parseLines(l);
			if (Math.abs(items[LAT1]) > latitudeClipAbs || Math.abs(items[LAT2]) > latitudeClipAbs){
				continue;
			}
			if (Math.abs(items[LON1]) > longitudeClipAbs || Math.abs(items[LON2]) > longitudeClipAbs){
				continue;
			}
			long time = System.currentTimeMillis();
			inverse(items[LAT1], items[LON1], items[AZIMUTH1], items[GEODESIC_DISTANCE], items[LAT2], items[LON2], items[AZIMUTH2], tmp);
			ir.MillisecondsStats.addValue(System.currentTimeMillis() - time);
			ir.Azimuth1ErrorStats.addValue(tmp.Azimuth1Error);
			ir.Azimuth2ErrorStats.addValue(tmp.Azimuth2Error);
			ir.GeodesicDistanceErrorStats.addValue(tmp.GeodesicDistanceError);
			ir.Count++;
		}
		return ir;
	}
}
