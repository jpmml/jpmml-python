/*
 * Copyright (c) 2021 Villu Ruusmann
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
import org.jpmml.python.CythonObjectConstructor;

public class FromBufferConstructor extends CythonObjectConstructor {

	public FromBufferConstructor(String module, String name){
		super(module, name, NDArray.class);
	}

	@Override
	public NDArray newObject(){
		return (NDArray)super.newObject();
	}

	@Override
	public NDArray construct(Object[] args){
		NDArray dict = newObject();

		if(args.length != 4){
			throw new PickleException(Arrays.deepToString(args));
		}

		dict.__setstate__(new Object[]{null, args[2], args[1], isFortranOrder((String)args[3]), args[0]});

		return dict;
	}

	static
	private boolean isFortranOrder(String format){

		switch(format){
			case "C":
			case "CONTIGUOUS":
			case "C_CONTIGUOUS":
				return false;
			case "F":
			case "FORTRAN":
			case "F_CONTIGUOUS":
				return true;
			default:
				throw new IllegalArgumentException(format);
		}
	}
}