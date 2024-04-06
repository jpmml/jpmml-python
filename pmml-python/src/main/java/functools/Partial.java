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
package functools;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Streams;
import net.razorvine.pickle.IObjectConstructor;
import net.razorvine.pickle.PickleException;
import net.razorvine.pickle.objects.ClassDict;
import net.razorvine.pickle.objects.ClassDictConstructor;
import org.jpmml.python.CythonObject;
import org.jpmml.python.CythonObjectUtil;

public class Partial extends CythonObject implements IObjectConstructor {

	public Partial(String module, String name){
		super(module, name);
	}

	@Override
	public void __init__(Object[] args){
		super.__setstate__(CythonObjectUtil.createState(INIT_ATTRIBUTES, args));
	}

	@Override
	public void __setstate__(Object[] args){
		super.__setstate__(CythonObjectUtil.createState(SETSTATE_ATTRIBUTES, args));
	}

	@Override
	public Object construct(Object[] args) throws PickleException {
		ClassDictConstructor func = getFunc();
		Object[] funcArgs = getArgs();
		Map<String, ?> funcKeywords = getKeywords();

		funcArgs = Streams.concat(Arrays.stream(funcArgs), Arrays.stream(args))
			.toArray(Object[]::new);

		ClassDict result = (ClassDict)func.construct(funcArgs);
		result.putAll(funcKeywords);

		return result;
	}

	public ClassDictConstructor getFunc(){
		return get("func", ClassDictConstructor.class);
	}

	public Object[] getArgs(){
		return getTuple("args");
	}

	public Map<String, ?> getKeywords(){
		return getDict("kwds");
	}

	private static final String[] INIT_ATTRIBUTES = {
		"func"
	};

	private static final String[] SETSTATE_ATTRIBUTES = {
		"func",
		"args",
		"kwds",
		"namespace"
	};
}