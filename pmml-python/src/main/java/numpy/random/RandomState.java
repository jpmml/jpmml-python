/*
 * Copyright (c) 2015 Villu Ruusmann
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
package numpy.random;

import org.jpmml.python.CythonObject;

public class RandomState extends CythonObject {

	public RandomState(String module, String name){
		super(module, name);
	}

	@Override
	public void __setstate__(Object[] args){
		super.__setstate__(SETSTATE_ATTRIBUTES, args);
	}

	private static final String[] SETSTATE_ATTRIBUTES = {
		"str", // "MT19937"
		"keys",
		"pos",
		"has_gauss",
		"cached_gaussian"
	};
}