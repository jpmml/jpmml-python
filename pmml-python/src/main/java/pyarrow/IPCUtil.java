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

import org.apache.arrow.flatbuf.Field;
import org.apache.arrow.flatbuf.FieldNode;
import org.apache.arrow.flatbuf.FloatingPoint;
import org.apache.arrow.flatbuf.Int;
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

		String name = field.name();
		int typeType = (int)field.typeType();

		result.put("name", name);
		result.put("typeType", typeType);

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

		result.put("length", recordBatch.length());

		FieldNode node = recordBatch.nodes(0);

		result.put("nodes", Collections.singletonList(new long[]{node.length(), node.nullCount()}));

		List<byte[]> buffers = new ArrayList<>();

		int bodyStart = recordBatchInfo.getNextOffset();

		for(int i = 0; i < recordBatch.buffersLength(); i++){
			org.apache.arrow.flatbuf.Buffer arrowBuffer = recordBatch.buffers(i);

			int offset = Math.toIntExact(arrowBuffer.offset());
			int length = Math.toIntExact(arrowBuffer.length());

			byte[] arrowBufferBytes = new byte[length];

			System.arraycopy(state, bodyStart + offset, arrowBufferBytes, 0, length);

			buffers.add(arrowBufferBytes);
		}

		result.put("buffers", buffers);

		return result;
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