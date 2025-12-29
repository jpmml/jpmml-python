/*
 * Copyright (c) 2018 Villu Ruusmann
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
package builtins;

import net.razorvine.pickle.objects.ClassDict;
import net.razorvine.pickle.objects.ClassDictConstructor;
import org.jpmml.python.PythonEnumConstructor;

public class GetAttrConstructor extends ClassDictConstructor {

	public GetAttrConstructor(String module, String name){
		super(module, name);
	}

	@Override
	public Object construct(Object[] args){

		if(args.length == 2){

			// Python 3.11+
			if(args[0] instanceof PythonEnumConstructor){
				PythonEnumConstructor enumConstructor = (PythonEnumConstructor)args[0];
				String name = (String)args[1];

				return enumConstructor.construct(new Object[]{name});
			}

			ClassDict dict = (ClassDict)args[0];
			String name = (String)args[1];

			return dict.get(name);
		} else

		if(args.length == 3){
			ClassDict dict = (ClassDict)args[0];
			String name = (String)args[1];
			Object _default = args[2];

			return dict.getOrDefault(name, _default);
		}

		return super.construct(args);
	}
}