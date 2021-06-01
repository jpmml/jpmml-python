/*
 * Copyright (c) 2019 Villu Ruusmann
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

import org.jpmml.python.CustomPythonObject;
import org.jpmml.python.HasArray;

public class BlockManager extends CustomPythonObject {

	public BlockManager(String module, String name){
		super(module, name);
	}

	public List<Index> getAxesArray(){
		return getList("axes_array", Index.class);
	}

	public List<Index> getBlockItems(){
		return getList("block_items", Index.class);
	}

	public BlockManager setBlockItems(List<Index> blockItems){
		put("block_items", blockItems);

		return this;
	}

	public List<HasArray> getBlockValues(){
		return getList("block_values", HasArray.class);
	}

	public BlockManager setBlockValues(List<HasArray> blockValues){
		put("block_values", blockValues);

		return this;
	}

	@Override
	public void __setstate__(Object[] args){

		if(args.length > 3){
			Object[] newArgs = new Object[3];

			System.arraycopy(args, 0, newArgs, 0, newArgs.length);

			args = newArgs;
		}

		super.__setstate__(createAttributeMap(SETSTATE_ATTRIBUTES, args));
	}

	private static final String[] SETSTATE_ATTRIBUTES = {
		"axes_array",
		"block_values",
		"block_items"
	};
}