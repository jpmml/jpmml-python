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

import builtins.Type;
import builtins.TypeConstructor;
import numpy.core.ScalarUtil;

public class DTypeCastFunction<E extends TypeInfo> extends CastFunction<E> {

	public DTypeCastFunction(Class<? extends E> clazz){
		super(clazz);
	}

	@Override
	public E apply(Object object){
		object = ScalarUtil.decode(object);

		if(object instanceof TypeConstructor){
			TypeConstructor typeConstructor = (TypeConstructor)object;

			object = typeConstructor.construct();
		} else

		if(object instanceof String){
			String string = (String)object;

			object = Type.forClassName(string);
		}

		return super.apply(object);
	}
}