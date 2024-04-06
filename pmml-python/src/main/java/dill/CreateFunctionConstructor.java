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
package dill;

import java.util.HashMap;

import net.razorvine.pickle.objects.ClassDict;
import net.razorvine.pickle.objects.ClassDictConstructor;
import org.jpmml.python.CythonObjectUtil;
import types.FunctionType;

public class CreateFunctionConstructor extends ClassDictConstructor {

	public CreateFunctionConstructor(String module, String name){
		super(module, name);
	}

	@Override
	public Object construct(Object[] args){
		ClassDict dict = new FunctionType();
		dict.__setstate__(createState(args));

		return dict;
	}

	static
	private HashMap<String, Object> createState(Object[] args){
		return CythonObjectUtil.createState(CreateFunctionConstructor.STATE_ATTRIBUTES, args);
	}

	private static final String[] STATE_ATTRIBUTES = {
		"code",
		"globals",
		"name",
		"defaults",
		"closure"
	};
}