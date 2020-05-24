/*
 * Copyright (c) 2016 Villu Ruusmann
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
package joblib;

import java.io.IOException;
import java.io.InputStream;

import numpy.DType;
import numpy.core.NDArray;
import numpy.core.NDArrayUtil;
import org.jpmml.python.PyClassDict;

public class NumpyArrayWrapper extends PyClassDict {

	public NumpyArrayWrapper(String module, String name){
		super(module, name);
	}

	public NDArray toArray(InputStream is) throws IOException {
		DType dtype = getDType();
		Object[] shape = getShape();
		String order = getOrder();

		Object descr = dtype.toDescr();
		Boolean fortranOrder = parseOrder(order);

		Object data = NDArrayUtil.parseData(is, descr, shape);

		NDArray array = new NDArray();
		array.__setstate__(new Object[]{null, shape, descr, fortranOrder, data});

		return array;
	}

	public DType getDType(){
		return get("dtype", DType.class);
	}

	public Object[] getShape(){
		return getTuple("shape");
	}

	public String getOrder(){
		return getString("order");
	}

	static
	private Boolean parseOrder(String order){

		switch(order){
			case "C":
				return Boolean.FALSE;
			case "F":
				return Boolean.TRUE;
			default:
				throw new IllegalArgumentException(order);
		}
	}
}