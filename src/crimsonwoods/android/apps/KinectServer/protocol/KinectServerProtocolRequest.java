package crimsonwoods.android.apps.KinectServer.protocol;

public class KinectServerProtocolRequest extends KinectServerProtocolCommand {
	public KinectServerProtocolRequest() {
		super(new KinectServerProtocolHeader(CommandType.Request, 0));
	}
}
