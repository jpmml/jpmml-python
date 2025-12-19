/*
 * Copyright (c) 2025 Villu Ruusmann
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

import net.razorvine.pickle.objects.ClassDict;

public class Attribute {

	private ClassDict dict = null;

	private String name = null;


	public Attribute(ClassDict dict, String name){
		setClassDict(dict);
		setName(name);
	}

	public String format(){
		ClassDict dict = getClassDict();
		String name = getName();

		return ClassDictUtil.formatMember(dict, name);
	}

	public ClassDict getClassDict(){
		return this.dict;
	}

	private void setClassDict(ClassDict dict){
		this.dict = Objects.requireNonNull(dict);
	}

	public String getName(){
		return this.name;
	}

	private void setName(String name){
		this.name = Objects.requireNonNull(name);
	}
}