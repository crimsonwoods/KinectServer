package crimsonwoods.android.apps.KinectServer.error;

@SuppressWarnings("serial")
public class InvalidKinectProtocolCommandTypeException extends
		KinectServerRuntimeException {
	public InvalidKinectProtocolCommandTypeException() {
		super();
	}
	public InvalidKinectProtocolCommandTypeException(String message) {
		super(message);
	}
	public InvalidKinectProtocolCommandTypeException(Throwable cause) {
		super(cause);
	}
	public InvalidKinectProtocolCommandTypeException(String message, Throwable cause) {
		super(message, cause);
	}
}
