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
package org.jpmml.python.testing;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import com.google.common.base.Equivalence;
import org.jpmml.converter.testing.ModelEncoderBatch;
import org.jpmml.evaluator.ResultField;
import org.jpmml.python.PickleUtil;
import org.jpmml.python.Storage;
import org.jpmml.python.StorageUtil;

abstract
public class PythonEncoderBatch extends ModelEncoderBatch {

	public PythonEncoderBatch(String algorithm, String dataset, Predicate<ResultField> columnFilter, Equivalence<Object> equivalence){
		super(algorithm, dataset, columnFilter, equivalence);
	}

	@Override
	abstract
	public PythonEncoderBatchTest getArchiveBatchTest();

	public String getPklPath(){
		return "/pkl/" + getAlgorithm() + getDataset() + ".pkl";
	}

	public Object loadPickle() throws IOException {
		InputStream is = open(getPklPath());

		try(Storage storage = StorageUtil.createStorage(is)){
			return PickleUtil.unpickle(storage);
		}
	}
}