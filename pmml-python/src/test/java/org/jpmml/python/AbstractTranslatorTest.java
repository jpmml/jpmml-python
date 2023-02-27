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

import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AbstractTranslatorTest {

	@Test
	public void canonicalize(){
		AbstractTranslator abstractTranslator = new AbstractTranslator(){
		};

		Map<String, String> imports = abstractTranslator.getImports();
		imports.put("np", "numpy");
		imports.put("pd", "pandas");

		assertEquals("builtins.name", abstractTranslator.canonicalizeDottedName("name"));
		assertEquals("builtins.name", abstractTranslator.canonicalizeDottedName("builtins.name"));

		assertEquals("numpy.name", abstractTranslator.canonicalizeDottedName("np.name"));
		assertEquals("numpy.suffix.name", abstractTranslator.canonicalizeDottedName("np.suffix.name"));

		assertEquals("pandas.name", abstractTranslator.canonicalizeDottedName("pd.name"));
		assertEquals("pandas.suffix.name", abstractTranslator.canonicalizeDottedName("pd.suffix.name"));
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