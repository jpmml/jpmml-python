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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jpmml.converter.Feature;
import org.jpmml.converter.FeatureUtil;
import org.jpmml.converter.OperationException;
import org.jpmml.converter.PMMLEncoder;

public class BlockScope extends Scope {

	private List<? extends Feature> variables = null;


	public BlockScope(List<? extends Feature> variables){
		this(variables, null);
	}

	public BlockScope(List<? extends Feature> variables, PMMLEncoder encoder){
		super(encoder);

		setVariables(variables);
	}

	@Override
	public Feature getFeature(String name){
		Feature feature = resolveFeature(name);

		if(feature != null){
			return feature;
		}

		throw new NameResolutionException(name);
	}

	@Override
	public Feature getFeature(String name, int columnIndex){
		getFeature(name);

		throw new OperationException("Name \'" + name + "\' is not subscriptable");
	}

	@Override
	public Feature getFeature(String name, String columnName){
		getFeature(name);

		throw new OperationException("Name \'" + name + "\' is not subscriptable");
	}

	@Override
	public Feature resolveFeature(String name){
		List<? extends Feature> variables = getVariables();

		Feature feature = FeatureUtil.findFeature(variables, name);
		if(feature != null){
			return feature;
		}

		return super.resolveFeature(name);
	}

	public List<? extends Feature> getVariables(){
		return this.variables;
	}

	private void setVariables(List<? extends Feature> variables){
		this.variables = Objects.requireNonNull(variables);
	}

	public static final BlockScope EMPTY = new BlockScope(Collections.emptyList());
}