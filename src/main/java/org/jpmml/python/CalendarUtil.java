/*
 * Copyright (c) 2020 Villu Ruusmann
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarUtil {

	private CalendarUtil(){
	}

	static
	public LocalDate toLocalDate(Calendar calendar){
		LocalDateTime dateTime = toLocalDateTime(calendar);

		return dateTime.toLocalDate();
	}

	static
	public LocalDateTime toLocalDateTime(Calendar calendar){
		ZoneId zoneId = ZoneId.systemDefault();

		TimeZone timeZone = calendar.getTimeZone();
		if(timeZone != null){
			zoneId = timeZone.toZoneId();
		}

		return LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
	}
}