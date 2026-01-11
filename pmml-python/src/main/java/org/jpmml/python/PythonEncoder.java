/*
 * Copyright (c) 2020 Villu Ruusmann
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

import org.dmg.pmml.Field;
import org.jpmml.converter.Feature;
import org.jpmml.converter.FeatureResolver;
import org.jpmml.converter.FeatureUtil;
import org.jpmml.converter.ModelEncoder;
import org.jpmml.converter.SchemaException;

abstract
public class PythonEncoder extends ModelEncoder implements FeatureResolver {

	@Override
	public Feature resolveFeature(String name){
		Field<?> field;

		try {
			field = getField(name);
		} catch(SchemaException se){
			return null;
		}

		return FeatureUtil.createFeature(field, this);
	}

	static {
		ClassLoader clazzLoader = PythonEncoder.class.getClassLoader();

		PickleUtil.init(clazzLoader, "python2pmml.properties");
	}
}