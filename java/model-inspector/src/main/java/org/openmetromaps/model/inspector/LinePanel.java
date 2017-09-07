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

package org.openmetromaps.model.inspector;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openmetromaps.model.DraftLine;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public class LinePanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	private JLabel labelName = new JLabel("Name:");
	private JLabel displayName = new JLabel();
	private JLabel labelSource = new JLabel("Source:");
	private JButton displaySource = new JButton();
	private JLabel labelNumStations = new JLabel("# Stations:");
	private JLabel displayNumStations = new JLabel();

	public LinePanel()
	{
		super(new GridBagLayout());
		init();
	}

	private void init()
	{
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		displaySource.setBorderPainted(false);
		displaySource.setOpaque(false);
		displaySource.setBackground(Color.WHITE);
		displaySource.setHorizontalAlignment(SwingConstants.LEFT);
		displaySource.setBorder(null);

		c.fill(GridBagConstraints.HORIZONTAL).weight(1, 0);
		c.gridX(1);
		add(labelName, c.getConstraints());
		c.gridX(2);
		add(displayName, c.getConstraints());

		c.gridY(1);
		c.gridX(1);
		add(labelSource, c.getConstraints());
		c.gridX(2);
		add(displaySource, c.getConstraints());

		c.gridY(2);
		c.gridX(1);
		add(labelNumStations, c.getConstraints());
		c.gridX(2);
		add(displayNumStations, c.getConstraints());

		c.gridY(3);
		c.fill(GridBagConstraints.BOTH).weight(1, 1);
		add(new JPanel(), c.getConstraints());
	}

	public void setLine(DraftLine line)
	{
		final OsmRelation source = line.getSource();
		Map<String, String> tags = OsmModelUtil.getTagsAsMap(source);
		String name = tags.get("ref");

		displayName.setText(name);
		displaySource.setText(String.format("Relation %d", source.getId()));
		displayNumStations
				.setText(String.format("%d", line.getStations().size()));

		displaySource.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				openRelationInBrowser(source);
			}

		});
	}

	protected void openRelationInBrowser(OsmRelation source)
	{
		try {
			URI uri = new URI(
					String.format("http://www.openstreetmap.org/relation/%d",
							source.getId()));
			Desktop.getDesktop().browse(uri);
		} catch (IOException | URISyntaxException e1) {
			// ignore
		}
	}

}