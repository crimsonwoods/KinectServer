package crimsonwoods.android.apps.KinectServer.error;

@SuppressWarnings("serial")
public class KinectServerRuntimeException extends RuntimeException {
	public KinectServerRuntimeException() {
		super();
	}
	public KinectServerRuntimeException(String message) {
		super(message);
	}
	public KinectServerRuntimeException(Throwable cause) {
		super(cause);
	}
	public KinectServerRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
