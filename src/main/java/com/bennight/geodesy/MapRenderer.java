package com.bennight.geodesy;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import javax.imageio.ImageIO;

public class MapRenderer{

	public static void drawMap(String title, List<Coordinate> points, int imageWidth, String outputFile) throws Exception {

		File f = new File(test.COASTLINE_SHAPE_FILE);
		ShapefileDataStore shapefile = new ShapefileDataStore(f.toURI().toURL());
		MapContent map = new MapContent();
		map.setTitle(title);
		StyleBuilder styleBuilder = new StyleBuilder();

		LineSymbolizer lsymbol = styleBuilder.createLineSymbolizer(Color.BLACK, 1);
		Style lineStyle = styleBuilder.createStyle(lsymbol);
		FeatureLayer layer = new FeatureLayer(shapefile.getFeatureSource(), lineStyle);
		map.addLayer(layer);


		StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
		FilterFactory ff = CommonFactoryFinder.getFilterFactory2();
		SimpleFeatureSource fs = shapefile.getFeatureSource();
		SimpleFeatureType pointtype = DataUtilities.createType("Error", "the_geom:Point,name:String");
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		ListFeatureCollection col = new ListFeatureCollection(pointtype);

		SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(pointtype);
		int i = 0;
		for (Coordinate c : points) {
			SimpleFeature feature1 = sfb.buildFeature(null);
			sfb.set("the_geom", geometryFactory.createPoint(c));
			sfb.set("name", "(" + c.x + "," + c.y + ")");
			col.add(sfb.buildFeature(String.valueOf(i)));
			i++;
		}


		Stroke stroke2 = sf.createStroke(
				ff.literal(Color.RED),
				ff.literal(1)
		);

		Fill fill2 = sf.createFill(
				ff.literal(Color.RED));

		Mark mark = sf.getSquareMark();
		mark.setFill(fill2);
		mark.setStroke(stroke2);

		Graphic graphic = sf.createDefaultGraphic();
		graphic.graphicalSymbols().clear();
		graphic.graphicalSymbols().add(mark);

		graphic.setSize(ff.literal(2));

		GeometryDescriptor geomDesc = fs.getSchema().getGeometryDescriptor();
		String geometryAttributeName = geomDesc.getLocalName();
		PointSymbolizer sym2 = sf.createPointSymbolizer(graphic, geometryAttributeName);

		Rule rule2 = sf.createRule();
		rule2.symbolizers().add(sym2);
		Rule rules2[] = {rule2};
		FeatureTypeStyle fts2 = sf.createFeatureTypeStyle(rules2);
		Style style2 = sf.createStyle();
		style2.featureTypeStyles().add(fts2);

		map.addLayer(new FeatureLayer(col, style2));
		StreamingRenderer renderer = new StreamingRenderer();
		renderer.setMapContent(map);
		Rectangle imageBounds = null;
		ReferencedEnvelope mapBounds = null;
		try {
			mapBounds = map.getMaxBounds();
			double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
			imageBounds = new Rectangle(0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));

		} catch (Exception e) {
			// failed to access map layers
			throw new RuntimeException(e);
		}

		BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

		Graphics2D gr = image.createGraphics();
		gr.setPaint(Color.WHITE);
		gr.fill(imageBounds);

		try {
			renderer.paint(gr, imageBounds, mapBounds);
			File fileToSave = new File(outputFile);
			if (fileToSave.exists()){
				fileToSave.delete();
			}
			ImageIO.write(image, "png", fileToSave);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}
}