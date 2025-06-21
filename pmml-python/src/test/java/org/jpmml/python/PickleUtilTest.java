/*
 * Copyright (c) 2016 Villu Ruusmann
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

abstract
public class PickleUtilTest {

	static
	protected Object unpickle(String dir, String name) throws IOException {
		byte[] bytes;

		InputStream is = (PickleUtilTest.class).getResourceAsStream(("/" + dir + "/") + name);

		try {
			bytes = ByteStreams.toByteArray(is);
		} finally {
			is.close();
		}

		return unpickle(bytes);
	}

	static
	protected Object unpickle(byte[] bytes) throws IOException {
		InputStream is = new ByteArrayInputStream(bytes);

		try(Storage storage = StorageUtil.createStorage(is)){
			PythonUnpickler pythonUnpickler = new JoblibUnpickler();

			return pythonUnpickler.load(storage);
		}
	}

	static {
		ClassLoader clazzLoader = PickleUtilTest.class.getClassLoader();

		PickleUtil.init(clazzLoader, "python2pmml.properties");
	}
}