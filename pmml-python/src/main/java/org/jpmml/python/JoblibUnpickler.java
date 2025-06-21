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

import java.io.IOException;
import java.io.InputStream;

import joblib.NDArrayWrapperConstructor;
import joblib.NumpyArrayWrapper;
import net.razorvine.pickle.Opcodes;
import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.Unpickler;
import numpy.core.NDArray;

public class JoblibUnpickler extends PythonUnpickler {

	private InputStream is = null;


	@Override
	public Object load(Storage storage) throws PickleException, IOException {
		PythonObjectConstructor[] constructors = {
			new NDArrayWrapperConstructor("joblib.numpy_pickle", "NDArrayWrapper", storage),
			new NDArrayWrapperConstructor("sklearn.externals.joblib.numpy_pickle", "NDArrayWrapper", storage),
		};

		for(PythonObjectConstructor constructor : constructors){
			Unpickler.registerConstructor(constructor.getModule(), constructor.getName(), constructor);
		}

		try(InputStream is = storage.getObject()){
			this.is = is;

			return load(is);
		} finally {
			this.is = null;
		}
	}

	@Override
	protected Object dispatch(short key) throws IOException {
		Object result = super.dispatch(key);

		if(key == Opcodes.BUILD){
			Object head = peekHead();

			// Modify the stack by replacing NumpyArrayWrapper with NDArray
			if(head instanceof NumpyArrayWrapper){
				NumpyArrayWrapper arrayWrapper = (NumpyArrayWrapper)head;

				NDArray array = arrayWrapper.toArray(this.is);

				replaceHead(array);
			}
		}

		return result;
	}
}