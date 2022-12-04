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
import org.jpmml.converter.PMMLEncoder;

public class DataFrameScope extends Scope {

	private String variableName = null;

	private List<? extends Feature> columns = null;


	public DataFrameScope(List<? extends Feature> columns){
		this("X", columns, null);
	}

	public DataFrameScope(String variableName, List<? extends Feature> columns){
		this(variableName, columns, null);
	}

	public DataFrameScope(String variableName, List<? extends Feature> columns, PMMLEncoder encoder){
		super(encoder);

		setVariableName(variableName);
		setColumns(columns);
	}

	@Override
	public Feature getFeature(String name){
		String variableName = getVariableName();

		if((variableName).equals(name)){
			throw new IllegalArgumentException("Name \'" + variableName + "\' refers to a row vector. Use an array indexing expression " + variableName + "[<column index>] or " + variableName + "[<column name>] to refer to a specific row vector element");
		}

		Feature feature = resolveFeature(name);

		if(feature != null){
			return feature;
		}

		throw new IllegalArgumentException("Name \'" + name + "\' is not defined");
	}

	@Override
	public Feature getFeature(String name, int columnIndex){
		List<? extends Feature> columns = getColumns();

		checkIsDataFrame(name);

		if(columnIndex >= 0){

			if(columnIndex < columns.size()){
				return columns.get(columnIndex);
			}

			throw new IllegalArgumentException("Column index " + columnIndex + " not in range " + Arrays.asList(0, columns.size()));
		} else

		{
			if((-columnIndex) <= columns.size()){
				return columns.get(columns.size() - (-columnIndex));
			}

			throw new IllegalArgumentException("Column index " + columnIndex + " not in range " + Arrays.asList(-columns.size(), -1));
		}
	}

	@Override
	public Feature getFeature(String name, String columnName){
		List<? extends Feature> columns = getColumns();

		checkIsDataFrame(name);

		for(Feature column : columns){

			if((column.getName()).equals(columnName)){
				return column;
			}
		}

		List<String> columnNames = columns.stream()
			.map(feature -> "\'" + feature.getName() + "\'")
			.collect(Collectors.toList());

		throw new IllegalArgumentException("Column name \'" + columnName + "\' is not in " + columnNames);
	}

	@Override
	public Feature resolveFeature(String name){
		List<? extends Feature> columns = getColumns();

		for(Feature column : columns){

			if((column.getName()).equals(name)){
				return column;
			}
		}

		return super.resolveFeature(name);
	}

	private void checkIsDataFrame(String name){
		String variableName = getVariableName();

		if(!(variableName).equals(name)){
			throw new IllegalArgumentException("Name \'" + name + "\' is not defined");
		}
	}

	public String getVariableName(){
		return this.variableName;
	}

	private void setVariableName(String variableName){
		this.variableName = Objects.requireNonNull(variableName);
	}

	public List<? extends Feature> getColumns(){
		return this.columns;
	}

	private void setColumns(List<? extends Feature> columns){
		this.columns = Objects.requireNonNull(columns);
	}
}