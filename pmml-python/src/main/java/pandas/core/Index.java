/*
 * Copyright (c) 2019 Villu Ruusmann
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

import java.util.ArrayList;
import java.util.List;

import net.razorvine.pickle.objects.ClassDictConstructor;
import numpy.DType;
import numpy.core.NDArray;
import org.jpmml.python.ClassDictConstructorUtil;
import org.jpmml.python.CustomPythonObject;
import org.jpmml.python.PythonObject;

public class Index extends CustomPythonObject {

	public Index(String module, String name){
		super(module, name);
	}

	@Override
	public void __init__(Object[] args){
		super.__setstate__(createAttributeMap(INIT_ATTRIBUTES, args));
	}

	@Override
	public void __setstate__(Object[] args){
		super.__setstate__(createAttributeMap(SETSTATE_ATTRIBUTES, args));
	}

	public DType getDescr(){
		Data data = getData();

		return data.getDescr();
	}

	public List<?> getValues(){
		Data data = getData();

		return data.getValues();
	}

	public String getCls(){
		ClassDictConstructor dictConstructor = get("cls", ClassDictConstructor.class);

		return ClassDictConstructorUtil.getClassName(dictConstructor);
	}

	public Data getData(){
		String cls = getCls();

		switch(cls){
			case "pandas.core.indexes.range.RangeIndex":
				return getPythonObject("data", this.new RangeData());
			default:
				return getPythonObject("data", this.new NDArrayData(getPythonModule(), "data"));
		}
	}

	public class RangeData extends Data {

		public RangeData(){
			super("pandas.core.indexes.range", "RangeIndex");
		}

		@Override
		public DType getDescr(){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<?> getValues(){
			int start = getStart();
			int stop = getStop();
			int step = getStep();

			List<Integer> result = new ArrayList<>();

			for(int i = start; i < stop; i += step){
				result.add(i);
			}

			return result;
		}

		public Integer getStart(){
			return getInteger("start");
		}

		public Integer getStop(){
			return getInteger("stop");
		}

		public Integer getStep(){
			return getInteger("step");
		}
	}

	public class NDArrayData extends Data {

		public NDArrayData(String module, String name){
			super(module, name);
		}

		@Override
		public DType getDescr(){
			NDArray data = getData();

			return (DType)data.getDescr();
		}

		@Override
		public List<?> getValues(){
			NDArray data = getData();

			return data.getArrayContent();
		}

		public NDArray getData(){
			return get("data", NDArray.class);
		}
	}

	abstract
	public class Data extends PythonObject {

		public Data(String module, String name){
			super(module, name);
		}

		abstract
		public DType getDescr();

		abstract
		public List<?> getValues();
	}

	private static final String[] INIT_ATTRIBUTES = {
		"cls",
		"data"
	};

	private static final String[] SETSTATE_ATTRIBUTES = {
		"state"
	};
}