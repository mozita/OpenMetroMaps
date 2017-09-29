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

package org.openmetromaps.maps.actions;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.maps.MapViewer;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;
import org.openmetromaps.maps.xml.XmlModelReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.swing.util.action.SimpleAction;

public class OpenAction extends SimpleAction
{

	final static Logger logger = LoggerFactory.getLogger(OpenAction.class);

	private static final long serialVersionUID = 1L;

	private MapViewer mapViewer;

	public OpenAction(MapViewer mapViewer)
	{
		super("Open", "Open a file");
		this.mapViewer = mapViewer;
		setIcon("res/images/24/document-open.png");
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Window frame = mapViewer.getFrame();
		JFileChooser chooser = new JFileChooser();
		int value = chooser.showOpenDialog(frame);
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			logger.debug("attempting to open document from file: " + file);

			try {
				FileInputStream is = new FileInputStream(file);
				XmlModel xmlModel = new XmlModelReader().read(is);
				is.close();

				ModelData data = new XmlModelConverter().convert(xmlModel);
				mapViewer.setModel(data);
				mapViewer.getMap().repaint();
			} catch (ParserConfigurationException | SAXException
					| IOException e) {
				logger.error("Error while loading file", e);
				// TODO: display an error dialog
			}
		}
	}

}