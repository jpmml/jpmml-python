/*
 * Copyright (c) 2026 Villu Ruusmann
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
package pyarrow.lib;

import java.util.Arrays;

import builtins.Type;
import net.razorvine.pickle.PickleException;
import org.jpmml.python.PythonObjectConstructor;
import pyarrow.Types;

public class TypeForAliasConstructor extends PythonObjectConstructor {

	public TypeForAliasConstructor(String module, String name){
		super(module, name, Type.class);
	}

	@Override
	public Type newObject(){
		throw new UnsupportedOperationException();
	}

	@Override
	public Type construct(Object[] args){

		if(args.length == 1){
			String className = (String)args[0];

			switch(className){
				case "large_string":
					className = Types.LARGE_STRING;
					break;
				default:
					break;
			}

			return Type.forClassName(className);
		} else

		{
			throw new PickleException(Arrays.deepToString(args));
		}
	}
}