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
import java.util.Objects;

import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.objects.ClassDict;
import net.razorvine.pickle.objects.ClassDictConstructor;

public class PythonObjectConstructor extends ClassDictConstructor {

	private Class<? extends PythonObject> clazz = null;


	public PythonObjectConstructor(String module, String name, Class<? extends PythonObject> clazz){
		super(module, name);

		setClazz(clazz);
	}

	public PythonObject newObject(){
		Class<? extends PythonObject> clazz = getClazz();

		try {
			try {
				Constructor<? extends PythonObject> namedConstructor = clazz.getDeclaredConstructor(String.class, String.class);

				return namedConstructor.newInstance(getModule(), getName());
			} catch(NoSuchMethodException nsme){
				Constructor<? extends PythonObject> constructor = clazz.getDeclaredConstructor();

				return constructor.newInstance();
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
			ClassDictConstructor dictConstructor = (ClassDictConstructor)first;

			ClassDict dict = (ClassDict)dictConstructor.construct(new Object[0]);
			dict.__setstate__(new HashMap<String, Object>()); // Initializes the previously uninitialized "__class__" attribute

			if(isObject(dict) && (second == null)){
				return newObject();
			}
		} else

		if(first instanceof CustomPythonObjectConstructor){
			CustomPythonObjectConstructor dictConstructor = (CustomPythonObjectConstructor)first;

			CustomPythonObject dict = dictConstructor.construct(new Object[0]);

			if(isObject(dict) && (second == null)){
				return newObject();
			}
		}

		throw new PickleException(ClassDictConstructorUtil.getClassName(this) + ".reconstruct(" + first + ", " + second + ")");
	}

	public String getModule(){
		return ClassDictConstructorUtil.getModule(this);
	}

	public String getName(){
		return ClassDictConstructorUtil.getName(this);
	}

	public Class<? extends PythonObject> getClazz(){
		return this.clazz;
	}

	private void setClazz(Class<? extends PythonObject> clazz){
		this.clazz = Objects.requireNonNull(clazz);
	}

	static
	private boolean isObject(ClassDict dict){
		return ("__builtin__.object").equals(dict.getClassName());
	}
}