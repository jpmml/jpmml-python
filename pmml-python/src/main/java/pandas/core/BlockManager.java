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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jpmml.python.ClassDictUtil;
import org.jpmml.python.CythonObject;
import org.jpmml.python.HasArray;

public class BlockManager extends CythonObject {

	public BlockManager(String module, String name){
		super(module, name);
	}

	@Override
	public void __init__(Object[] args){

		if(args.length == 0){
			super.__init__(args);
		} else

		{
			super.__setstate__(ClassDictUtil.createAttributeMap(INIT_ATTRIBUTES, args));
		}
	}

	@Override
	public void __setstate__(Object[] args){

		if(args.length > 3){
			args = ClassDictUtil.extractArgs(args, 0, 3);
		}

		super.__setstate__(ClassDictUtil.createAttributeMap(SETSTATE_ATTRIBUTES, args));
	}

	public Index getColumnAxis(){
		List<Index> axesArray = getAxesArray();

		ClassDictUtil.checkSize(2, axesArray);

		return axesArray.get(0);
	}

	public Index getRowAxis(){
		List<Index> axesArray = getAxesArray();

		ClassDictUtil.checkSize(2, axesArray);

		return axesArray.get(1);
	}

	public List<Index> getAxesArray(){

		if(hasattr("axes_array")){
			return getIndexList("axes_array");
		}

		// Pandas 1.3+
		return getIndexList("axes");
	}

	public List<Index> getBlockItems(){

		if(hasattr("block_items")){
			return getIndexList("block_items");
		}

		// Pandas 1.3+
		throw new UnsupportedOperationException();
	}

	public BlockManager setBlockItems(List<Index> blockItems){
		setattr("block_items", blockItems);

		return this;
	}

	public List<HasArray> getBlockValues(){

		if(hasattr("block_values")){
			return getArrayList("block_values");
		}

		// Pandas 1.3+
		Object[] blocks = getTuple("blocks");

		return Arrays.stream(blocks)
			.map(block -> ((Block)block).get("values", HasArray.class))
			.collect(Collectors.toList());
	}

	public BlockManager setBlockValues(List<HasArray> blockValues){
		setattr("block_values", blockValues);

		return this;
	}

	public List<Index> getIndexList(String name){
		return getList(name, Index.class);
	}

	private static final String[] INIT_ATTRIBUTES = {
		"blocks",
		"axes"
	};

	// XXX
	private static final String[] SETSTATE_ATTRIBUTES = {
		"axes_array",
		"block_values",
		"block_items"
	};
}