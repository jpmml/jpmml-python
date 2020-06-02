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
import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.FieldName;
import org.jpmml.converter.Feature;

abstract
public class AbstractTranslator {

	private List<? extends Feature> features = null;


	public Feature getFeature(int index){
		List<? extends Feature> features = getFeatures();

		if(index >= 0 && index < features.size()){
			return features.get(index);
		}

		throw new IllegalArgumentException("Column index " + index + " not in range " + Arrays.asList(0, features.size()));
	}

	public Feature getFeature(FieldName name){
		List<? extends Feature> features = getFeatures();

		for(Feature feature : features){

			if((feature.getName()).equals(name)){
				return feature;
			}
		}

		List<String> names = features.stream()
			.map(feature -> "\'" + (feature.getName()).getValue() + "\'")
			.collect(Collectors.toList());

		throw new IllegalArgumentException("Column name \'" + name.getValue() + "\' not in " + names);
	}

	public List<? extends Feature> getFeatures(){
		return this.features;
	}

	void setFeatures(List<? extends Feature> features){
		this.features = features;
	}
}