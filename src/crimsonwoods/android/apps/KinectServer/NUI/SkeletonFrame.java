package crimsonwoods.android.apps.KinectServer.NUI;

import java.nio.ByteBuffer;

public class SkeletonFrame {
	private int frameNumber;
	private SkeletonData[] skeletons;
	
	public SkeletonFrame(ByteBuffer buffer) {
		reload(buffer);
	}
	
	public void reload(ByteBuffer buffer) {
		frameNumber = buffer.getInt();
		final int skeletonDataCount = buffer.getInt();
		if ((null == skeletons) || (skeletons.length != skeletonDataCount)) {
			skeletons = new SkeletonData[skeletonDataCount];
		}
		for(int i = 0; i < skeletonDataCount; ++i) {
			skeletons[i] = new SkeletonData(buffer);
		}
	}

	public int getFrameNumber() {
		return frameNumber;
	}

	public void setFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
	}
	
	public SkeletonData[] getSkeletons() {
		return skeletons;
	}

	public void setSkeletons(SkeletonData[] skeletons) {
		this.skeletons = skeletons;
	}
}
