/*
 * Copyright (c) 2016 Villu Ruusmann
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
package numpy.core;

import org.jpmml.python.ClassDictUtil;
import org.jpmml.python.CustomPythonObject;
import org.jpmml.python.Identifiable;

public class UFunc extends CustomPythonObject implements Identifiable {

	public UFunc(String module, String name){
		super(module, name);
	}

	@Override
	public void __init__(Object[] args){
		super.__setstate__(ClassDictUtil.createAttributeMap(INIT_ATTRIBUTES, args));
	}

	@Override
	public String getModule(){
		return (String)get("module");
	}

	@Override
	public String getName(){
		return (String)get("name");
	}

	private static final String[] INIT_ATTRIBUTES = {
		"module",
		"name"
	};
}