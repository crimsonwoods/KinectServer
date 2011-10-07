package crimsonwoods.android.apps.KinectServer.NUI;

public final class Joint {
	public enum JointID {
		AnkleLeft(1),
        AnkleRight(2),
        ElbowLeft(3),
        ElbowRight(4),
        FootLeft(5),
        FootRight(6),
        HandLeft(7),
        HandRight(8),
        Head(9),
        HipCenter(10),
        HipLeft(11),
        HipRight(12),
        KneeLeft(13),
        KneeRight(14),
        ShoulderCenter(15),
        ShoulderLeft(16),
        ShoulderRight(17),
        Spine(18),
        WristLeft(19),
        WristRight(20),
		Count(20); // number of joints.
		
		private static final JointID[] jointIDs = new JointID[] {
			AnkleLeft,
	        AnkleRight,
	        ElbowLeft,
	        ElbowRight,
	        FootLeft,
	        FootRight,
	        HandLeft,
	        HandRight,
	        Head,
	        HipCenter,
	        HipLeft,
	        HipRight,
	        KneeLeft,
	        KneeRight,
	        ShoulderCenter,
	        ShoulderLeft,
	        ShoulderRight,
	        Spine,
	        WristLeft,
	        WristRight,
		};
		
		private int mValue;
		
		private JointID(int value) {
			mValue = value;
		}
		
		public int toInt() {
			return mValue;
		}
		
		public static JointID fromInt(int value) {
			return jointIDs[value-1];
		}
	}
	
	public enum JointTrackingState {
		Inferred(1),
		NotTracked(2),
		Tracked(3);
		
		private static final JointTrackingState[] jointTrackingStates = new JointTrackingState[] {
			Inferred,
			NotTracked,
			Tracked,
		};
		
		private int mValue;
		
		private JointTrackingState(int value) {
			mValue = value;
		}
		
		public int toInt() {
			return mValue;
		}
		
		public static JointTrackingState fromInt(int value) {
			return jointTrackingStates[value-1];
		}
	}
	
	private JointID jointId;
	private Vector position;
	private JointTrackingState trackingState;
	
	public Joint(JointID id, Vector pos, JointTrackingState state) {
		jointId = id;
		position = pos;
		trackingState = state;
	}

	public JointID getJointId() {
		return jointId;
	}

	public void setJointId(JointID jointId) {
		this.jointId = jointId;
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public JointTrackingState getTrackingState() {
		return trackingState;
	}

	public void setTrackingState(JointTrackingState trackingState) {
		this.trackingState = trackingState;
	}
	
}
