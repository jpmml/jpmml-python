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

import java.util.Arrays;
import java.util.Map;

import net.razorvine.pickle.IObjectConstructor;
import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.objects.ClassDictConstructor;
import org.jpmml.python.PickleUtil;

public class LoadTypeConstructor extends ClassDictConstructor {

	public LoadTypeConstructor(String module, String name){
		super(module, name);
	}

	@Override
	public Object construct(Object[] args){

		if(args.length != 1){
			throw new PickleException(Arrays.deepToString(args));
		}

		String name = (String)args[0];

		IObjectConstructor objectConstructor = findObjectConstructor(name);
		if(objectConstructor != null){
			return objectConstructor;
		}

		throw new PickleException(Arrays.deepToString(args));
	}

	static
	private IObjectConstructor findObjectConstructor(String name){
		Map<String, IObjectConstructor> objectConstructors = PickleUtil.getObjectConstructors();

		switch(name){
			case "PartialType":
				{
					return objectConstructors.get("functools.partial");
				}
			default:
				{
					String[] modules = {"builtins", "types"};

					for(String module : modules){
						IObjectConstructor objectConstructor = objectConstructors.get(module + "." + name);

						if(objectConstructor != null){
							return objectConstructor;
						}
					}

					return null;
				}
		}
	}
}