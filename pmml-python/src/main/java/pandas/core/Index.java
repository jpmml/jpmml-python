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

import java.util.List;
import java.util.Map;

import net.razorvine.pickle.objects.ClassDictConstructor;
import numpy.core.NDArray;
import org.jpmml.python.ClassDictConstructorUtil;
import org.jpmml.python.ClassDictUtil;
import org.jpmml.python.CythonObject;
import org.jpmml.python.HasArray;
import org.jpmml.python.PythonObject;
import org.jpmml.python.SliceUtil;

public class Index extends CythonObject implements HasArray {

	public Index(String module, String name){
		super(module, name);
	}

	@Override
	public void __init__(Object[] args){
		super.__setstate__(INIT_ATTRIBUTES, args);
	}

	@Override
	public void __setstate__(Object[] args){
		super.__setstate__(SETSTATE_ATTRIBUTES, args);
	}

	@Override
	public List<?> getArrayContent(){
		return getValues();
	}

	@Override
	public int[] getArrayShape(){
		List<?> values = getArrayContent();

		return new int[]{values.size()};
	}

	@Override
	public Object getArrayType(){
		return getDescr();
	}

	public Object getDescr(){
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

	private <E extends PythonObject> E getPythonObject(String name, E object){
		Map<String, ?> dict = getDict(name);

		if(dict.containsKey("__class__")){
			throw new IllegalArgumentException("Dict attribute \'" + ClassDictUtil.formatMember(this, name) + "\' has a non-dict value (" + ClassDictUtil.formatClass(dict) + ")");
		}

		object.update(dict);

		return object;
	}

	public class RangeData extends Data {

		public RangeData(){
			super("pandas.core.indexes.range", "RangeIndex");
		}

		@Override
		public Object getDescr(){
			return "i4";
		}

		@Override
		public List<?> getValues(){
			int start = getStart();
			int stop = getStop();
			int step = getStep();

			return SliceUtil.indices(start, stop, step);
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
		public Object getDescr(){
			NDArray data = getData();

			return data.getDescr();
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
		public Object getDescr();

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