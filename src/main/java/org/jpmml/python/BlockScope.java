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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.dmg.pmml.FieldName;
import org.jpmml.converter.Feature;

public class BlockScope extends Scope {

	private List<? extends Feature> variables = null;


	public BlockScope(List<? extends Feature> variables){
		setVariables(variables);
	}

	@Override
	public Feature getFeature(FieldName name){
		List<? extends Feature> features = getVariables();

		for(Feature feature : features){

			if((feature.getName()).equals(name)){
				return feature;
			}
		}

		List<String> variableNames = features.stream()
			.map(feature -> "\'" + (feature.getName()).getValue() + "\'")
			.collect(Collectors.toList());

		throw new IllegalArgumentException("Name \'" + name.getValue() + "\' is not in " + variableNames);
	}

	@Override
	public Feature getFeature(FieldName name, int columnIndex){
		getFeature(name);

		throw new IllegalArgumentException("Name \'" + name.getValue() + "\' is not subscriptable");
	}

	@Override
	public Feature getFeature(FieldName name, FieldName columnName){
		getFeature(name);

		throw new IllegalArgumentException("Name \'" + name.getValue() + "\' is not subscriptable");
	}

	@Override
	public Feature resolveFeature(FieldName name){
		List<? extends Feature> features = getVariables();

		for(Feature feature : features){

			if((feature.getName()).equals(name)){
				return feature;
			}
		}

		return null;
	}

	public List<? extends Feature> getVariables(){
		return this.variables;
	}

	private void setVariables(List<? extends Feature> variables){
		this.variables = Objects.requireNonNull(variables);
	}
}