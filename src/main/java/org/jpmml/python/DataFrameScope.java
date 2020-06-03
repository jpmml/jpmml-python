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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.dmg.pmml.FieldName;
import org.jpmml.converter.Feature;

public class DataFrameScope extends Scope {

	private FieldName name = null;

	private List<? extends Feature> columns = null;


	public DataFrameScope(List<? extends Feature> features){
		this(FieldName.create("X"), features);
	}

	public DataFrameScope(FieldName name, List<? extends Feature> columns){
		setName(name);
		setColumns(columns);
	}

	@Override
	public Feature getFeature(FieldName name){
		FieldName dataFrameName = getName();

		checkName(name);

		throw new IllegalArgumentException("Name \'" + dataFrameName.getValue() + "\' refers to a row vector. Use an array indexing expression " + dataFrameName.getValue() + "[<column index>] or " + dataFrameName.getValue() + "[<column name>] to refer to a specific row vector element");
	}

	@Override
	public Feature getFeature(FieldName name, int columnIndex){
		List<? extends Feature> features = getColumns();

		checkName(name);

		if(columnIndex >= 0 && columnIndex < features.size()){
			return features.get(columnIndex);
		}

		throw new IllegalArgumentException("Column index " + columnIndex + " not in range " + Arrays.asList(0, features.size()));
	}

	@Override
	public Feature getFeature(FieldName name, FieldName columnName){
		List<? extends Feature> features = getColumns();

		checkName(name);

		for(Feature feature : features){

			if((feature.getName()).equals(columnName)){
				return feature;
			}
		}

		List<String> columnNames = features.stream()
			.map(feature -> "\'" + (feature.getName()).getValue() + "\'")
			.collect(Collectors.toList());

		throw new IllegalArgumentException("Column name \'" + columnName.getValue() + "\' not in " + columnNames);
	}

	@Override
	public Feature resolveFeature(FieldName name){
		List<? extends Feature> features = getColumns();

		for(Feature feature : features){

			if((feature.getName()).equals(name)){
				return feature;
			}
		}

		return null;
	}

	private void checkName(FieldName name){
		FieldName dataFrameName = getName();

		if(!(dataFrameName).equals(name)){
			throw new IllegalArgumentException("Name \'" + name.getValue() + "\' is not defined");
		}
	}

	public FieldName getName(){
		return this.name;
	}

	private void setName(FieldName name){
		this.name = Objects.requireNonNull(name);
	}

	public List<? extends Feature> getColumns(){
		return this.columns;
	}

	private void setColumns(List<? extends Feature> columns){
		this.columns = Objects.requireNonNull(columns);
	}
}