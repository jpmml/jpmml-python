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

import net.razorvine.pickle.PickleException;
import org.jpmml.python.CustomPythonObjectConstructor;

public class NDArrayBackedConstructor extends CustomPythonObjectConstructor {

	public NDArrayBackedConstructor(String module, String name, Class<? extends NDArrayBacked> clazz){
		super(module, name, clazz);
	}

	@Override
	public NDArrayBacked newObject(){
		return (NDArrayBacked)super.newObject();
	}

	@Override
	public NDArrayBacked construct(Object[] args){

		if(args.length != 3){
			throw new PickleException(Arrays.toString(args));
		}

		NDArrayBackedConstructor dictConstructor = (NDArrayBackedConstructor)args[0];

		return dictConstructor.newObject();
	}
}