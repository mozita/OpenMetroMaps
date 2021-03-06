// Copyright 2018 Sebastian Kuerten
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

package org.openmetromaps.maps.morpher;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.image.ImageUtil;

import de.topobyte.system.utils.SystemPaths;

public class TestBerlinExport
{

	public static void main(String[] args) throws Exception
	{
		Path berlin = SystemPaths.HOME
				.resolve("github/OpenMetroMapsData/berlin");

		MapModel geographic = Util.load(berlin.resolve("geographic.xml"));
		MapModel schematic = Util.load(berlin.resolve("schematic.xml"));

		int width = 1440;
		int height = 1080;
		int x = 250;
		int y = 100;
		double zoom = 3;

		ImageUtil.createPng(geographic, Paths.get("/tmp/geographic.png"), width,
				height, x, y, zoom);
		ImageUtil.createPng(schematic, Paths.get("/tmp/schematic.png"), width,
				height, x, y, zoom);
	}

}
