/*
 * Copyright (c) 2023 Villu Ruusmann
 *
 * This file is part of JPMML-Python
 *
 * JPMML-Python is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-Python is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-Python.  If not, see <http://www.gnu.org/licenses/>.
 */
package numpy.random;

import java.util.HashMap;

import org.jpmml.python.CustomPythonObject;

public class Generator extends CustomPythonObject {

	public Generator(String module, String name){
		super(module, name);
	}

	@Override
	public void __init__(Object[] args){
		Object bitGenerator = BitGeneratorUtil.create(args);

		HashMap<String, Object> attributes = new HashMap<>();
		attributes.put("bit_generator", bitGenerator);

		super.__setstate__(attributes);
	}
}