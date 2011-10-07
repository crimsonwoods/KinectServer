package crimsonwoods.android.apps.KinectServer.NUI;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import crimsonwoods.android.apps.KinectServer.NUI.Joint.JointID;
import crimsonwoods.android.apps.KinectServer.NUI.Joint.JointTrackingState;

public class SkeletonData {
	public enum SkeletonQuality {
		ClippedRight,
		ClippedLeft,
		ClippedTop,
		ClippedBottom;
		
		public static EnumSet<SkeletonQuality> fromInt(int value) {
			EnumSet<SkeletonQuality> qualities = EnumSet.noneOf(SkeletonQuality.class);
			if (0x00 != (value & 0x01)) {
				qualities.add(ClippedRight);
			}
			if (0x00 != (value & 0x02)) {
				qualities.add(ClippedLeft);
			}
			if (0x00 != (value & 0x04)) {
				qualities.add(ClippedTop);
			}
			if (0x00 != (value & 0x08)) {
				qualities.add(ClippedBottom);
			}
			return qualities; 
		}
	}
	
	public enum SkeletonTrackingState {
		NotTracked(1),
		PositionOnly(2),
		Tracked(3);
		
		private static final SkeletonTrackingState[] skeletonTrackingStates = new SkeletonTrackingState[] {
			NotTracked,
			PositionOnly,
			Tracked,
		};
		
		private int value;
		
		private SkeletonTrackingState(int value) {
			this.value = value;
		}
		
		public int toInt() {
			return value;
		}
		
		public static SkeletonTrackingState fromInt(int value) {
			return skeletonTrackingStates[value-1];
		}
	}
	
	private JointCollection joints;
	private Vector position;
	private EnumSet<SkeletonQuality> quality;
	private int trackingId;
	private SkeletonTrackingState trackingState;
	private int userIndex;
	
	public SkeletonData() {
	}
	
	public SkeletonData(ByteBuffer buffer) {
		reload(buffer);
	}
	
	public void reload(ByteBuffer buffer) {
		trackingId = buffer.getInt();
		userIndex = buffer.getInt();
		trackingState = SkeletonTrackingState.fromInt(buffer.getInt());
		quality = SkeletonQuality.fromInt(buffer.getInt());
		
		float x, y, z, w;
		int jointCount;
		
		switch (trackingState) {
		case NotTracked:
			break;
		case PositionOnly:
			x = buffer.getFloat();
			y = buffer.getFloat();
			z = buffer.getFloat();
			w = buffer.getFloat();
			if (null == position) {
				position = new Vector(x, y, z, w);
			} else {
				position.setX(x);
				position.setY(y);
				position.setZ(z);
				position.setW(w);
			}
			break;
		case Tracked:
			x = buffer.getFloat();
			y = buffer.getFloat();
			z = buffer.getFloat();
			w = buffer.getFloat();
			if (null == position) {
				position = new Vector(x, y, z, w);
			} else {
				position.setX(x);
				position.setY(y);
				position.setZ(z);
				position.setW(w);
			}
			if (null == joints) {
				joints = new JointCollection();
			}
			jointCount = buffer.getInt();
			for (int i = 0; i < jointCount; ++i) {
				int jointId = buffer.getInt();
				int trackingState = buffer.getInt();
				x = buffer.getFloat();
				y = buffer.getFloat();
				z = buffer.getFloat();
				w = buffer.getFloat();
				joints.set(new Joint(JointID.fromInt(jointId), new Vector(x, y, z, w), JointTrackingState.fromInt(trackingState)));
			}
			break;
		}
	}

	public JointCollection getJoints() {
		return joints;
	}

	public void setJoints(JointCollection joints) {
		this.joints = joints;
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public EnumSet<SkeletonQuality> getQuality() {
		return quality;
	}

	public void setQuality(EnumSet<SkeletonQuality> quality) {
		this.quality = quality;
	}

	public int getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(int trackingId) {
		this.trackingId = trackingId;
	}

	public SkeletonTrackingState getTrackingState() {
		return trackingState;
	}

	public void setTrackingState(SkeletonTrackingState trackingState) {
		this.trackingState = trackingState;
	}

	public int getUserIndex() {
		return userIndex;
	}

	public void setUserIndex(int userIndex) {
		this.userIndex = userIndex;
	}

	@Override
	public String toString() {
		return String.format("TrackingID = %d, TrackingState = %s, UserIndex = %d", 
				trackingId, trackingState.name(), userIndex);
	}
}
