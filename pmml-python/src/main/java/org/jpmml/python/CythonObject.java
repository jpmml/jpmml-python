/*
 * Copyright (c) 2015 Villu Ruusmann
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
package org.jpmml.python;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.razorvine.pickle.PickleException;

abstract
public class CythonObject extends PythonObject {

	public CythonObject(String module, String name){
		super(module, name);

		reset();
	}

	public void __init__(Object[] args){

		if(args.length > 0){
			throw new PickleException(ClassDictUtil.formatMember(this, "__init__(" + Arrays.deepToString(args) + ")"));
		}

		reset();
	}

	public void __setstate__(Object[] args){

		if(args.length > 0){
			throw new PickleException(ClassDictUtil.formatMember(this, "__setstate__(" + Arrays.deepToString(args) + ")"));
		}

		reset();
	}

	public HashMap<String, Object> __getstate__(){
		HashMap<String, Object> result = new LinkedHashMap<>(this);
		result.remove("__class__");

		return result;
	}

	public void __setstate__(String[] attributes, Object[] args){
		__setstate__(CythonObjectUtil.createState(attributes, args));
	}

	@Override
	public void __setstate__(HashMap<String, Object> newState){
		HashMap<String, Object> state = __getstate__();

		// The state is additive
		state.putAll(newState);

		super.__setstate__(state);
	}

	private void reset(){
		HashMap<String, Object> state = new HashMap<>();

		super.__setstate__(state);
	}
}