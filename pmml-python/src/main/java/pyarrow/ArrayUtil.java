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
package pyarrow;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.arrow.flatbuf.Precision;

public class ArrayUtil {

	private ArrayUtil(){
	}

	static
	public <E> List<E> decodeDictionary(List<E> values, List<Number> indices){
		List<E> result = new ArrayList<>(indices.size());

		for(int i = 0; i < indices.size(); i++){
			Number index = indices.get(i);

			result.add(index != null ? values.get(index.intValue()) : null);
		}

		return result;
	}

	static
	public List<Boolean> decodeBooleans(byte[] validityBuffer, int nullCount, byte[] dataBuffer, int offset, int length){
		List<Boolean> result = new ArrayList<>(length);

		BitSet validityMask = toValidityMask(validityBuffer, nullCount);

		BitSet data = BitSet.valueOf(dataBuffer);

		for(int i = 0; i < length; i++){
			int index = offset + i;

			if(!ArrayUtil.isValid(validityMask, index)){
				result.add(null);

				continue;
			}

			result.add(data.get(index));
		}

		return result;
	}

	static
	public List<Number> decodeInts(byte[] validityBuffer, int nullCount, byte[] dataBuffer, int offset, int length, int bitWidth, boolean signed){
		List<Number> result = new ArrayList<>(length);

		BitSet validityMask = toValidityMask(validityBuffer, nullCount);

		ByteBuffer data = ByteBuffer.wrap(dataBuffer)
			.order(ByteOrder.LITTLE_ENDIAN);

		for(int i = 0; i < length; i++){
			int index = offset + i;

			if(!ArrayUtil.isValid(validityMask, index)){
				result.add(null);

				continue;
			}

			switch(bitWidth){
				case 8:
					{
						byte value = data.get(index);

						result.add(signed ? value : (short)Byte.toUnsignedInt(value));
					}
					break;
				case 16:
					{
						short value = data.getShort(index * 2);

						result.add(signed ? value : Short.toUnsignedInt(value));
					}
					break;
				case 32:
					{
						int value = data.getInt(index * 4);

						result.add(signed ? value : Integer.toUnsignedLong(value));
					}
					break;
				case 64:
					{
						long value = data.getLong(index * 8);

						result.add(signed ? value : new BigInteger(Long.toUnsignedString(value)));
					}
					break;
				default:
					throw new IllegalArgumentException();
			}
		}

		return result;
	}

	static
	public List<Number> decodeFloatingPoints(byte[] validityBuffer, int nullCount, byte[] dataBuffer, int offset, int length, int precision){
		List<Number> result = new ArrayList<>(length);

		BitSet validityMask = toValidityMask(validityBuffer, nullCount);

		ByteBuffer data = ByteBuffer.wrap(dataBuffer)
			.order(ByteOrder.LITTLE_ENDIAN);

		for(int i = 0; i < length; i++){
			int index = offset + i;

			if(!ArrayUtil.isValid(validityMask, index)){
				result.add(null);

				continue;
			}

			switch(precision){
				case Precision.SINGLE:
					{
						float value = data.getFloat(index * 4);

						result.add(value);
					}
					break;
				case Precision.DOUBLE:
					{
						double value = data.getDouble(index * 8);

						result.add(value);
					}
					break;
				default:
					throw new IllegalArgumentException();
			}
		}

		return result;
	}

	static
	public List<String> decodeStrings(byte[] validityBuffer, int nullCount, byte[] offsetsBuffer, byte[] dataBuffer, int offset, int length, boolean largeStrings){
		List<String> result = new ArrayList<>(length);

		BitSet validityMask = toValidityMask(validityBuffer, nullCount);

		ByteBuffer offsets = ByteBuffer.wrap(offsetsBuffer)
			.order(ByteOrder.LITTLE_ENDIAN);

		for(int i = 0; i < length; i++){
			int index = offset + i;

			if(!ArrayUtil.isValid(validityMask, index)){
				result.add(null);

				continue;
			}

			int start;
			int end;

			if(largeStrings){
				start = (int)offsets.getLong(index * 8);
				end = (int)offsets.getLong((index + 1) * 8);
			} else

			{
				start = offsets.getInt(index * 4);
				end = offsets.getInt((index + 1) * 4);
			}

			String string = new String(dataBuffer, start, (end - start), StandardCharsets.UTF_8);

			result.add(string);
		}

		return result;
	}

	static
	public List<String> decodeStringViews(byte[] validityBuffer, int nullCount, byte[] dataBuffer, int offset, int length){
		List<String> result = new ArrayList<>(length);

		BitSet validityMask = toValidityMask(validityBuffer, nullCount);

		ByteBuffer data = ByteBuffer.wrap(dataBuffer)
			.order(ByteOrder.LITTLE_ENDIAN);

		for(int i = 0; i < length; i++){
			int index = offset + i;

			if(!ArrayUtil.isValid(validityMask, index)){
				result.add(null);

				continue;
			}

			int viewOffset = index * 16;

			int stringLength = data.getInt(viewOffset);
			if(stringLength > 12){
				throw new IllegalArgumentException();
			}

			String string = new String(dataBuffer, viewOffset + 4, stringLength, StandardCharsets.UTF_8);

			result.add(string);
		}

		return result;
	}

	static
	private boolean isValid(BitSet validityMask, int index){

		if(validityMask == null){
			return true;
		}

		return validityMask.get(index);
	}

	static
	private BitSet toValidityMask(byte[] buffer, int nullCount){

		if(buffer != null && nullCount > 0){
			return BitSet.valueOf(buffer);
		}

		return null;
	}
}