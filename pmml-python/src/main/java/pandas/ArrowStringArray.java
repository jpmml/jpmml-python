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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import builtins.Type;
import org.jpmml.python.HasArray;
import org.jpmml.python.PythonObject;
import pyarrow.Array;
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
		Type type = paArray.getType();

		Buffer validityBuffer = buffers.get(0);
		Buffer offsetsBuffer = buffers.get(1);
		Buffer dataBuffer = buffers.get(2);

		BitSet validityMask = null;

		if(validityBuffer != null){
			validityMask = BitSet.valueOf(validityBuffer.getBuffer());
		}

		boolean hasLargeStrings = Objects.equals(Types.LARGE_STRING, type.getClassName());

		ByteBuffer offsets = ByteBuffer.wrap(offsetsBuffer.getBuffer())
			.order(ByteOrder.LITTLE_ENDIAN);

		byte[] dataBytes = dataBuffer.getBuffer();

		List<String> result = new ArrayList<>(length);

		for(int i = 0; i < length; i++){
			int index = offset + i;

			if(validityMask != null && !validityMask.get(index)){
				result.add(null);

				continue;
			}

			int start;
			int end;

			if(hasLargeStrings){
				start = (int)offsets.getLong(index * 8);
				end = (int)offsets.getLong((index + 1) * 8);
			} else

			{
				start = offsets.getInt(index * 4);
				end = offsets.getInt((index + 1) * 4);
			}

			String string = new String(dataBytes, start, (end - start), StandardCharsets.UTF_8);

			result.add(string);
		}

		return result;
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

	@Override
	public void __setstate__(HashMap<String, Object> newState){
		super.__setstate__(newState);
	}

	public Object getDType(){
		return get("_dtype");
	}

	public Array getPAArray(){
		return get("_pa_array", Array.class);
	}
}