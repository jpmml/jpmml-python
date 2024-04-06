/*
 * Copyright (c) 2022 Villu Ruusmann
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
package pandas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.razorvine.pickle.PickleException;
import numpy.core.NDArray;
import org.jpmml.python.CythonObject;
import org.jpmml.python.HasArray;

public class NDArrayBacked extends CythonObject implements HasArray {

	public NDArrayBacked(String module, String name){
		super(module, name);
	}

	@Override
	public void __setstate__(Object[] args){

		if(args.length == 1){
			HashMap<String, Object> state = (HashMap<String, Object>)args[0];

			super.__setstate__(state);
		} else

		if(args.length == 3){
			Object[] stateArgs;

			if(args[0] instanceof NDArray){
				stateArgs = new Object[]{args[0], args[1]};
			} else

			if(args[1] instanceof NDArray){
				stateArgs = new Object[]{args[1], args[0]};
			} else

			{
				throw new PickleException(Arrays.toString(args));
			}

			super.__setstate__(SETSTATE_ATTRIBUTES, stateArgs);

			// Re-execute with truncated arguments
			if(args[2] instanceof Map){
				this.__setstate__(new Object[]{args[2]});
			}
		} else

		{
			throw new PickleException(Arrays.toString(args));
		}
	}

	@Override
	public List<?> getArrayContent(){
		NDArray content = getContent();

		return content.getArrayContent();
	}

	@Override
	public int[] getArrayShape(){
		NDArray content = getContent();

		return content.getArrayShape();
	}

	@Override
	public Object getArrayType(){
		return getDType();
	}

	public NDArray getContent(){
		return getNDArray();
	}

	public NDArray getNDArray(){
		return get("_ndarray", NDArray.class);
	}

	public Object getDType(){
		return getOptionalObject("_dtype");
	}

	private static final String[] SETSTATE_ATTRIBUTES = {
		"_ndarray",
		"_dtype"
	};
}