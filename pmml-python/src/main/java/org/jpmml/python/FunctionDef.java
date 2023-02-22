/*
 * Copyright (c) 2023 Villu Ruusmann
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionDef {

	private String string = null;

	private String name = null;

	private List<Parameter> parameters = new ArrayList<>();


	public FunctionDef(String string){
		setString(string);
	}

	public String getString(){
		return this.string;
	}

	private void setString(String string){
		this.string = Objects.requireNonNull(string);
	}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = Objects.requireNonNull(name);
	}

	public List<Parameter> getParameters(){
		return this.parameters;
	}

	static
	public class Parameter {

		private String name = null;


		public Parameter(String name){
			setName(name);
		}

		public String getName(){
			return this.name;
		}

		private void setName(String name){
			this.name = Objects.requireNonNull(name);
		}
	}
}