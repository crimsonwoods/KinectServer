package crimsonwoods.android.apps.KinectServer.error;

@SuppressWarnings("serial")
public class UncompletedDataReadingException extends KinectServerRuntimeException {
	public UncompletedDataReadingException() {
		super();
	}
	public UncompletedDataReadingException(String message) {
		super(message);
	}
	public UncompletedDataReadingException(Throwable cause) {
		super(cause);
	}
	public UncompletedDataReadingException(String message, Throwable cause) {
		super(message, cause);
	}
}
