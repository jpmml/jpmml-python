/*
 * Copyright (c) 2015 Villu Ruusmann
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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;

import net.razorvine.pickle.IObjectConstructor;
import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.objects.ClassDict;
import net.razorvine.pickle.objects.ClassDictConstructor;

public class PythonObjectConstructor implements IObjectConstructor {

	private String module = null;

	private String name = null;

	private Class<? extends PythonObject> clazz = null;


	public PythonObjectConstructor(String module, String name, Class<? extends PythonObject> clazz){
		setModule(module);
		setName(name);
		setClazz(clazz);
	}

	public PythonObject newObject(){
		Class<? extends PythonObject> clazz = getClazz();

		if(clazz == null){
			throw new RuntimeException();
		}

		try {
			try {
				Constructor<? extends PythonObject> namedConstructor = clazz.getConstructor(String.class, String.class);

				return namedConstructor.newInstance(getModule(), getName());
			} catch(NoSuchMethodException nsme){
				return clazz.newInstance();
			}
		} catch(ReflectiveOperationException roe){
			throw new RuntimeException(roe);
		}
	}

	@Override
	public PythonObject construct(Object[] args){

		if(args.length != 0){
			throw new PickleException(Arrays.deepToString(args));
		}

		return newObject();
	}

	public PythonObject reconstruct(Object first, Object second){

		if(first instanceof ClassDictConstructor){
			ClassDictConstructor constructor = (ClassDictConstructor)first;

			ClassDict dict = (ClassDict)constructor.construct(new Object[0]);
			dict.__setstate__(new HashMap<String, Object>()); // Initializes the previously uninitialized "__class__" attribute

			if(isObject(dict) && (second == null)){
				return newObject();
			}
		} else

		if(first instanceof CustomPythonObjectConstructor){
			CustomPythonObjectConstructor constructor = (CustomPythonObjectConstructor)first;

			CustomPythonObject dict = constructor.construct(new Object[0]);

			if(isObject(dict) && (second == null)){
				return newObject();
			}
		}

		throw new PickleException(getModule() + "." + getName() + ".reconstruct(" + first + ", " + second + ")");
	}

	public String getModule(){
		return this.module;
	}

	private void setModule(String module){
		this.module = module;
	}

	public String getName(){
		return this.name;
	}

	private void setName(String name){
		this.name = name;
	}

	public Class<? extends PythonObject> getClazz(){
		return this.clazz;
	}

	private void setClazz(Class<? extends PythonObject> clazz){
		this.clazz = clazz;
	}

	static
	private boolean isObject(ClassDict dict){
		return ("__builtin__.object").equals(dict.getClassName());
	}
}