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
package pandas.core;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import numpy.core.NDArray;
import org.jpmml.python.HasArray;
import org.jpmml.python.PythonObject;

public class MaskedArray extends PythonObject implements HasArray {

	public MaskedArray(String module, String name){
		super(module, name);
	}

	@Override
	public List<?> getArrayContent(){
		NDArray data = getData();
		NDArray mask = getMask();

		List<?> dataValues = data.getArrayContent();
		List<?> maskValues = mask.getArrayContent();

		List<Object> result = new AbstractList<Object>(){

			@Override
			public int size(){
				return dataValues.size();
			}

			@Override
			public Object get(int index){
				Boolean mask = (Boolean)maskValues.get(index);

				if(mask.booleanValue()){
					return null;
				}

				return dataValues.get(index);
			}
		};

		return result;
	}

	@Override
	public int[] getArrayShape(){
		NDArray data = getData();
		NDArray mask = getMask();

		int[] dataShape = data.getArrayShape();
		int[] maskShape = mask.getArrayShape();

		// XXX
		if(!Arrays.equals(dataShape, maskShape)){
			throw new IllegalArgumentException("Expected matching array shapes, got " + Arrays.toString(dataShape) + " and " + Arrays.toString(maskShape));
		}

		return dataShape;
	}

	@Override
	public Object getArrayType(){
		Object dtype = getDType();

		if(dtype == null){
			NDArray data = getData();

			return data.getDescr();
		}

		return dtype;
	}

	public NDArray getData(){
		return get("_data", NDArray.class);
	}

	public Object getDType(){
		return getOptionalObject("_dtype");
	}

	public NDArray getMask(){
		return get("_mask", NDArray.class);
	}
}