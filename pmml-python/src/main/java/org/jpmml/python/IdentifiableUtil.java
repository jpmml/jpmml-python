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
package org.jpmml.python;

import net.razorvine.pickle.objects.ClassDict;
import net.razorvine.pickle.objects.ClassDictConstructor;

public class IdentifiableUtil {

	private IdentifiableUtil(){
	}

	static
	public Identifiable toIdentifiable(ClassDict dict){
		String dottedName = dict.getClassName();

		return toIdentifiable(dottedName);
	}

	static
	public Identifiable toIdentifiable(ClassDictConstructor dictConstructor){
		String module = ClassDictConstructorUtil.getModule(dictConstructor);
		String name = ClassDictConstructorUtil.getName(dictConstructor);

		return toIdentifiable(module, name);
	}

	static
	public Identifiable toIdentifiable(String dottedName){
		int dot = dottedName.lastIndexOf('.');
		if(dot < 0){
			throw new IllegalArgumentException(dottedName);
		}

		String module = dottedName.substring(0, dot);
		String name = dottedName.substring(dot + 1);

		return toIdentifiable(module, name);
	}

	static
	public Identifiable toIdentifiable(String module, String name){

		if(module == null || name == null){
			throw new IllegalArgumentException();
		} // End if

		if(name.indexOf('.') > -1){
			throw new IllegalArgumentException(name);
		}

		return new Identifiable(){

			@Override
			public String getModule(){
				return module;
			}

			@Override
			public String getName(){
				return name;
			}
		};
	}
}