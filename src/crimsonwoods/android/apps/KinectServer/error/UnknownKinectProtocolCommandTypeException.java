package crimsonwoods.android.apps.KinectServer.error;

@SuppressWarnings("serial")
public class UnknownKinectProtocolCommandTypeException extends KinectServerRuntimeException {
	public UnknownKinectProtocolCommandTypeException() {
		super();
	}
	public UnknownKinectProtocolCommandTypeException(int value) {
		super(String.format("Unknown Command-Type : %d", value));
	}
	public UnknownKinectProtocolCommandTypeException(String message) {
		super(message);
	}
	public UnknownKinectProtocolCommandTypeException(Throwable cause) {
		super(cause);
	}
	public UnknownKinectProtocolCommandTypeException(String message, Throwable cause) {
		super(message, cause);
	}
}
