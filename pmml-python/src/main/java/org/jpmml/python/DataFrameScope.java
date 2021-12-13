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

import org.jpmml.converter.Feature;

public class DataFrameScope extends Scope {

	private String name = null;

	private List<? extends Feature> columns = null;


	public DataFrameScope(List<? extends Feature> features){
		this("X", features);
	}

	public DataFrameScope(String name, List<? extends Feature> columns){
		setName(name);
		setColumns(columns);
	}

	@Override
	public Feature getFeature(String name){
		String dataFrameName = getName();

		checkName(name);

		throw new IllegalArgumentException("Name \'" + dataFrameName + "\' refers to a row vector. Use an array indexing expression " + dataFrameName + "[<column index>] or " + dataFrameName + "[<column name>] to refer to a specific row vector element");
	}

	@Override
	public Feature getFeature(String name, int columnIndex){
		List<? extends Feature> features = getColumns();

		checkName(name);

		if(columnIndex >= 0){

			if(columnIndex < features.size()){
				return features.get(columnIndex);
			}

			throw new IllegalArgumentException("Column index " + columnIndex + " not in range " + Arrays.asList(0, features.size()));
		} else

		{
			if((-columnIndex) <= features.size()){
				return features.get(features.size() - (-columnIndex));
			}

			throw new IllegalArgumentException("Column index " + columnIndex + " not in range " + Arrays.asList(-features.size(), -1));
		}
	}

	@Override
	public Feature getFeature(String name, String columnName){
		List<? extends Feature> features = getColumns();

		checkName(name);

		for(Feature feature : features){

			if((feature.getName()).equals(columnName)){
				return feature;
			}
		}

		List<String> columnNames = features.stream()
			.map(feature -> "\'" + feature.getName() + "\'")
			.collect(Collectors.toList());

		throw new IllegalArgumentException("Column name \'" + columnName + "\' is not in " + columnNames);
	}

	@Override
	public Feature resolveFeature(String name){
		List<? extends Feature> features = getColumns();

		for(Feature feature : features){

			if((feature.getName()).equals(name)){
				return feature;
			}
		}

		return null;
	}

	private void checkName(String name){
		String dataFrameName = getName();

		if(!(dataFrameName).equals(name)){
			throw new IllegalArgumentException("Name \'" + name + "\' is not defined");
		}
	}

	public String getName(){
		return this.name;
	}

	private void setName(String name){
		this.name = Objects.requireNonNull(name);
	}

	public List<? extends Feature> getColumns(){
		return this.columns;
	}

	private void setColumns(List<? extends Feature> columns){
		this.columns = Objects.requireNonNull(columns);
	}
}