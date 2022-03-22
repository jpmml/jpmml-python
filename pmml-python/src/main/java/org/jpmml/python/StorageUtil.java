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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class StorageUtil {

	private StorageUtil(){
	}

	static
	public Storage createStorage(File file) throws IOException {
		PushbackInputStream is = new PushbackInputStream(new FileInputStream(file), 2);

		CompressedInputStreamStorage.Type type = CompressedInputStreamStorage.detectType(is);

		// If the file contains compressed data, create a limited-functionality (Compressed)InputStreamStorage.
		if(type != null){
			return new CompressedInputStreamStorage(is);
		}

		is.close();

		// Otherwise, create an unlimited-functionality FileStorage.
		return new FileStorage(file);
	}

	static
	public Storage createStorage(InputStream is) throws IOException {
		return createStorage(new PushbackInputStream(is, 2));
	}

	static
	public Storage createStorage(PushbackInputStream is) throws IOException {
		CompressedInputStreamStorage.Type type = CompressedInputStreamStorage.detectType(is);

		if(type != null){
			return new CompressedInputStreamStorage(is);
		}

		return new InputStreamStorage(is);
	}
}