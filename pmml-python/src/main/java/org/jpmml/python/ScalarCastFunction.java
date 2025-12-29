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

import java.util.Set;

import numpy.core.ScalarUtil;

public class ScalarCastFunction<E> extends CastFunction<E> {

	public ScalarCastFunction(Class<? extends E> clazz){
		super(requireScalar(clazz));
	}

	@Override
	public E apply(Object object){
		object = ScalarUtil.decode(object);

		return super.apply(object);
	}

	static
	public boolean isScalar(Class<?> clazz){
		return ScalarCastFunction.SCALAR_CLASSES.contains(clazz);
	}

	static
	private <E> Class<E> requireScalar(Class<E> clazz){

		if(!isScalar(clazz)){
			throw new IllegalArgumentException();
		}

		return clazz;
	}

	private static final Set<Class<?>> SCALAR_CLASSES = Set.of(
		Object.class,
		// Boolean
		Boolean.class,
		// Primitive numbers
		Number.class,
		Byte.class,
		Short.class,
		Character.class, // XXX
		Integer.class,
		Long.class,
		Float.class,
		Double.class,
		// String
		String.class
	);
}