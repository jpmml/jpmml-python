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
package pandas.core;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jpmml.python.NullConstructor;
import pandas.NDArrayBacked;

public class StringArray extends NDArrayBacked {

	public StringArray(String module, String name){
		super(module, name);
	}

	@Override
	public List<?> getArrayContent(){
		List<?> content = super.getArrayContent();

		Function<Object, Object> function = new Function<Object, Object>(){

			@Override
			public Object apply(Object object){

				if(object instanceof NullConstructor){
					NullConstructor nullConstructor = (NullConstructor)object;

					return nullConstructor.construct(new Object[0]);
				}

				return object;
			}
		};

		return Lists.transform(content, function);
	}
}