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
package pyarrow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.arrow.flatbuf.DictionaryBatch;
import org.apache.arrow.flatbuf.DictionaryEncoding;
import org.apache.arrow.flatbuf.Field;
import org.apache.arrow.flatbuf.FieldNode;
import org.apache.arrow.flatbuf.FloatingPoint;
import org.apache.arrow.flatbuf.Int;
import org.apache.arrow.flatbuf.KeyValue;
import org.apache.arrow.flatbuf.Message;
import org.apache.arrow.flatbuf.MessageHeader;
import org.apache.arrow.flatbuf.RecordBatch;
import org.apache.arrow.flatbuf.Schema;
import org.apache.arrow.flatbuf.Type;

public class IPCUtil {

	private IPCUtil(){
	}

	static
	public HashMap<String, Object> parseSeries(byte[] state){
		HashMap<String, Object> result = new HashMap<>();

		ByteBuffer buffer = ByteBuffer.wrap(state)
			.order(ByteOrder.LITTLE_ENDIAN);

		MessageInfo schemaInfo = readMessage(MessageHeader.Schema, buffer, 0);
		Schema schema = (Schema)(schemaInfo.getMessage()).header(new Schema());

		if(schema.fieldsLength() != 1){
			throw new IllegalArgumentException();
		}

		Field field = schema.fields(0);

		DictionaryEncoding dictionaryEncoding = field.dictionary();
		String name = field.name();
		int typeType = (int)field.typeType();

		result.put("name", name);
		result.put("typeType", typeType);

		if(dictionaryEncoding != null){
			Int indexType = dictionaryEncoding.indexType();

			result.put("bitWidth", indexType.bitWidth());
			result.put("signed", indexType.isSigned());

			MessageInfo dictionaryBatchInfo = readMessage(MessageHeader.DictionaryBatch, buffer, schemaInfo.getNextOffset());
			DictionaryBatch dictionaryBatch = (DictionaryBatch)(dictionaryBatchInfo.getMessage()).header(new DictionaryBatch());

			polars.series.Series categories = new polars.series.Series()
				.setTypeType(typeType);

			categories.putAll(parseRecordBatch(state, dictionaryBatch.data(), dictionaryBatchInfo.getNextOffset()));

			result.put("categories", categories);

			Object dtype;

			if(isEnum(field)){
				dtype = new polars.datatypes.Enum()
					.setCategories(categories);
			} else

			{
				dtype = new polars.datatypes.Categorical();
			}

			result.put("dtype", dtype);

			int recordBatchOffset = dictionaryBatchInfo.getNextOffset() + Math.toIntExact((dictionaryBatchInfo.getMessage()).bodyLength());

			MessageInfo recordBatchInfo = readMessage(MessageHeader.RecordBatch, buffer, recordBatchOffset);
			RecordBatch recordBatch = (RecordBatch)(recordBatchInfo.getMessage()).header(new RecordBatch());

			result.putAll(parseRecordBatch(state, recordBatch, recordBatchInfo.getNextOffset()));
		} else

		{
			switch(typeType){
				case Type.Int:
					{
						Int intType = (Int)field.type(new Int());

						result.put("bitWidth", intType.bitWidth());
						result.put("signed", intType.isSigned());
					}
					break;
				case Type.FloatingPoint:
					{
						FloatingPoint fpType = (FloatingPoint)field.type(new FloatingPoint());

						result.put("precision", (int)fpType.precision());
					}
					break;
				default:
					break;
			}

			MessageInfo recordBatchInfo = readMessage(MessageHeader.RecordBatch, buffer, schemaInfo.getNextOffset());
			RecordBatch recordBatch = (RecordBatch)(recordBatchInfo.getMessage()).header(new RecordBatch());

			result.putAll(parseRecordBatch(state, recordBatch, recordBatchInfo.getNextOffset()));
		}

		return result;
	}

	static
	private boolean isEnum(Field field){

		for(int i = 0; i < field.customMetadataLength(); i++){
			KeyValue keyValue = field.customMetadata(i);

			String key = keyValue.key();
			String value = keyValue.value();

			// Legacy
			if(Objects.equals(key, "POLARS.CATEGORICAL_TYPE") && Objects.equals(value, "ENUM")){
				return true;
			} else

			// Modern
			if(Objects.equals(key, "_PL_ENUM_VALUES2")){
				return true;
			}
		}

		return false;
	}

	static
	private Map<String, ?> parseRecordBatch(byte[] state, RecordBatch recordBatch, int bodyStart){
		Map<String, Object> result = new HashMap<>();

		result.put("length", recordBatch.length());

		FieldNode node = recordBatch.nodes(0);

		result.put("nodes", Collections.singletonList(new long[]{node.length(), node.nullCount()}));
		result.put("buffers", parseBuffers(state, recordBatch, bodyStart));

		return result;
	}

	static
	private List<byte[]> parseBuffers(byte[] state, RecordBatch recordBatch, int bodyStart){
		List<byte[]> buffers = new ArrayList<>();

		for(int i = 0; i < recordBatch.buffersLength(); i++){
			org.apache.arrow.flatbuf.Buffer arrowBuffer = recordBatch.buffers(i);

			int offset = Math.toIntExact(arrowBuffer.offset());
			int length = Math.toIntExact(arrowBuffer.length());

			byte[] arrowBufferBytes = new byte[length];

			System.arraycopy(state, bodyStart + offset, arrowBufferBytes, 0, length);

			buffers.add(arrowBufferBytes);
		}

		return buffers;
	}

	static
	private MessageInfo readMessage(byte headerType, ByteBuffer buffer, int offset){
		int continuation = buffer.getInt(offset);
		if(continuation != 0xFFFFFFFF){
			throw new IllegalArgumentException();
		}

		int metadataStart = offset + 8;
		int metadataLength = buffer.getInt(offset + 4);

		ByteBuffer metadataBuffer = buffer.duplicate()
			.position(metadataStart)
			.slice()
			.order(ByteOrder.LITTLE_ENDIAN);

		Message message = Message.getRootAsMessage(metadataBuffer);
		if(message.headerType() != headerType){
			throw new IllegalArgumentException();
		}

		int nextOffset = metadataStart + metadataLength;

		return new MessageInfo(message, nextOffset);
	}

	static
	private class MessageInfo {

		private Message message = null;

		private int nextOffset = -1;


		private MessageInfo(Message message, int nextOffset){
			this.message = message;
			this.nextOffset = nextOffset;
		}

		public Message getMessage(){
			return this.message;
		}

		public int getNextOffset(){
			return this.nextOffset;
		}
	}
}