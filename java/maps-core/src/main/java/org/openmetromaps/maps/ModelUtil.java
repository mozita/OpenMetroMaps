// Copyright 2017 Sebastian Kuerten
//
// This file is part of OpenMetroMaps.
//
// OpenMetroMaps is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// OpenMetroMaps is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with OpenMetroMaps. If not, see <http://www.gnu.org/licenses/>.

package org.openmetromaps.maps;

import java.util.ArrayList;
import java.util.List;

import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.LineNetworkBuilder;
import org.openmetromaps.maps.graph.LineNetworkUtil;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.openmetromaps.maps.painting.core.ColorCode;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.BBoxHelper;
import de.topobyte.adt.geo.Coordinate;
import de.topobyte.lightgeom.lina.Point;
import de.topobyte.viewports.geometry.Rectangle;

public class ModelUtil
{

	public static ColorCode getColor(Line line)
	{
		String sColor = line.getColor();
		return new ColorCode(Integer.decode(sColor));
	}

	public static DataConfig dataConfig(ModelData model)
	{
		List<Coordinate> coords = new ArrayList<>();
		for (Station station : model.stations) {
			coords.add(station.getLocation());
			for (Stop stop : station.getStops()) {
				if (stop.getLocation() != null) {
					coords.add(stop.getLocation());
				}
			}
		}
		BBox bbox = BBoxHelper.minimumBoundingBox(coords);

		coords.sort(new CoordinateComparatorLongitude());
		double medianLon = coords.get(coords.size() / 2).getLongitude();

		coords.sort(new CoordinateComparatorLatitude());
		double medianLat = coords.get(coords.size() / 2).getLatitude();

		Coordinate startPosition = new Coordinate(medianLon, medianLat);

		return new DataConfig(bbox, startPosition);
	}

	public static ViewConfig viewConfig(LineNetwork lineNetwork)
	{
		List<Point> points = new ArrayList<>();
		for (Node node : lineNetwork.getNodes()) {
			points.add(node.location);
		}
		Point min = Points.minimum(points);
		Point max = Points.maximum(points);

		points.sort(new PointComparatorX());
		double medianX = points.get(points.size() / 2).getX();

		points.sort(new PointComparatorY());
		double medianY = points.get(points.size() / 2).getY();

		Rectangle scene = new Rectangle(min.x, min.y, max.x, max.y);

		return new ViewConfig(scene,
				new de.topobyte.viewports.geometry.Coordinate(medianX,
						medianY));
	}

	public static ViewConfig viewConfig(LineNetwork lineNetwork, double width,
			double height)
	{
		List<Point> points = new ArrayList<>();
		for (Node node : lineNetwork.getNodes()) {
			points.add(node.location);
		}

		points.sort(new PointComparatorX());
		double medianX = points.get(points.size() / 2).getX();

		points.sort(new PointComparatorY());
		double medianY = points.get(points.size() / 2).getY();

		Rectangle scene = new Rectangle(0, 0, width, height);

		return new ViewConfig(scene,
				new de.topobyte.viewports.geometry.Coordinate(medianX,
						medianY));
	}

	public static void ensureView(MapModel model)
	{
		if (!model.getViews().isEmpty()) {
			return;
		}

		LineNetworkBuilder builder = new LineNetworkBuilder(model.getData());
		LineNetwork lineNetwork = builder.getGraph();
		List<Node> nodes = lineNetwork.getNodes();

		for (Node node : nodes) {
			Coordinate coord = node.station.getLocation();
			node.location = new Point(coord.lon, coord.lat);
		}
		LineNetworkUtil.calculateAllNeighborLocations(lineNetwork);

		ViewConfig viewConfig = ModelUtil.viewConfig(lineNetwork);
		MapView view = new MapView("Test", lineNetwork, viewConfig);
		CoordinateConversion.convertView(view);
		model.getViews().add(view);
	}

}
