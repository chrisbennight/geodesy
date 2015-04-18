package com.bennight.geodesy;

import com.bennight.geodesy.GeodesicImpl.GeographicLib;
import com.bennight.geodesy.GeodesicImpl.Geotools;
import com.vividsolutions.jts.geom.Coordinate;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.sf.geographiclib.Geodesic;

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
	private static final int numReps = 10;
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

		Map<String, List<DirectResults>> directResults = new HashMap<>(tests.length);
		Map<String, List<InverseResults>> inverseResults = new HashMap<>(tests.length);

		try {
			for (GeodesicCalculator gc : tests) {

				System.out.println("");
				System.out.println("Warming up JVM for: " + gc.getName() + " direct");
				DirectResults dr = gc.Direct(DATA_FILE, latitudeClipAbs, longitudeClipAbs);

				dr = null;
				directResults.put(gc.getName(), new ArrayList<DirectResults>(numReps));

				System.out.println("----------------------------------------------------------------------------------");
				System.out.println("Beginning " + numReps + " repititions for " + gc.getName() + " direct computation.");
				System.out.println("----------------------------------------------------------------------------------");
				for (int i = 0; i < numReps; i++) {
					dr = gc.Direct(DATA_FILE, latitudeClipAbs, longitudeClipAbs);
					System.out.println("  Time for rep " + i + " for " + gc.getName() + " direct was " + dr.Milliseconds / 1000d + " seconds (" + dr.Count + " conversions)");
					directResults.get(gc.getName()).add(dr);
				}
				System.out.println("  Finished " + numReps + " reps for " + gc.getName() + " direct.");


				System.out.println("");
				System.out.println("Warming up JVM for: " + gc.getName() + " inverse");
				InverseResults ir = gc.Inverse(DATA_FILE, latitudeClipAbs, longitudeClipAbs);

				ir = null;
				inverseResults.put(gc.getName(), new ArrayList<InverseResults>(numReps));
				System.out.println("----------------------------------------------------------------------------------");
				System.out.println("Beginning " + numReps + " repititions for " + gc.getName() + " inverse computation.");
				System.out.println("----------------------------------------------------------------------------------");
				for (int i = 0; i < numReps; i++) {
					ir = gc.Inverse(DATA_FILE, latitudeClipAbs, longitudeClipAbs);
					System.out.println("  Time for rep " + i + " for " + gc.getName() + " inverse was " + ir.Milliseconds / 1000d + " seconds (" + ir.Count + " conversions)");
					inverseResults.get(gc.getName()).add(ir);
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
			System.out.println("Results for: " + gc.getName());
			long totalTime = 0;
			long totalCounts = 0;
			for (DirectResults dr : directResults.get(gc.getName())){
				totalTime += dr.Milliseconds;
				totalCounts = dr.Count;
			}
			DirectResults dresults = directResults.get(gc.getName()).get(0);
			System.out.println("  Avg Time Per Conversion: " + (double)totalTime / totalCounts + " mSec.");
			System.out.println("  Avg Longitude Error: " + dresults.Longtidue2Error / dresults.Count + " degrees (abs) longitude.");
			System.out.println("  Avg Latitude Error: " + dresults.Lattitude2Error / dresults.Count + " degrees (abs) lattitude.");
		}

		System.out.println("----------------------------------------------------------------------------------");
		System.out.println("Inverse Results");
		System.out.println("----------------------------------------------------------------------------------");
		for (GeodesicCalculator gc : tests){
			System.out.println("Results for: " + gc.getName());
			long totalTime = 0;
			long totalCounts = 0;
			for (InverseResults ir : inverseResults.get(gc.getName())){
				totalTime += ir.Milliseconds;
				totalCounts = ir.Count;
			}
			InverseResults iresults = inverseResults.get(gc.getName()).get(0);

			System.out.println("  Avg Time Per Conversion: " + (double)totalTime / totalCounts + " mSec.");
			System.out.println("  Avg Azimuth 1 Error: " + iresults.Azimuth1Error / iresults.Count + " degrees (abs) heading.");
			System.out.println("  Avg Geodesic Distance Error: " + iresults.GeodesicDistanceError / iresults.Count + " meters (abs).");
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

