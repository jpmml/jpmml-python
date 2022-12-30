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

import java.util.Objects;

import org.jpmml.converter.Feature;
import org.jpmml.converter.FeatureResolver;

abstract
public class AbstractTranslator implements FeatureResolver {

	private Scope scope = null;


	public AbstractTranslator(){
	}

	@Override
	public Feature resolveFeature(String name){
		Scope scope = ensureScope();

		return scope.resolveFeature(name);
	}

	public Scope ensureScope(){
		Scope scope = getScope();

		if(scope == null){
			throw new IllegalStateException();
		}

		return scope;
	}

	public Scope getScope(){
		return this.scope;
	}

	public void setScope(Scope scope){
		this.scope = Objects.requireNonNull(scope);
	}

	static
	public String toSingleLine(String string){
		return string
			.replaceAll("\t", "\\\\t")
			.replaceAll("\n", "\\\\n");
	}
}