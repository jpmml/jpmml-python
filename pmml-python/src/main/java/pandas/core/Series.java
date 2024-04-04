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

import java.util.List;

import org.jpmml.python.HasArray;
import org.jpmml.python.PythonObject;

public class Series extends PythonObject implements HasArray {

	public Series(){
		this("pandas.core.series", "Series");
	}

	public Series(String module, String name){
		super(module, name);
	}

	@Override
	public List<?> getArrayContent(){
		HasArray values = getValues();

		return values.getArrayContent();
	}

	@Override
	public int[] getArrayShape(){
		HasArray values = getValues();

		return values.getArrayShape();
	}

	@Override
	public Object getArrayType(){
		HasArray values = getValues();

		return values.getArrayType();
	}

	public HasArray getValues(){
		SingleBlockManager blockManager = getBlockManager();

		return blockManager.getOnlyBlockValue();
	}

	public SingleBlockManager getBlockManager(){

		// Pandas 1.0
		if(hasattr("_data")){
			return get("_data", SingleBlockManager.class);
		}

		// Pandas 1.1+
		return get("_mgr", SingleBlockManager.class);
	}

	public Series setBlockManager(SingleBlockManager singleBlockManager){
		setattr("_mgr", singleBlockManager);

		return this;
	}

	public String getName(){
		return getOptionalString("name");
	}

	public String getTyp(){
		return getString("_typ");
	}
}