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
package numpy.random;

import java.util.Arrays;

import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.objects.ClassDictConstructor;

public class BitGeneratorUtil {

	private BitGeneratorUtil(){
	}

	static
	public BitGenerator createBitGenerator(Object[] args){

		// Numpy 1.23.4
		if(args.length == 1){
			String bitGeneratorName = (String)args[0];

			// XXX
			BitGenerator bitGenerator = new BitGenerator("numpy.random._" + bitGeneratorName.toLowerCase(), bitGeneratorName);

			return bitGenerator;
		} else

		// Numpy 1.24.1+
		if(args.length == 2){
			String bitGeneratorName = (String)args[0];
			ClassDictConstructor bitGeneratorCtor = (ClassDictConstructor)args[1];

			BitGenerator bitGenerator = (BitGenerator)bitGeneratorCtor.construct(new Object[]{bitGeneratorName});

			return bitGenerator;
		} else

		{
			throw new PickleException(Arrays.deepToString(args));
		}
	}
}