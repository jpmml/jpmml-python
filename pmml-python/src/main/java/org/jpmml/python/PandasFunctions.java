/*
 * Copyright (c) 2025 Villu Ruusmann
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

import java.util.Map;

import org.dmg.pmml.PMMLFunctions;

public interface PandasFunctions extends Functions {

	PythonFunction ISNA = new UnaryFunction(PMMLFunctions.ISMISSING);

	PythonFunction NOTNA = new UnaryFunction(PMMLFunctions.ISNOTMISSING);

	Map<String, PythonFunction> REGISTRY = Map.ofEntries(
		Map.entry("isna", PandasFunctions.ISNA),
		Map.entry("isnull", PandasFunctions.ISNA),
		Map.entry("notna", PandasFunctions.NOTNA),
		Map.entry("notnull", PandasFunctions.NOTNA)
	);
}