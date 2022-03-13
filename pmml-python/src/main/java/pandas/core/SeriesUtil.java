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
package pandas.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jpmml.python.ClassDictUtil;
import org.jpmml.python.HasArray;

public class SeriesUtil {

	private SeriesUtil(){
	}

	static
	public <InK, OutK, InV, OutV> Map<OutK, OutV> toMap(Series series, Function<InK, OutK> keyFunction, Function<InV, OutV> valueFunction){
		SingleBlockManager blockManager = series.getBlockManager();

		Index blockItem = blockManager.getOnlyBlockItem();
		List<OutK> keys = Lists.transform((List<InK>)(blockItem.getValues()), keyFunction);

		HasArray blockValue = blockManager.getOnlyBlockValue();
		List<OutV> values = Lists.transform((List<InV>)blockValue.getArrayContent(), valueFunction);

		ClassDictUtil.checkSize(keys, values);

		Map<OutK, OutV> result = new LinkedHashMap<>();

		for(int i = 0; i < keys.size(); i++){
			result.put(keys.get(i), values.get(i));
		}

		return result;
	}
}