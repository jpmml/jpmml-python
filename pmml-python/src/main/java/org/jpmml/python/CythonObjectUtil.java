/*
 * Copyright (c) 2024 Villu Ruusmann
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

public class CythonObjectUtil {

	private CythonObjectUtil(){
	}

	static
	public HashMap<String, Object> createState(String[] attributes, Object[] args){

		if(attributes.length != args.length){
			throw new PickleException(Arrays.deepToString(args));
		}

		HashMap<String, Object> result = new LinkedHashMap<>();

		for(int i = 0; i < attributes.length; i++){
			result.put(attributes[i], args[i]);
		}

		return result;
	}
}