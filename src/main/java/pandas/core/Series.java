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
package pandas.core;

import org.jpmml.python.PythonObject;

public class Series extends PythonObject {

	public Series(String module, String name){
		super(module, name);
	}

	public SingleBlockManager getBlockManager(){

		// Pandas 1.0
		if(containsKey("_data")){
			return get("_data", SingleBlockManager.class);
		}

		// Pandas 1.1+
		return get("_mgr", SingleBlockManager.class);
	}

	public String getName(){
		return getOptionalString("name");
	}

	public String getTyp(){
		return getString("_typ");
	}
}