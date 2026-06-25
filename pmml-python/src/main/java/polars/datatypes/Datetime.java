/*
 * Copyright (c) 2026 Villu Ruusmann
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
package polars.datatypes;

import org.jpmml.converter.ExceptionUtil;
import org.jpmml.python.Attribute;
import org.jpmml.python.InvalidAttributeException;
import org.jpmml.python.PythonFormatterUtil;

public class Datetime extends DataType {

	public Datetime(java.lang.String module, java.lang.String name){
		super(module, name);
	}

	@Override
	public org.dmg.pmml.DataType getDataType(){
		java.lang.String timeZone = getTimeZone();

		if(timeZone != null){
			throw new InvalidAttributeException("Attribute " + ExceptionUtil.formatName("time_zone") + " must be set to the missing (" + PythonFormatterUtil.formatValue(null) + ") value", new Attribute(this, "time_zone"));
		}

		return org.dmg.pmml.DataType.DATE_TIME;
	}

	public java.lang.String getTimeUnit(){
		return getString("time_unit");
	}

	public java.lang.String getTimeZone(){
		return getOptionalString("time_zone");
	}
}