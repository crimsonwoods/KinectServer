package crimsonwoods.android.apps.KinectServer.protocol;

import java.nio.ByteBuffer;

import crimsonwoods.android.apps.KinectServer.error.*;

public class KinectServerProtocolCommand {
	public enum CommandType {
		Request(1),
		Reply(2),
		Notification(3);
		
		private int mValue;
		
		private CommandType(int value) {
			mValue = value;
		}
		
		public int toInt() {
			return mValue;
		}
		
		public static CommandType fromInt(int value) {
			switch(value) {
			case 1:
				return Request;
			case 2:
				return Reply;
			case 3:
				return Notification;
			default:
				throw new UnknownKinectProtocolCommandTypeException(value);
			}
		}
	}
	
	public static class KinectServerProtocolHeader {
		private CommandType commandType;
		private int bodyLength;
		
		public static final int HEADER_SIZE = 8; // int32 + int32
		
		public KinectServerProtocolHeader(CommandType type, int length) {
			commandType = type;
			bodyLength = length;
		}
		
		public KinectServerProtocolHeader(ByteBuffer buffer) {
			commandType = CommandType.fromInt(buffer.getInt());
			bodyLength = buffer.getInt();
		}
		
		public CommandType getCommandType() {
			return commandType;
		}
		
		public int getBodyLength() {
			return bodyLength;
		}
	}
	
	private KinectServerProtocolHeader commandHeader;
	
	protected KinectServerProtocolCommand(KinectServerProtocolHeader header) {
		commandHeader = header;
	}
	
	public KinectServerProtocolHeader getHeader() {
		return commandHeader;
	}
	
	public void setHeader(KinectServerProtocolHeader value) {
		commandHeader = value;
	}
}
