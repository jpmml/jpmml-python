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
package types;

import java.util.Arrays;

import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.objects.ClassDict;
import net.razorvine.pickle.objects.ClassDictConstructor;
import org.jpmml.python.ClassDictUtil;

public class MethodTypeConstructor extends ClassDictConstructor {

	public MethodTypeConstructor(String module, String name){
		super(module, name);
	}

	@Override
	public Object construct(Object[] args){

		if(args.length != 2){
			throw new PickleException(Arrays.deepToString(args));
		}

		FunctionType func = (FunctionType)args[0];
		ClassDict self = (ClassDict)args[1];

		MethodType dict = new MethodType();
		dict.__setstate__(ClassDictUtil.createAttributeMap(SETSTATE_ATTRIBUTES, args));

		return dict;
	}

	private static final String[] SETSTATE_ATTRIBUTES = {
		"func",
		"self"
	};
}