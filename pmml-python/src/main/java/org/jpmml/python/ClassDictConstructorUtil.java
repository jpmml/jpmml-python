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
import org.jpmml.model.ReflectionUtil;

public class ClassDictConstructorUtil {

	private ClassDictConstructorUtil(){
	}

	static
	public <C extends ClassDict> C construct(ClassDictConstructor dictConstructor, Class<? extends C> clazz){
		String module = getModule(dictConstructor);
		String name = getName(dictConstructor);

		try {
			Constructor<? extends ClassDict> constructor = clazz.getDeclaredConstructor(String.class, String.class);

			return clazz.cast(constructor.newInstance(module, name));
		} catch(ReflectiveOperationException roe){
			throw new RuntimeException(roe);
		}
	}

	static
	public String getClassName(ClassDictConstructor dictConstructor){
		return getModule(dictConstructor) + "." + getName(dictConstructor);
	}

	static
	public String getModule(ClassDictConstructor dictConstructor){
		return ReflectionUtil.getFieldValue(ClassDictConstructorUtil.FIELD_MODULE, dictConstructor);
	}

	static
	public String getName(ClassDictConstructor dictConstructor){
		return ReflectionUtil.getFieldValue(ClassDictConstructorUtil.FIELD_NAME, dictConstructor);
	}

	private static final Field FIELD_MODULE = ReflectionUtil.getField(ClassDictConstructor.class, "module");
	private static final Field FIELD_NAME = ReflectionUtil.getField(ClassDictConstructor.class, "name");
}