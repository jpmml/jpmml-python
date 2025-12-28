/*
 * Copyright (c) 2025 Villu Ruusmann
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.razorvine.pickle.PickleException;
import org.jpmml.python.PythonObjectConstructor;

public class StringDtypeConstructor extends PythonObjectConstructor {

	public StringDtypeConstructor(String module, String name){
		super(module, name, StringDtype.class);
	}

	@Override
	public StringDtype newObject(){
		return (StringDtype)super.newObject();
	}

	@Override
	public StringDtype construct(Object[] args){
		StringDtype dict = newObject();

		if(args.length == 0){
			// Ignored
		} else

		if(args.length == 2){
			HashMap<String, Object> state = new LinkedHashMap<>();
			state.put("_storage", args[0]);
			state.put("_na_value", args[1]);

			dict.__setstate__(state);
		} else

		{
			throw new PickleException(Arrays.deepToString(args));
		}

		return dict;
	}
}