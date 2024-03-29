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

import builtins.GetAttr;
import net.razorvine.pickle.Opcodes;
import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.Unpickler;

public class CustomUnpickler extends Unpickler {

	@Override
	protected Object dispatch(short key) throws PickleException, IOException {
		Object result = super.dispatch(key);

		if(key == Opcodes.GLOBAL || key == Opcodes.STACK_GLOBAL){
			Object head = super.stack.peek();

			if(head instanceof IConstantConstructor){
				IConstantConstructor constantConstructor = (IConstantConstructor)head;

				super.stack.pop();

				Object value = constantConstructor.construct();

				super.stack.add(value);
			}
		} else

		// Python 3.11+
		if(key == Opcodes.REDUCE){
			Object head = super.stack.peek();

			if(head instanceof GetAttr){
				GetAttr getAttr = (GetAttr)head;

				Object obj = getAttr.getObj();
				String name = getAttr.getName();

				if(obj instanceof PythonEnumConstructor){
					PythonEnumConstructor enumConstructor = (PythonEnumConstructor)obj;

					super.stack.pop();

					PythonEnum _enum = enumConstructor.construct(new Object[]{name});

					super.stack.add(_enum);
				}
			}
		}

		return result;
	}
}