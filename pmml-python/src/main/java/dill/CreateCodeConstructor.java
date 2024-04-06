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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.objects.ClassDict;
import net.razorvine.pickle.objects.ClassDictConstructor;
import org.jpmml.python.ClassDictUtil;
import org.jpmml.python.CythonObjectUtil;
import types.CodeType;

public class CreateCodeConstructor extends ClassDictConstructor {

	public CreateCodeConstructor(String module, String name){
		super(module, name);
	}

	@Override
	public Object construct(Object[] args){
		ClassDict dict = new CodeType();
		dict.__setstate__(createState(args));

		return dict;
	}

	static
	private HashMap<String, Object> createState(Object[] args){

		if(!(args[0] instanceof Integer)){
			args = ClassDictUtil.extractArgs(args, 1, args.length);
		}

		for(int i = 0; i < 4; i++){
			String[] attributes = CreateCodeConstructor.STATE_ATTRIBUTES[i];

			if(attributes.length == args.length){
				return CythonObjectUtil.createState(attributes, args);
			}
		}

		throw new PickleException(Arrays.deepToString(args));
	}

	static
	private String[][] prepareNames(String[][] table){
		String[][] result = new String[4][];

		for(int i = 0; i < 4; i++){
			List<String> names = new ArrayList<>();

			for(int j = 0; j < table.length; j++){
				String[] row = table[j];

				String name = null;

				if(row.length == 1){
					name = row[0];
				} else

				if(row.length == 4){
					name = row[i];
				} else

				{
					throw new IllegalArgumentException();
				} // End if

				if(name != null){
					names.add(name);
				}
			}

			result[i] = names.toArray(new String[names.size()]);
		}

		return result;
	}

	private static final String[][] STATE_ATTRIBUTES = prepareNames(new String[][]{
		{"argcount"},
		{null, "posonlyargcount", "posonlyargcount", "posonlyargcount"},
		{"kwonlyargcount"},
		{"nlocals"},
		{"stacksize"},
		{"flags"},
		{"code"},
		{"consts"},
		{"names"},
		{"varnames"},
		{"filename"},
		{"name"},
		{null, null, null, "qualname"},
		{"firstlineno"},
		{"lnotab", "lnotab", "linetable", "linetable"},
		{null, null, null, "exceptiontable"},
		{"freevars"},
		{"cellvars"}
	});
}