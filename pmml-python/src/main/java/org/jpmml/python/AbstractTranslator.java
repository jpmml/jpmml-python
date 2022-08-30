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

abstract
public class AbstractTranslator {

	private Scope scope = null;


	public AbstractTranslator(){
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
	protected String translateBoolean(String image){
		return image.toLowerCase();
	}

	static
	protected String translateInt(String image){

		if(image.endsWith("l") || image.endsWith("L")){
			return image.substring(0, image.length() - 1);
		}

		return image;
	}

	static
	protected String translateString(String image){
		return image.substring(1, image.length() - 1);
	}

	static
	protected int parseInt(Token sign, Token value){
		return Integer.parseInt((sign != null ? sign.image : "") + translateInt(value.image));
	}
}