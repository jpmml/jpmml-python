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
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.converter.ResolutionException;

abstract
public class Scope implements FeatureResolver {

	private PMMLEncoder encoder = null;


	public Scope(){
	}

	public Scope(PMMLEncoder encoder){
		setEncoder(encoder);
	}

	abstract
	public Feature getFeature(String name);

	abstract
	public Feature getFeature(String name, int columnIndex);

	abstract
	public Feature getFeature(String name, String columnName);

	@Override
	public Feature resolveFeature(String name){
		PMMLEncoder encoder = getEncoder();

		if(encoder != null){
			Field<?> field;

			try {
				field = encoder.getField(name);
			} catch(ResolutionException re){
				return null;
			}

			return FeatureUtil.createFeature(field, encoder);
		}

		return null;
	}

	public PMMLEncoder getEncoder(){
		return this.encoder;
	}

	private void setEncoder(PMMLEncoder encoder){
		this.encoder = encoder;
	}
}