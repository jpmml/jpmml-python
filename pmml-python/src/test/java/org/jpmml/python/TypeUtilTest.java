/*
 * Copyright (c) 2025 Villu Ruusmann
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

import org.dmg.pmml.DataType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeUtilTest {

	@Test
	public void getModule(){
		assertEquals("builtins", TypeUtil.getModule("bytes"));
		assertEquals("builtins", TypeUtil.getModule("complex"));
		assertEquals("builtins", TypeUtil.getModule("float"));
		assertEquals("builtins", TypeUtil.getModule("long"));
		assertEquals("builtins", TypeUtil.getModule("object"));
		assertEquals("builtins", TypeUtil.getModule("unicode"));

		assertEquals("numpy", TypeUtil.getModule("float_"));
		assertEquals("numpy", TypeUtil.getModule("float32"));
		assertEquals("numpy", TypeUtil.getModule("float64"));
	}

	@Test
	public void parseBuiltinType(){
		assertEquals(DataType.BOOLEAN, parseDataType("bool"));

		assertEquals(DataType.INTEGER, parseDataType("int"));
		assertEquals(DataType.INTEGER, parseDataType("long"));

		assertEquals(DataType.DOUBLE, parseDataType("float"));

		assertEquals(DataType.STRING, parseDataType("bytes"));

		assertEquals(DataType.STRING, parseDataType("str"));
		assertEquals(DataType.STRING, parseDataType("unicode"));
	}

	@Test
	public void parseNumPyType(){
		assertEquals(DataType.BOOLEAN, parseDataType("bool_"));
		assertEquals(DataType.BOOLEAN, parseDataType("?"));
		assertEquals(DataType.BOOLEAN, parseDataType("|b1"));

		assertEquals(DataType.INTEGER, parseDataType("int8"));
		assertEquals(DataType.INTEGER, parseDataType("b"));
		assertEquals(DataType.INTEGER, parseDataType("i4"));
		assertEquals(DataType.INTEGER, parseDataType("<i8"));
		assertEquals(DataType.INTEGER, parseDataType("Q"));
		assertEquals(DataType.INTEGER, parseDataType(">u2"));

		assertEquals(DataType.FLOAT, parseDataType("float32"));
		assertEquals(DataType.FLOAT, parseDataType("f"));
		assertEquals(DataType.FLOAT, parseDataType("<f4"));

		assertEquals(DataType.DOUBLE, parseDataType("float64"));
		assertEquals(DataType.DOUBLE, parseDataType("d"));
		assertEquals(DataType.DOUBLE, parseDataType("f8"));

		assertEquals(DataType.DATE, parseDataType("datetime64[D]"));
		assertEquals(DataType.DATE, parseDataType("<M8[D]"));
		assertEquals(DataType.DATE_TIME, parseDataType("M8[s]"));
		assertEquals(DataType.DATE_TIME, parseDataType("<M8[s]"));

		assertEquals(DataType.STRING, parseDataType("bytes_"));
		assertEquals(DataType.STRING, parseDataType("S"));
		assertEquals(DataType.STRING, parseDataType("|S10"));

		assertEquals(DataType.STRING, parseDataType("str_"));
		assertEquals(DataType.STRING, parseDataType("U"));
		assertEquals(DataType.STRING, parseDataType("<U10"));
	}

	static
	private DataType parseDataType(String className){
		builtins.Type type = builtins.Type.forClassName(className);

		return type.getDataType();
	}
}