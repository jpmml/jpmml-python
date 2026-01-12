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
package org.jpmml.python;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PythonFormatterUtilTest {

	@Test
	public void formatValue(){
		assertEquals("None", PythonFormatterUtil.formatValue(null));
	}

	@Test
	public void formatBoolean(){
		assertEquals("False", PythonFormatterUtil.formatBoolean(false));
		assertEquals("True", PythonFormatterUtil.formatBoolean(true));
	}

	@Test
	public void formatValues(){
		assertEquals("", PythonFormatterUtil.formatValues(Collections.emptyList()));

		assertEquals("\'A\'", PythonFormatterUtil.formatValues(Arrays.asList("A")));
		assertEquals("\'A\' or \'B\'", PythonFormatterUtil.formatValues(Arrays.asList("A", "B")));
		assertEquals("\'A\', \'B\' or \'C\'", PythonFormatterUtil.formatValues(Arrays.asList("A", "B", "C")));
	}
}