/*
 * Copyright (c) 2021 Villu Ruusmann
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
package pandas.core;

import java.util.List;

import builtins.Slice;
import org.jpmml.python.ClassDictUtil;
import org.jpmml.python.CustomPythonObject;

public class Block extends CustomPythonObject {

	public Block(String module, String name){
		super(module, name);
	}

	@Override
	public void __init__(Object[] args){

		if(args.length == 2){
			args = new Object[]{args[0], args[1], null};
		}

		super.__setstate__(ClassDictUtil.createAttributeMap(INIT_ATTRIBUTES, args));
	}

	public Slice getSlice(){
		return get("slice", Slice.class);
	}

	public List<?> getValues(){
		return getArray("values");
	}

	private static final String[] INIT_ATTRIBUTES = {
		"values",
		"placement",
		"ndim"
	};
}