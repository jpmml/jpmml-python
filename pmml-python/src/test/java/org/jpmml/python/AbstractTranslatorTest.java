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
package org.jpmml.python;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractTranslatorTest {

	@Test
	public void canonicalize(){
		AbstractTranslator abstractTranslator = new AbstractTranslator(){
		};

		assertEquals("np.name", abstractTranslator.canonicalizeDottedName("np.name"));
		assertEquals("np.ext.name", abstractTranslator.canonicalizeDottedName("np.ext.name"));

		assertEquals("pd.name", abstractTranslator.canonicalizeDottedName("pd.name"));
		assertEquals("pd.ext.name", abstractTranslator.canonicalizeDottedName("pd.ext.name"));

		abstractTranslator.registerModuleAliases();

		assertEquals("name", abstractTranslator.canonicalizeDottedName("name"));

		assertEquals("builtins.name", abstractTranslator.canonicalizeDottedName("builtins.name"));

		assertEquals("math.name", abstractTranslator.canonicalizeDottedName("math.name"));

		assertEquals("numpy.name", abstractTranslator.canonicalizeDottedName("np.name"));
		assertEquals("numpy.name", abstractTranslator.canonicalizeDottedName("numpy.name"));
		assertEquals("numpy.ext.name", abstractTranslator.canonicalizeDottedName("np.ext.name"));

		assertEquals("pandas.name", abstractTranslator.canonicalizeDottedName("pd.name"));
		assertEquals("pandas.name", abstractTranslator.canonicalizeDottedName("pandas.name"));
		assertEquals("pandas.ext.name", abstractTranslator.canonicalizeDottedName("pd.ext.name"));
	}

	@Test
	public void toSingleLine(){
		String string =
			"if True:\n" +
			"	return 1\n" +
			"else:\n" +
			"	return 0";

		assertEquals("if True:\\n\\treturn 1\\nelse:\\n\\treturn 0", AbstractTranslator.toSingleLine(string));
	}
}