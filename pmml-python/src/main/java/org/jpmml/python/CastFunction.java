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
package org.jpmml.python;

import java.util.Objects;

import com.google.common.base.Function;

abstract
public class CastFunction<E> implements Function<Object, E> {

	private Class<? extends E> clazz = null;


	public CastFunction(Class<? extends E> clazz){
		setClazz(clazz);
	}

	abstract
	protected String formatMessage(Object object);

	@Override
	public E apply(Object object){
		Class<? extends E> clazz = getClazz();

		try {
			object = CastUtil.deepCastTo(object, clazz);

			return clazz.cast(object);
		} catch(ClassCastException cce){
			throw createPythonException(formatMessage(object), cce)
				.fillInStackTrace();
		}
	}

	public PythonException createPythonException(String message, ClassCastException cause){
		return new PythonException(message, cause);
	}

	public Class<? extends E> getClazz(){
		return this.clazz;
	}

	private void setClazz(Class<? extends E> clazz){
		this.clazz = Objects.requireNonNull(clazz);
	}
}