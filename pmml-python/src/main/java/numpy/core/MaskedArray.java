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
package numpy.core;

import java.util.Arrays;

import net.razorvine.pickle.PickleException;
import numpy.DType;
import org.jpmml.python.CythonObject;

public class MaskedArray extends CythonObject {

	public MaskedArray(String module, String name){
		super(module, name);
	}

	@Override
	public void __init__(Object[] args){

		if(args.length == 0){
			super.__init__(args);
		} else

		if(args.length == 4){
			super.__setstate__(
				new String[]{"data", "mask"},
				new Object[]{new NDArray(), new NDArray()}
			);
		} else

		{
			throw new PickleException(Arrays.deepToString(args));
		}
	}

	@Override
	public void __setstate__(Object[] args){

		if(args.length == 7){
			NDArray data = getData();
			data.__setstate__(new Object[]{null, args[1], args[2], args[3], args[4]});

			NDArray mask = getMask();
			mask.__setstate__(new Object[]{null, args[1], make_mask_descr((DType)args[2]), args[3], args[5]});

			setFillValue(args[6]);

			return;
		}

		super.__setstate__(args);
	}

	public NDArray getData(){
		return getOptional("data", NDArray.class);
	}

	public NDArray getMask(){
		return getOptional("mask", NDArray.class);
	}

	public Object getFillValue(){
		return getOptionalObject("fill_value");
	}

	public MaskedArray setFillValue(Object fillValue){
		setattr("fill_value", fillValue);

		return this;
	}

	static
	private DType make_mask_descr(DType dtype){
		// XXX
		return dtype;
	}
}