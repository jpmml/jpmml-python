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
package pandas;

import java.util.List;
import java.util.Objects;

import builtins.Type;
import org.jpmml.python.HasArray;
import org.jpmml.python.PythonObject;
import pyarrow.Array;
import pyarrow.ArrayUtil;
import pyarrow.Buffer;
import pyarrow.Types;

public class ArrowStringArray extends PythonObject implements HasArray {

	public ArrowStringArray(String module, String name){
		super(module, name);
	}

	@Override
	public List<?> getArrayContent(){
		Array paArray = getPAArray();

		List<Buffer> buffers = paArray.getBuffers();
		int length = paArray.getLength();
		int offset = paArray.getOffset();
		int nullCount = paArray.getNullCount();
		Type type = paArray.getType();

		byte[] validityBuffer = getBuffer(buffers.get(0));
		byte[] offsetsBuffer = getBuffer(buffers.get(1));
		byte[] dataBuffer = getBuffer(buffers.get(2));

		boolean largeStrings = Objects.equals(Types.LARGE_STRING, type.getClassName());

		return ArrayUtil.decodeStrings(validityBuffer, nullCount, offsetsBuffer, dataBuffer, offset, length, largeStrings);
	}

	@Override
	public int[] getArrayShape(){
		Array paArray = getPAArray();

		int length = paArray.getLength();

		return new int[]{length};
	}

	@Override
	public Object getArrayType(){
		return getDType();
	}

	public Object getDType(){
		return get("_dtype");
	}

	public Array getPAArray(){
		return get("_pa_array", Array.class);
	}

	static
	private byte[] getBuffer(Buffer buffer){

		if(buffer != null){
			return buffer.getBuffer();
		}

		return null;
	}
}