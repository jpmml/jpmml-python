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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SliceUtilTest {

	@Test
	public void slice(){
		List<Integer> values = SliceUtil.indices(0, 5);

		assertEquals(Arrays.asList(0, 1, 2, 3, 4), values);

		assertEquals(Collections.emptyList(), SliceUtil.slice(values, 2, 2));
		assertEquals(Arrays.asList(2, 3, 4), SliceUtil.slice(values, 2, null));
		assertEquals(Arrays.asList(0, 1), SliceUtil.slice(values, null, 2));
		assertEquals(Arrays.asList(0, 1, 2, 3, 4), SliceUtil.slice(values, null, null));

		assertEquals(Arrays.asList(3, 4), SliceUtil.slice(values, -2, null));
		assertEquals(Arrays.asList(0, 1, 2), SliceUtil.slice(values, null, -2));

		assertEquals(Arrays.asList(0, 2, 4), SliceUtil.slice(values, null, null, 2));
		assertEquals(Arrays.asList(1, 3), SliceUtil.slice(values, 1, null, 2));
		assertEquals(Arrays.asList(1), SliceUtil.slice(values, (Integer)1, (Integer)3, (Integer)2));
		assertEquals(Arrays.asList(4), SliceUtil.slice(values, -1, null, 2));
		assertEquals(Arrays.asList(0, 2, 4), SliceUtil.slice(values, -10, null, 2));
		assertEquals(Arrays.asList(0, 2, 4), SliceUtil.slice(values, -9, null, 2));
		assertEquals(Collections.emptyList(), SliceUtil.slice(values, (Integer)(-1), (Integer)3, (Integer)2));

		assertEquals(Arrays.asList(0, 3), SliceUtil.slice(values, null, null, 3));
		assertEquals(Arrays.asList(1, 4), SliceUtil.slice(values, 1, null, 3));
		assertEquals(Arrays.asList(4), SliceUtil.slice(values, -1, null, 3));
		assertEquals(Arrays.asList(0, 3), SliceUtil.slice(values, -10, null, 3));
		assertEquals(Arrays.asList(0, 3), SliceUtil.slice(values, -9, null, 3));

		values = SliceUtil.indices(0, 1);

		assertEquals(Arrays.asList(0), values);

		assertEquals(Arrays.asList(0), SliceUtil.slice(values, 0, 10));

		assertEquals(Collections.emptyList(), SliceUtil.slice(values, null, -2));
	}
}