/*
 * Copyright (c) 2026 Villu Ruusmann
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
package polars.series;

import java.util.List;

import org.apache.arrow.flatbuf.Precision;
import org.apache.arrow.flatbuf.Type;
import org.jpmml.python.HasArray;
import org.jpmml.python.PythonObject;
import polars.datatypes.DataType;
import pyarrow.ArrayUtil;
import pyarrow.IPCUtil;

public class Series extends PythonObject implements HasArray {

	public Series(){
		this("polars.series.series", "Series");
	}

	public Series(String module, String name){
		super(module, name);
	}

	@Override
	public List<?> getArrayContent(){
		List<byte[]> buffers = getBuffers();
		Series categories = getCategories();
		int length = getLength();
		List<long[]> nodes = getNodes();
		int typeType = getTypeType();

		int nullCount = (int)(nodes.get(0))[1];

		if(categories != null){
			List<?> categoryValues = categories.getArrayContent();
			List<Number> indices = ArrayUtil.decodeInts(buffers.get(0), nullCount, buffers.get(1), 0, length, getBitWidth(), isSigned());

			return ArrayUtil.decodeDictionary(categoryValues, indices);
		}

		switch(typeType){
			case Type.Bool:
				return ArrayUtil.decodeBooleans(buffers.get(0), nullCount, buffers.get(1), 0, length);
			case Type.Int:
				return ArrayUtil.decodeInts(buffers.get(0), nullCount, buffers.get(1), 0, length, getBitWidth(), isSigned());
			case Type.FloatingPoint:
				return ArrayUtil.decodeFloatingPoints(buffers.get(0), nullCount, buffers.get(1), 0, length, getPrecision());
			case Type.Utf8View:
				return ArrayUtil.decodeStringViews(buffers.get(0), nullCount, buffers.get(1), 0, length);
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public int[] getArrayShape(){
		return new int[]{getLength()};
	}

	@Override
	public DataType getArrayType(){
		DataType dtype = getDType();
		int typeType = getTypeType();

		if(dtype != null){
			return dtype;
		}

		String name;

		switch(typeType){
			case Type.Bool:
				name = "Boolean";
				break;
			case Type.Int:
				name = (isSigned() ? "" : "U") + "Int" + getBitWidth();
				break;
			case Type.FloatingPoint:
				name = "Float" + toBitWidth(getPrecision());
				break;
			case Type.Utf8View:
				name = "String";
				break;
			default:
				throw new IllegalArgumentException();
		}

		return DataType.forName(name);
	}

	public void __setstate__(byte[] state){
		super.__setstate__(IPCUtil.parseSeries(state));
	}

	public List<byte[]> getBuffers(){
		return (List)getList("buffers");
	}

	public Integer getBitWidth(){
		return getOptionalInteger("bitWidth");
	}

	public Series getCategories(){
		return getOptional("categories", Series.class);
	}

	public DataType getDType(){
		return getOptional("dtype", DataType.class);
	}

	public Integer getLength(){
		return getInteger("length");
	}

	public String getName(){
		return getString("name");
	}

	public List<long[]> getNodes(){
		return (List)getList("nodes");
	}

	public Integer getPrecision(){
		return getOptionalInteger("precision");
	}

	public Boolean isSigned(){
		return getOptionalBoolean("signed");
	}

	public Integer getTypeType(){
		return getInteger("typeType");
	}

	public Series setTypeType(int typeType){
		setattr("typeType", typeType);

		return this;
	}

	static
	private int toBitWidth(int precision){

		switch(precision){
			case Precision.SINGLE:
			case Precision.DOUBLE:
				return precision * 32;
			default:
				throw new IllegalArgumentException();
		}
	}
}