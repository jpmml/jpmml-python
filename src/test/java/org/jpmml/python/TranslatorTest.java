/*
 * Copyright (c) 2018 Villu Ruusmann
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldRef;
import org.jpmml.converter.BooleanFeature;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.Feature;
import org.jpmml.converter.ObjectFeature;
import org.jpmml.converter.PMMLEncoder;

abstract
class TranslatorTest {

	static final PMMLEncoder encoder = new PMMLEncoder();

	static final Map<Object, FieldRef> fieldRefs = new HashMap<>();

	static {
		fieldRefs.put(0, new FieldRef("a"));
		fieldRefs.put("a", new FieldRef("a"));

		fieldRefs.put(1, new FieldRef("b"));
		fieldRefs.put("b", new FieldRef("b"));

		fieldRefs.put(2, new FieldRef("c"));
		fieldRefs.put("c", new FieldRef("c"));
	}

	static final List<Feature> booleanFeatures = Arrays.asList(
		new BooleanFeature(TranslatorTest.encoder, "a"),
		new BooleanFeature(TranslatorTest.encoder, "b"),
		new BooleanFeature(TranslatorTest.encoder, "c")
	);

	static final List<Feature> doubleFeatures = Arrays.asList(
		new ContinuousFeature(TranslatorTest.encoder, "a", DataType.DOUBLE),
		new ContinuousFeature(TranslatorTest.encoder, "b", DataType.DOUBLE),
		new ContinuousFeature(TranslatorTest.encoder, "c", DataType.DOUBLE)
	);

	static final List<Feature> stringFeatures = Arrays.asList(
		new ObjectFeature(TranslatorTest.encoder, "a", DataType.STRING),
		new ObjectFeature(TranslatorTest.encoder, "b", DataType.STRING),
		new ObjectFeature(TranslatorTest.encoder, "c", DataType.STRING)
	);
}