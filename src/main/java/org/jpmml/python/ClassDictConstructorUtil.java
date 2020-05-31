/*
 * Copyright (c) 2017 Villu Ruusmann
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
import java.lang.reflect.Field;

import net.razorvine.pickle.objects.ClassDict;
import net.razorvine.pickle.objects.ClassDictConstructor;

public class ClassDictConstructorUtil {

	private ClassDictConstructorUtil(){
	}

	static
	public <C extends ClassDict> C construct(ClassDictConstructor classDictConstructor, Class<? extends C> clazz){
		String module;
		String name;

		try {
			module = (String)getFieldValue(classDictConstructor, "module");
			name = (String)getFieldValue(classDictConstructor, "name");
		} catch(ReflectiveOperationException roe){
			throw new RuntimeException(roe);
		} // End try

		try {
			Constructor<? extends ClassDict> constructor = clazz.getDeclaredConstructor(String.class, String.class);

			return clazz.cast(constructor.newInstance(module, name));
		} catch(ReflectiveOperationException roe){
			throw new RuntimeException(roe);
		}
	}

	static
	private Object getFieldValue(ClassDictConstructor classDictConstructor, String name) throws ReflectiveOperationException {
		Field field = ClassDictConstructor.class.getDeclaredField(name);

		if(!field.isAccessible()){
			field.setAccessible(true);
		}

		return field.get(classDictConstructor);
	}
}