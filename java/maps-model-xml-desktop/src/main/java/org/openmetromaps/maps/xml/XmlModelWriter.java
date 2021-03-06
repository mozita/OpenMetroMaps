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

package org.openmetromaps.maps.xml;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openmetromaps.maps.Edges;
import org.openmetromaps.maps.Interval;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ViewConfig;
import org.openmetromaps.maps.graph.LineNetwork;
import org.openmetromaps.maps.graph.Node;
import org.openmetromaps.maps.model.Coordinate;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.topobyte.lightgeom.lina.Point;

public class XmlModelWriter
{

	public void write(OutputStream os, ModelData data, List<MapView> views)
			throws ParserConfigurationException, TransformerException
	{
		// Create document

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.newDocument();

		// Add data to document

		Element eMain = doc.createElement("omm-file");
		eMain.setAttribute("version", "1.0.0");

		Element eStations = doc.createElement("stations");
		Element eLines = doc.createElement("lines");

		doc.appendChild(eMain);
		eMain.appendChild(eStations);
		eMain.appendChild(eLines);

		Collections.sort(data.stations, new Comparator<Station>() {

			@Override
			public int compare(Station o1, Station o2)
			{
				return o1.getName().compareTo(o2.getName());
			}

		});

		Collections.sort(data.lines, new Comparator<Line>() {

			@Override
			public int compare(Line o1, Line o2)
			{
				return o1.getName().compareTo(o2.getName());
			}

		});

		for (Station station : data.stations) {
			Element eStation = doc.createElement("station");
			eStations.appendChild(eStation);

			Coordinate location = station.getLocation();

			eStation.setAttribute("name", station.getName());
			eStation.setAttribute("lon",
					String.format("%.6f", location.getLongitude()));
			eStation.setAttribute("lat",
					String.format("%.6f", location.getLatitude()));
		}

		for (Line line : data.lines) {
			Element eLine = doc.createElement("line");
			eLines.appendChild(eLine);

			eLine.setAttribute("name", line.getName());
			eLine.setAttribute("color", line.getColor());
			eLine.setAttribute("circular", Boolean.toString(line.isCircular()));

			for (Stop stop : line.getStops()) {
				Element eStop = doc.createElement("stop");
				eLine.appendChild(eStop);

				eStop.setAttribute("station", stop.getStation().getName());
			}
		}

		for (MapView view : views) {
			Element eView = doc.createElement("view");
			eMain.appendChild(eView);

			ViewConfig config = view.getConfig();
			eView.setAttribute("name", view.getName());
			eView.setAttribute("scene-width",
					String.format("%.6f", config.getScene().getWidth()));
			eView.setAttribute("scene-height",
					String.format("%.6f", config.getScene().getHeight()));
			eView.setAttribute("start-x",
					String.format("%.6f", config.getStartPosition().getX()));
			eView.setAttribute("start-y",
					String.format("%.6f", config.getStartPosition().getY()));

			LineNetwork lineNetwork = view.getLineNetwork();
			List<Node> nodes = new ArrayList<>(lineNetwork.getNodes());
			Collections.sort(nodes, new Comparator<Node>() {

				@Override
				public int compare(Node o1, Node o2)
				{
					return o1.station.getName().compareTo(o2.station.getName());
				}

			});

			List<Edges> edgesDefs = view.getEdges();
			for (Edges edgesDef : edgesDefs) {
				Element eEdges = doc.createElement("edges");
				eView.appendChild(eEdges);
				eEdges.setAttribute("line", edgesDef.getLine());

				for (Interval interval : edgesDef.getIntervals()) {
					Element eInterval = doc.createElement("interval");
					eEdges.appendChild(eInterval);
					eInterval.setAttribute("from", interval.getFrom());
					eInterval.setAttribute("to", interval.getTo());
				}
			}

			for (Node node : nodes) {
				Station station = node.station;

				Element eStation = doc.createElement("station");
				eView.appendChild(eStation);

				Point location = node.location;

				eStation.setAttribute("name", station.getName());
				eStation.setAttribute("x",
						String.format("%.6f", location.getX()));
				eStation.setAttribute("y",
						String.format("%.6f", location.getY()));
			}
		}

		// Write document

		StreamResult streamResult = new StreamResult(os);

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");

		DOMSource source = new DOMSource(doc);
		transformer.transform(source, streamResult);
	}

}
