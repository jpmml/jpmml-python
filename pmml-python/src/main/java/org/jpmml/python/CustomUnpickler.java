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
package org.jpmml.python;

import java.io.IOException;

import net.razorvine.pickle.Opcodes;
import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.Unpickler;

public class CustomUnpickler extends Unpickler {

	@Override
	protected Object dispatch(short key) throws PickleException, IOException {
		Object result = super.dispatch(key);

		if(key == Opcodes.GLOBAL || key == Opcodes.STACK_GLOBAL){
			Object head = super.stack.peek();

			if(head instanceof NullConstructor){
				NullConstructor nullConstructor = (NullConstructor)head;

				super.stack.pop();

				Object _null = nullConstructor.construct(new Object[0]);

				super.stack.add(_null);
			}
		}

		return result;
	}
}