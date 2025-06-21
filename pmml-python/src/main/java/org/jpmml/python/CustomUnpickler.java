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
import java.io.InputStream;

import builtins.GetAttr;
import net.razorvine.pickle.Opcodes;
import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.Unpickler;

public class CustomUnpickler extends Unpickler {

	public Object load(Storage storage) throws PickleException, IOException {

		try(InputStream is = storage.getObject()){
			return load(is);
		}
	}

	@Override
	protected Object dispatch(short key) throws PickleException, IOException {
		Object result = super.dispatch(key);

		if(key == Opcodes.GLOBAL || key == Opcodes.STACK_GLOBAL){
			Object head = peekHead();

			if(head instanceof IConstantConstructor){
				IConstantConstructor constantConstructor = (IConstantConstructor)head;

				Object value = constantConstructor.construct();

				replaceHead(value);
			}
		} else

		// Python 3.11+
		if(key == Opcodes.REDUCE){
			Object head = peekHead();

			if(head instanceof GetAttr){
				GetAttr getAttr = (GetAttr)head;

				Object obj = getAttr.getObj();
				String name = getAttr.getName();

				if(obj instanceof PythonEnumConstructor){
					PythonEnumConstructor enumConstructor = (PythonEnumConstructor)obj;

					PythonEnum _enum = enumConstructor.construct(new Object[]{name});

					replaceHead(_enum);
				}
			}
		}

		return result;
	}

	protected Object peekHead(){
		return super.stack.peek();
	}

	protected void replaceHead(Object object){
		super.stack.pop();
		super.stack.add(object);
	}
}