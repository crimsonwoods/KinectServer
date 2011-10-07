package crimsonwoods.android.apps.KinectServer.protocol;

import java.nio.ByteBuffer;

import crimsonwoods.android.apps.KinectServer.NUI.SkeletonFrame;

public class KinectServerProtocolNotification extends KinectServerProtocolCommand {
	private SkeletonFrame skeletonFrame;
	
	public KinectServerProtocolNotification(KinectServerProtocolHeader header, ByteBuffer body) {
		super(header);
		skeletonFrame = new SkeletonFrame(body);
	}
	
	public SkeletonFrame getSkeletonFrame() {
		return skeletonFrame;
	}
	
	public void reload(KinectServerProtocolHeader header, ByteBuffer body) {
		setHeader(header);
		skeletonFrame.reload(body);
	}
}
