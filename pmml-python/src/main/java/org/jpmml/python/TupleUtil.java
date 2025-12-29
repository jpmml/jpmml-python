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

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class TupleUtil {

	private TupleUtil(){
	}

	static
	public Object extractElement(Object[] tuple, int i){
		return tuple[i];
	}

	static
	public <E> E extractElement(Object[] tuple, int i, Function<Object, E> castFunction){
		Object value = extractElement(tuple, i);

		try {
			return castFunction.apply(value);
		} catch(ClassCastException cce){
			throw new PythonException("Tuple contains an unsupported value (" + ClassDictUtil.formatClass(value) + ")", cce);
		}
	}

	static
	public String extractStringElement(Object[] tuple, int i){
		return extractElement(tuple, i, new ScalarCastFunction<>(String.class));
	}

	static
	public Object extractObjectElement(Object[] tuple, int i){
		return extractElement(tuple, i, new ScalarCastFunction<>(Object.class));
	}

	static
	public List<?> extractElementList(List<Object[]> tuples, int i){
		Function<Object[], Object> function = new Function<Object[], Object>(){

			@Override
			public Object apply(Object[] tuple){
				return tuple[i];
			}
		};

		return Lists.transform(tuples, function);
	}

	static
	public <E> List<? extends E> extractElementList(List<Object[]> tuples, int i, Function<Object, E> castFunction){
		List<?> values = extractElementList(tuples, i);

		Function<Object, E> function = new Function<Object, E>(){

			@Override
			public E apply(Object value){

				try {
					 return castFunction.apply(value);
				} catch(ClassCastException cce){
					throw new PythonException("Tuple contains an unsupported value (" + ClassDictUtil.formatClass(value) +")", cce);
				}
			}
		};

		return Lists.transform(values, function);
	}
}
