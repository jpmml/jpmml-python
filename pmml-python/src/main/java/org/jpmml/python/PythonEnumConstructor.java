/*
 * Copyright (c) 2022 Villu Ruusmann
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
import java.util.Collections;
import java.util.HashMap;

import net.razorvine.pickle.PickleException;

public class PythonEnumConstructor extends PythonObjectConstructor {

	public PythonEnumConstructor(String module, String name){
		this(module, name, PythonEnum.class);
	}

	public PythonEnumConstructor(String module, String name, Class<? extends PythonEnum> clazz){
		super(module, name, clazz);
	}

	@Override
	public PythonEnum newObject(){
		return (PythonEnum)super.newObject();
	}

	@Override
	public PythonEnum construct(Object[] args){

		if(args.length != 1){
			throw new PickleException(Arrays.deepToString(args));
		}

		PythonEnum _enum = newObject();
		_enum.__setstate__(new HashMap<>(Collections.singletonMap("value", args[0])));

		return _enum;
	}
}