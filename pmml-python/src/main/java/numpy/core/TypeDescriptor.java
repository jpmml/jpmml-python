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
package numpy.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteStreams;
import com.google.common.primitives.UnsignedInts;
import numpy.DType;
import org.dmg.pmml.DataType;
import org.jpmml.python.CalendarUtil;

/**
 * https://docs.scipy.org/doc/numpy/reference/arrays.dtypes.html
 * https://docs.scipy.org/doc/numpy/reference/generated/numpy.dtype.byteorder.html
 */
public class TypeDescriptor {

	private String descr = null;

	private ByteOrder byteOrder = null;

	private TypeDescriptor.Kind kind = null;

	private int size = 0;

	private Object[] datetimeData = null;


	public TypeDescriptor(Object descr){

		if(descr instanceof String){
			String string = (String)descr;

			parseDescr(string);
		} else

		if(descr instanceof DType){
			DType dtype = (DType)descr;

			String string = (String)dtype.toDescr();

			parseDescr(string);

			Object[] datetimeData = dtype.getDatetimeData();
			if(datetimeData != null){
				setDatetimeData(datetimeData);
			}
		} else

		{
			throw new IllegalArgumentException(String.valueOf(descr));
		}
	}

	private void parseDescr(String descr){
		setDescr(descr);

		int i = 0;

		ByteOrder byteOrder = null;

		switch(descr.charAt(i)){
			// Native
			case '=':
				byteOrder = ByteOrder.nativeOrder();
				i++;
				break;
			// Big-endian
			case '>':
				byteOrder = ByteOrder.BIG_ENDIAN;
				i++;
				break;
			// Little-endian
			case '<':
				byteOrder = ByteOrder.LITTLE_ENDIAN;
				i++;
				break;
			// Not applicable
			case '|':
				i++;
				break;
			default:
				break;
		}

		setByteOrder(byteOrder);

		TypeDescriptor.Kind kind = Kind.forChar(descr.charAt(i));

		i++;

		setKind(kind);

		if(i < descr.length()){
			int size = Integer.parseInt(descr.substring(i));

			setSize(size);
		}
	}

	public DataType getDataType(){
		String descr = getDescr();
		TypeDescriptor.Kind kind = getKind();
		int size = getSize();

		switch(kind){
			case BOOLEAN:
				return DataType.BOOLEAN;
			case INTEGER:
			case UNSIGNED_INTEGER:
				return DataType.INTEGER;
			case FLOAT:
				switch(size){
					case 4:
						return DataType.FLOAT;
					case 8:
						return DataType.DOUBLE;
					default:
						throw new IllegalArgumentException(descr);
				}
			case COMPLEX_FLOAT:
				switch(size){
					case 8:
						return DataType.FLOAT;
					case 16:
						return DataType.DOUBLE;
					default:
						throw new IllegalArgumentException(descr);
				}
			case DATETIME:
				switch(size){
					case 8:
						String datetimeUnit = getDatetimeUnit();

						switch(datetimeUnit){
							case "Y":
							case "M":
							case "D":
								return DataType.DATE;
							case "h":
							case "m":
							case "s":
								return DataType.DATE_TIME;
							case "ms":
							case "us":
							case "ns":
								return DataType.DATE_TIME;
							default:
								throw new IllegalArgumentException(datetimeUnit);
						}
					default:
						throw new IllegalArgumentException(descr);
				}
			case OBJECT:
				return DataType.STRING;
			case STRING:
			case UNICODE:
				return DataType.STRING;
			default:
				throw new IllegalArgumentException(descr);
		}
	}

	public Object read(InputStream is) throws IOException {
		String descr = getDescr();
		TypeDescriptor.Kind kind = getKind();
		ByteOrder byteOrder = getByteOrder();
		int size = getSize();

		switch(kind){
			case BOOLEAN:
				{
					switch(size){
						case 1:
							return (NDArrayUtil.readByte(is) == 1);
						default:
							break;
					}
				}
				break;
			case INTEGER:
				{
					switch(size){
						case 1:
							return NDArrayUtil.readByte(is);
						case 2:
							return NDArrayUtil.readShort(is, byteOrder);
						case 4:
							return NDArrayUtil.readInt(is, byteOrder);
						case 8:
							return NDArrayUtil.readLong(is, byteOrder);
						default:
							break;
					}
				}
				break;
			case UNSIGNED_INTEGER:
				{
					switch(size){
						case 1:
							return NDArrayUtil.readUnsignedByte(is);
						case 2:
							return NDArrayUtil.readUnsignedShort(is, byteOrder);
						case 4:
							return UnsignedInts.toLong(NDArrayUtil.readInt(is, byteOrder));
						case 8:
							String string = Long.toUnsignedString(NDArrayUtil.readLong(is, byteOrder));

							return Long.parseUnsignedLong(string);
						default:
							break;
					}
				}
				break;
			case FLOAT:
				{
					switch(size){
						case 4:
							return NDArrayUtil.readFloat(is, byteOrder);
						case 8:
							return NDArrayUtil.readDouble(is, byteOrder);
						default:
							break;
					}
				}
				break;
			case COMPLEX_FLOAT:
				{
					switch(size){
						case 8:
							{
								float real = NDArrayUtil.readFloat(is, byteOrder);
								float imaginary = NDArrayUtil.readFloat(is, byteOrder);

								return new Complex(real, imaginary);
							}
						case 16:
							{
								double real = NDArrayUtil.readDouble(is, byteOrder);
								double imaginary = NDArrayUtil.readDouble(is, byteOrder);

								return new Complex(real, imaginary);
							}
						default:
							break;
					}
				}
				break;
			case DATETIME:
				{
					String datetimeUnit = getDatetimeUnit();

					switch(size){
						case 8:
							long value = NDArrayUtil.readLong(is, byteOrder);

							Calendar calendar = Calendar.getInstance();

							// Local dates/datetimes relative to UTC
							calendar.setTimeZone(TypeDescriptor.TIMEZONE_UTC);

							switch(datetimeUnit){
								case "Y":
								case "M":
								case "D":
									{
										long millis;

										switch(datetimeUnit){
											case "D":
												millis = TimeUnit.DAYS.toMillis(value);
												break;
											default:
												throw new IllegalArgumentException(datetimeUnit);
										}

										calendar.setTimeInMillis(millis);

										return CalendarUtil.toLocalDate(calendar);
									}
								case "h":
								case "m":
								case "s":
								case "ms":
								case "us":
								case "ns":
									{
										long millis;

										switch(datetimeUnit){
											case "h":
												millis = TimeUnit.HOURS.toMillis(value);
												break;
											case "m":
												millis = TimeUnit.MINUTES.toMillis(value);
												break;
											case "s":
												millis = TimeUnit.SECONDS.toMillis(value);
												break;
											case "ms":
												millis = TimeUnit.MILLISECONDS.toMillis(value);
												break;
											case "us":
												millis = TimeUnit.MICROSECONDS.toMillis(value);
												break;
											case "ns":
												millis = TimeUnit.NANOSECONDS.toMillis(value);
												break;
											default:
												throw new IllegalArgumentException(datetimeUnit);
										}

										calendar.setTimeInMillis(millis);

										return CalendarUtil.toLocalDateTime(calendar);
									}
								default:
									throw new IllegalArgumentException(datetimeUnit);
							}
						default:
							break;
					}
				}
				break;
			case OBJECT:
				{
					return NDArrayUtil.readObject(is);
				}
			case STRING:
				{
					return NDArrayUtil.readString(is, size);
				}
			case UNICODE:
				{
					return NDArrayUtil.readUnicode(is, byteOrder, size);
				}
			case VOID:
				{
					byte[] buffer = new byte[size];

					ByteStreams.readFully(is, buffer);

					return buffer;
				}
			default:
				break;
		}

		throw new IllegalArgumentException(descr);
	}

	public boolean isObject(){
		TypeDescriptor.Kind kind = getKind();

		switch(kind){
			case OBJECT:
				return true;
			default:
				return false;
		}
	}

	public String getDatetimeUnit(){
		Object[] datetimeData = getDatetimeData();
		if(datetimeData == null){
			throw new IllegalStateException();
		}

		Object units = ((Object[])datetimeData[1])[0];

		if(units instanceof String){
			String string = (String)units;

			return string;
		} else

		if(units instanceof byte[]){
			byte[] bytes = (byte[])units;

			return new String(bytes);
		} else

		{
			throw new IllegalArgumentException(String.valueOf(units));
		}
	}

	public String getDescr(){
		return this.descr;
	}

	private void setDescr(String descr){
		this.descr = descr;
	}

	public ByteOrder getByteOrder(){
		return this.byteOrder;
	}

	private void setByteOrder(ByteOrder byteOrder){
		this.byteOrder = byteOrder;
	}

	public TypeDescriptor.Kind getKind(){
		return this.kind;
	}

	private void setKind(TypeDescriptor.Kind kind){
		this.kind = kind;
	}

	public int getSize(){
		return this.size;
	}

	private void setSize(int size){
		this.size = size;
	}

	public Object[] getDatetimeData(){
		return this.datetimeData;
	}

	private void setDatetimeData(Object[] datetimeData){
		this.datetimeData = datetimeData;
	}

	static
	public enum Kind {
		BOOLEAN,
		INTEGER,
		UNSIGNED_INTEGER,
		FLOAT,
		COMPLEX_FLOAT,
		TIMEDELTA,
		DATETIME,
		OBJECT,
		STRING,
		UNICODE,
		VOID,
		;

		static
		public TypeDescriptor.Kind forChar(char c){

			switch(c){
				case 'b':
					return BOOLEAN;
				case 'i':
					return INTEGER;
				case 'u':
					return UNSIGNED_INTEGER;
				case 'f':
					return FLOAT;
				case 'c':
					return COMPLEX_FLOAT;
				case 'm':
					return TIMEDELTA;
				case 'M':
					return DATETIME;
				case 'O':
					return OBJECT;
				case 'S':
				case 'a':
					return STRING;
				case 'U':
					return UNICODE;
				case 'V':
					return VOID;
				default:
					throw new IllegalArgumentException(String.valueOf(c));
			}
		}
	}

	private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
}