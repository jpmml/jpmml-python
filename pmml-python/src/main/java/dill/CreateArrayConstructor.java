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
package dill;

import java.util.Arrays;

import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.objects.ClassDictConstructor;
import numpy.core.NDArray;
import org.jpmml.python.CythonObjectConstructor;

public class CreateArrayConstructor extends CythonObjectConstructor {

	public CreateArrayConstructor(String module, String name){
		super(module, name, NDArray.class);
	}

	@Override
	public NDArray construct(Object[] args){

		if(args.length != 4){
			throw new PickleException(Arrays.deepToString(args));
		}

		ClassDictConstructor dictConstructor = (ClassDictConstructor)args[0];

		NDArray ndarray = (NDArray)dictConstructor.construct((Object[])args[1]);
		ndarray.__setstate__((Object[])args[2]);

		return ndarray;
	}
}