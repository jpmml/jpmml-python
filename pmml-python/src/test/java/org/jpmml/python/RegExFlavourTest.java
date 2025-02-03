/*
 * Copyright (c) 2024 Villu Ruusmann
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

public class RegExFlavourTest {

	@Test
	public void translateReplacement(){
		assertEquals("$$", RegExFlavour.RE.translateReplacement("$"));

		assertEquals("$$", RegExFlavour.PCRE.translateReplacement("$$"));
		assertEquals("$$$$", RegExFlavour.RE.translateReplacement("$$"));

		assertEquals("$1", RegExFlavour.PCRE.translateReplacement("$1"));
		assertEquals("$$1", RegExFlavour.RE.translateReplacement("$1"));

		assertEquals("\\1", RegExFlavour.PCRE.translateReplacement("\\1"));
		assertEquals("$1", RegExFlavour.RE.translateReplacement("\\1"));
	}
}