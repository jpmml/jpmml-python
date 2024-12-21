/*
 * Copyright (c) 2024 Villu Ruusmann
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
package numpy.core;

import java.util.Objects;

public class Complex {

	private Number real = null;

	private Number imaginary = null;


	public Complex(Number real, Number imaginary){
		setReal(real);
		setImaginary(imaginary);
	}

	@Override
	public int hashCode(){
		return 31 * Objects.hashCode(this.getReal()) + Objects.hashCode(this.getImaginary());
	}

	@Override
	public boolean equals(Object object){

		if(object instanceof Complex){
			Complex that = (Complex)object;

			return Objects.equals(this.getReal(), that.getReal()) && Objects.equals(this.getImaginary(), that.getImaginary());
		}

		return false;
	}

	public Number getReal(){
		return this.real;
	}

	private void setReal(Number real){
		this.real = Objects.requireNonNull(real);
	}

	public Number getImaginary(){
		return this.imaginary;
	}

	private void setImaginary(Number imaginary){
		this.imaginary = Objects.requireNonNull(imaginary);
	}
}