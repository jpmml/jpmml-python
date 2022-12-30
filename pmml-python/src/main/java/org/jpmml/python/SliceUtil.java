/*
 * Copyright (c) 2022 Villu Ruusmann
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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class SliceUtil {

	private SliceUtil(){
	}

	static
	public <E> List<E> slice(List<E> values, Integer start, Integer stop){
		return slice(values, start, stop, null);
	}

	static
	public <E> List<E> slice(List<E> values, Integer start, Integer stop, Integer step){

		if(start == null){
			start = 0;
		} // End if

		if(stop == null){
			stop = values.size();
		} // End if

		if(step == null){
			step = 1;
		}

		return slice(values, start.intValue(), stop.intValue(), step.intValue());
	}

	static
	public <E> List<E> slice(List<E> values, int start, int stop){
		return slice(values, start, stop, 1);
	}

	static
	public <E> List<E> slice(List<E> values, int start, int stop, int step){

		if(start < 0){
			start = values.size() + start;
		} // End if

		if(stop < 0){
			stop = values.size() + stop;
		} // End if

		// XXX
		if(step <= 0){
			throw new IllegalArgumentException();
		}

		start = Math.max(start, 0);
		stop = Math.min(stop, values.size());

		List<E> result = new ArrayList<>();

		for(int i = start; i < stop; i += step){
			E value = values.get(i);

			result.add(value);
		}

		return result;
	}

	static
	public List<Integer> indices(int start, int stop){
		return indices(start, stop, 1);
	}

	static
	public List<Integer> indices(int start, int stop, int step){
		return slice(SliceUtil.INDICES, start, stop, step);
	}

	private static final List<Integer> INDICES = new AbstractList<Integer>(){

		@Override
		public int size(){
			return Integer.MAX_VALUE;
		}

		@Override
		public Integer get(int index){

			if(index < 0){
				throw new IllegalArgumentException();
			}

			return Integer.valueOf(index);
		}
	};
}