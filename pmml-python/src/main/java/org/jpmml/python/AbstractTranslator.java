/*
 * Copyright (c) 2018 Villu Ruusmann
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Expression;
import org.dmg.pmml.Field;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.jpmml.converter.Feature;
import org.jpmml.converter.FeatureResolver;
import org.jpmml.converter.FeatureUtil;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.converter.TypeUtil;

abstract
public class AbstractTranslator implements FeatureResolver {

	private Scope scope = null;

	private Map<String, FunctionDef> functionDefs = new LinkedHashMap<>();


	public AbstractTranslator(){
	}

	@Override
	public Feature resolveFeature(String name){
		Scope scope = ensureScope();

		return scope.resolveFeature(name);
	}

	public PMMLEncoder ensureEncoder(){
		Scope scope = ensureScope();

		PMMLEncoder encoder = scope.getEncoder();
		if(encoder == null){
			throw new IllegalStateException();
		}

		return encoder;
	}

	public Scope ensureScope(){
		Scope scope = getScope();

		if(scope == null){
			throw new IllegalStateException();
		}

		return scope;
	}

	public FunctionDef getFunctionDef(String name){
		Map<String, FunctionDef> functionDefs = getFunctionDefs();

		return functionDefs.get(name);
	}

	public void addFunctionDef(String string){
		Map<String, FunctionDef> functionDefs = getFunctionDefs();

		FunctionDef functionDef;

		try {
			FunctionDefParser functionDefParser = new FunctionDefParser();

			functionDef = functionDefParser.parseFunctionDef(string);
		} catch(ParseException pe){
			throw new IllegalArgumentException("Python function definition \'" + toSingleLine(string) + "\' is either invalid or not supported", pe);
		}

		functionDefs.put(functionDef.getName(), functionDef);
	}

	public Expression encodeFunction(String name, List<?> arguments){
		FunctionDef functionDef = getFunctionDef(name);

		if(functionDef != null){
			PMMLEncoder encoder = ensureEncoder();

			DerivedField derivedField = encoder.getDerivedField(name);
			if(derivedField == null){
				List<FunctionDef.Parameter> parameters = functionDef.getParameters();

				ClassDictUtil.checkSize(parameters, arguments);

				List<Feature> features = arguments.stream()
					.map(argument -> {

						if(argument instanceof FieldRef){
							FieldRef fieldRef = (FieldRef)argument;

							Field<?> field = encoder.getField(fieldRef.requireField());

							return FeatureUtil.createFeature(field, encoder);
						}

						return (Feature)argument;
					})
					.collect(Collectors.toList());

				Map<String, Feature> argumentMap = new LinkedHashMap<>();

				for(int i = 0; i < parameters.size(); i++){
					FunctionDef.Parameter parameter = parameters.get(i);
					Feature feature = features.get(i);

					argumentMap.put(parameter.getName(), feature);
				}

				Scope scope = new Scope(encoder){

					@Override
					public Feature getFeature(String name){
						Feature feature = resolveFeature(name);

						if(feature != null){
							return feature;
						}

						throw new IllegalArgumentException("Name \'" + name + "\' is not defined");
					}

					@Override
					public Feature getFeature(String name, int columnIndex){
						getFeature(name);

						throw new IllegalArgumentException("Name \'" + name + "\' is not subscriptable");
					}

					@Override
					public Feature getFeature(String name, String columnName){
						getFeature(name);

						throw new IllegalArgumentException("Name \'" + name + "\' is not subscriptable");
					}

					@Override
					public Feature resolveFeature(String name){
						return argumentMap.get(name);
					}
				};

				ExpressionTranslator expressionTranslator = new ExpressionTranslator(scope){

					@Override
					public FunctionDef getFunctionDef(String name){
						return AbstractTranslator.this.getFunctionDef(name);
					}
				};

				derivedField = expressionTranslator.translateDef(functionDef.getString());
			}

			return new FieldRef(derivedField);
		}

		List<Expression> expressions = arguments.stream()
			.map(argument -> {

				if(argument instanceof Feature){
					Feature feature = (Feature)argument;

					return feature.ref();
				}

				return (Expression)argument;
			})
			.collect(Collectors.toList());

		return FunctionUtil.encodeFunction(name, expressions);
	}

	protected DerivedField createDerivedField(String name, Expression expression){
		PMMLEncoder encoder = ensureEncoder();

		OpType opType = null;
		DataType dataType = TypeUtil.getDataType(expression, this);

		if(dataType != null){
			opType = TypeUtil.getOpType(dataType);
		} else

		// XXX
		{
			opType = OpType.CONTINUOUS;
			dataType = DataType.DOUBLE;
		}

		return encoder.createDerivedField(name, opType, dataType, expression);
	}

	public Scope getScope(){
		return this.scope;
	}

	public void setScope(Scope scope){
		this.scope = Objects.requireNonNull(scope);
	}

	public Map<String, FunctionDef> getFunctionDefs(){
		return this.functionDefs;
	}

	static
	public String toSingleLine(String string){
		return string
			.replaceAll("\t", "\\\\t")
			.replaceAll("\n", "\\\\n");
	}
}