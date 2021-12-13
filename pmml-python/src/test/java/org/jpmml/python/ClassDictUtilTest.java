/*
 * Copyright (c) 2015 Villu Ruusmann
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

import net.razorvine.pickle.objects.ClassDict;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ClassDictUtilTest {

	@Test
	public void clearContent(){
		class ContentWrapper<E> extends ClassDict implements HasContent<E> {

			private E content = null;


			public ContentWrapper(E content){
				super(null, null);

				this.content = content;
			}

			@Override
			public E getContent(){
				return this.content;
			}

			@Override
			public void clearContent(){
				this.content = null;
			}
		}

		ClassDict parentDict = new ClassDict(null, null);

		ClassDict childDict = new ClassDict(null, null);

		parentDict.put("child", childDict);

		ContentWrapper<String> contentDict = new ContentWrapper<>("content");

		childDict.put("content", contentDict);

		assertNotNull(contentDict.getContent());

		ClassDictUtil.clearContent(parentDict);

		assertNull(contentDict.getContent());
	}
}