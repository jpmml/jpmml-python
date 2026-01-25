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
package org.jpmml.python;

import java.io.IOException;

import net.razorvine.pickle.objects.ClassDict;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionTest extends UnpicklerTest {

	@Test
	public void python27() throws IOException {
		unpickleNumPyFunctions("python-2.7_numpy-1.16.6");
	}

	@Test
	public void python34() throws IOException {
		unpickleNumPyFunctions("python-3.4_numpy-1.13.3");
	}

	@Test
	public void python37() throws IOException {
		unpickleNumPyFunctions("python-3.7_numpy-1.20.0");
	}

	@Test
	public void python38() throws IOException {
		unpickleNumPyFunctions("python-3.8_numpy-1.20.1");
	}

	@Test
	public void python39() throws IOException {
		unpickleMathFunctions("3.9");

		String[] numpyVersions = {
			"1.20.2", "1.22.1",
			"1.23.4", "1.24.1",
			"1.26.2", "2.0.0",
		};

		unpickleNumPyFunctions("3.9", numpyVersions);
	}

	@Test
	public void python311() throws IOException {
		unpickleMathFunctions("3.11");

		String[] numpyVersions = {
			"1.23.4", "1.24.1",
			"1.26.2", "2.0.0",
			"2.1.2", "2.2.3", "2.3.5", "2.4.1"
		};

		unpickleNumPyFunctions("3.11", numpyVersions);
	}

	@Test
	public void python312() throws Exception {
		unpickleMathFunctions("3.12");

		String[] numpyVersions = {
			"1.26.2", "2.0.0",
			"2.1.2", "2.2.3", "2.3.5", "2.4.1"
		};

		unpickleNumPyFunctions("3.12", numpyVersions);
	}

	@Test
	public void python313() throws Exception {
		unpickleMathFunctions("3.13");

		String[] numpyVersions = {
			"1.26.2", "2.0.0",
			"2.1.2", "2.2.3", "2.3.5", "2.4.1"
		};

		unpickleNumPyFunctions("3.13", numpyVersions);
	}

	static
	private void unpickleMathFunctions(String pythonVersion) throws IOException {
		String[] names = {"acos", "asin", "atan", "atan2", "ceil", "cos", "cosh", "degrees", "exp", "expm1", "fabs", "floor", "hypot", "isnan", "log", "log1p", "log10", "pow", "radians", "sin", "sinh", "sqrt", "tan", "tanh", "trunc"};

		for(String name : names){
			String prefix = "python-" + pythonVersion + "_" + "math";

			Object object = unpickle("func", prefix + "_" + name + ".pkl");

			Identifiable identifiable = (Identifiable)toClassDict(object);

			assertEquals(name, identifiable.getName());
		}
	}

	static
	private void unpickleNumPyFunctions(String pythonVersion, String... numpyVersions) throws IOException {

		for(String numpyVersion : numpyVersions){
			String prefix = "python-" + pythonVersion + "_" + "numpy-" + numpyVersion;

			unpickleNumPyFunctions(prefix);
		}
	}

	static
	private void unpickleNumPyFunctions(String prefix) throws IOException {
		String[] names = {"absolute", "arccos", "arcsin", "arctan", "arctan2", "ceil", "clip", "cos", "cosh", "degrees", "rad2deg", "exp", "expm1", "floor", "fmax", "fmin", "hypot", "log", "log1p", "log10", "negative", "power", "radians", "deg2rad", "reciprocal", "rint", "sign", "sin", "sinh", "sqrt", "square", "tan", "tanh"};

		for(String name : names){
			Object object = unpickle("ufunc", prefix + "_" + name + ".pkl");

			Identifiable identifiable = (Identifiable)toClassDict(object);

			assertEquals(name, identifiable.getName());
		}
	}

	static
	private ClassDict toClassDict(Object object){

		if(object instanceof PythonObjectConstructor){
			PythonObjectConstructor dictConstructor = (PythonObjectConstructor)object;

			object = dictConstructor.newObject();
		}

		return (ClassDict)object;
	}
}