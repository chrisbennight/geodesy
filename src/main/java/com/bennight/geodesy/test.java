package com.bennight.geodesy;

import com.bennight.geodesy.GeodesicImpl.GeographicLib;
import com.bennight.geodesy.GeodesicImpl.Geotools;
import com.vividsolutions.jts.geom.Coordinate;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.math3.stat.descriptive.AggregateSummaryStatistics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test
{

	private static final String DATA_DIR = "./target/data/";
	private static final String COASTLINE_DIR = "./target/coastline/";
	public static final String COASTLINE_SHAPE_FILE = "./target/coastline/ne_10m_coastline.shp";
	private static final String DATA_FILE = DATA_DIR + "GeodTest.dat";
	private final GeodesicCalculator[] tests = new GeodesicCalculator[] {new Geotools(), new GeographicLib()};
	private static final int numReps = 100;
	private static final double latitudeClipAbs = 91;
	private static final double longitudeClipAbs = 177;

	public static void main(String[] args){

		test t = new test();
		if (!t.runTest()){
			System.exit(-1);
		}
	}


	public boolean runTest()	{
		try {
			extractTestData();
		}
		catch (ZipException e) {
			System.out.println("Error extracting test data, error was: " + e.getLocalizedMessage());
			return false;
		}


		Map<String, Map<String, AggregateSummaryStatistics>> allDirectStats = new HashMap<>(tests.length);
		Map<String, Map<String, AggregateSummaryStatistics>> allInverseStats = new HashMap<>(tests.length);

		for (GeodesicCalculator gc : tests) {
			Map<String, AggregateSummaryStatistics> directStats = new HashMap<>();
			AggregateSummaryStatistics timeDirectAgg = new AggregateSummaryStatistics();
			AggregateSummaryStatistics lonDirectAgg = new AggregateSummaryStatistics();
			AggregateSummaryStatistics latDirectAgg = new AggregateSummaryStatistics();
			AggregateSummaryStatistics azimuth2DirectAgg = new AggregateSummaryStatistics();

			directStats.put("time", timeDirectAgg);
			directStats.put("latitude", latDirectAgg);
			directStats.put("longitude", lonDirectAgg);
			directStats.put("azimuth", azimuth2DirectAgg);

			allDirectStats.put(gc.getName(), directStats);

			Map<String, AggregateSummaryStatistics> inverseStats = new HashMap<>();
			AggregateSummaryStatistics timeInverseAgg = new AggregateSummaryStatistics();
			AggregateSummaryStatistics azimuth1InverseAgg = new AggregateSummaryStatistics();
			AggregateSummaryStatistics azimuth2InverseAgg = new AggregateSummaryStatistics();
			AggregateSummaryStatistics geodesicInverseAgg = new AggregateSummaryStatistics();

			inverseStats.put("time", timeInverseAgg);
			inverseStats.put("distance", geodesicInverseAgg);
			inverseStats.put("azimuth1", azimuth1InverseAgg);
			inverseStats.put("azimuth2", azimuth2InverseAgg);

			allInverseStats.put(gc.getName(), inverseStats);
		}


		try {
			for (GeodesicCalculator gc : tests) {
				System.out.println("");
				System.out.println("----------------------------------------------------------------------------------");
				System.out.println("Beginning " + numReps + " repititions for " + gc.getName() + " direct computation.");
				System.out.println("----------------------------------------------------------------------------------");
				DirectResults dr = null;
				for (int i = 0; i < numReps; i++) {
					dr = gc.Direct(DATA_FILE, latitudeClipAbs, longitudeClipAbs, allDirectStats.get(gc.getName()));
					System.out.println("  Average time for rep " + i + " for " + gc.getName() + " direct was " + String.format("%.5f", dr.MillisecondsStats.getMean()) + " msec (" + String.format("%.5f", dr.MillisecondsStats.getStandardDeviation()) + " stDev)");
				}
				System.out.println("  Finished " + numReps + " reps for " + gc.getName() + " direct.");

				System.out.println("");
				System.out.println("----------------------------------------------------------------------------------");
				System.out.println("Beginning " + numReps + " repititions for " + gc.getName() + " inverse computation.");
				System.out.println("----------------------------------------------------------------------------------");
				InverseResults ir = null;
				for (int i = 0; i < numReps; i++) {
					ir = gc.Inverse(DATA_FILE, latitudeClipAbs, longitudeClipAbs, allInverseStats.get(gc.getName()));
					System.out.println("  Average time for rep " + i + " for " + gc.getName() + " inverse was " + String.format("%.5f", ir.MillisecondsStats.getMean()) + " msec (" + String.format("%.5f", ir.MillisecondsStats.getStandardDeviation()) + " stDev)");
				}
				System.out.println("  Finished " + numReps + " reps for " + gc.getName() + " inverse.");
			}
		} catch (Exception ex){
			System.out.println("Error running tests, error was: " + ex.getLocalizedMessage());
			return false;
		}

		System.out.println("");
		System.out.println("----------------------------------------------------------------------------------");
		System.out.println("Testing extents (this may take awhile)");
		System.out.println("----------------------------------------------------------------------------------");



		for (GeodesicCalculator gc : tests){
			List<Coordinate> errors = new ArrayList<>();
			long numErrors = 0;
			for (double lon = -180; lon < 180; lon += 0.1){
				for (double lat = -90; lat < 90; lat += 0.1){
					try {
						gc.Inverse(0, 0, lat, lon);
					} catch (Exception ex){
						errors.add(new Coordinate(lon, lat));
						numErrors++;
					}
				}
			}
			try {
				MapRenderer.drawMap(gc.getName(), errors, 4096, "./target/" + gc.getName() + ".png");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Results for: " + gc.getName());
			System.out.println("  " + numErrors + " exceptions occurred measuring distance from (0,0) to a 0.1 degree grid across the WGS:84 ellipsoid");

		}



		System.out.println("");

		System.out.println("----------------------------------------------------------------------------------");
		System.out.println("Direct Results");
		System.out.println("----------------------------------------------------------------------------------");
		for (GeodesicCalculator gc : tests){
			Map<String, AggregateSummaryStatistics> das = allDirectStats.get(gc.getName());
			System.out.println("Results for: " + gc.getName());
			System.out.println("  Avg Time Per Conversion: " + String.format("%.5f",das.get("time").getMean()) + " mSec. [" + String.format("%.5f", das.get("time").getStandardDeviation()) + "] stdev.");
			System.out.println("  Avg Longitude Error: " + String.format("%.4e",das.get("longitude").getMean()) + " degrees (abs) longitude  [" + String.format("%.4e", das.get("longitude").getStandardDeviation()) + "] stdev.");
			System.out.println("  Avg Latitude Error: " + String.format("%.4e",das.get("latitude").getMean()) + " degrees (abs) longitude  [" + String.format("%.4e",das.get("latitude").getStandardDeviation()) + "] stdev.");
		}

		System.out.println("----------------------------------------------------------------------------------");
		System.out.println("Inverse Results");
		System.out.println("----------------------------------------------------------------------------------");
		for (GeodesicCalculator gc : tests){
			Map<String, AggregateSummaryStatistics> ias = allInverseStats.get(gc.getName());
			System.out.println("Results for: " + gc.getName());
			int azimuthError = (int)ias.get("azimuth1").getMean();
			System.out.println("  Avg Time Per Conversion: " + String.format("%.5f",ias.get("time").getMean()) + " mSec. [" + String.format("%.5f", ias.get("time").getStandardDeviation()) + "] stdev.");
			System.out.println("  Avg Azimuth 1 Error: " + ((azimuthError == -1) ? "Not Supported" : String.format("%.4e",ias.get("azimuth1").getMean()) + " degrees (abs) heading [" + String.format("%.4e", ias.get("azimuth1").getStandardDeviation()) + "] stdev."));
			System.out.println("  Avg Geodesic Distance Error: " + String.format("%.4e",ias.get("distance").getMean()) + " meters (abs). [" + String.format("%.4e", ias.get("distance").getStandardDeviation()) + "] stdev.");
		}
		return true;
	}

	private void extractTestData()
			throws ZipException {
		File f = new File(DATA_FILE);
		if (!f.exists()){
			f.mkdirs();
			ZipFile zf = new ZipFile(this.getClass().getClassLoader().getResource("GeodTest.zip").getFile());
			zf.extractAll(DATA_DIR);
		}
		File f2 = new File(COASTLINE_SHAPE_FILE);
		if (!f2.exists()){
			f2.mkdirs();
			ZipFile zf = new ZipFile(this.getClass().getClassLoader().getResource("ne_10m_coastline.zip").getFile());
			zf.extractAll(COASTLINE_DIR);
		}
	}
}

