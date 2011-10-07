package crimsonwoods.android.apps.KinectServer;

import java.io.IOException;
import java.util.HashMap;

import crimsonwoods.android.apps.KinectServer.NUI.*;
import crimsonwoods.android.apps.KinectServer.NUI.SkeletonData.SkeletonTrackingState;
import crimsonwoods.android.apps.KinectServer.protocol.*;

public abstract class KinectServerState {
	public enum State {
		Untracked,
		Tracked,
	}
	
	private static HashMap<State, KinectServerState> stateMap = new HashMap<KinectServerState.State, KinectServerState>();
	
	public static void registerState(State key, KinectServerState value) {
		stateMap.put(key, value);
	}
	
	public static KinectServerState findState(State key) {
		return stateMap.get(key);
	}
	
	private State mState;
	
	protected KinectServerState(State s) {
		mState = s;
	}
	
	public State getState() {
		return mState;
	}
	
	protected abstract State transitNextState(KinectServerStateContext context);
	protected abstract boolean canTransitNextState(KinectServerStateContext context);
	protected abstract void processCommand(KinectServerStateContext context, KinectServerProtocolCommand command);
	
	public void perform(KinectServerStateContext context) throws IOException {
		final KinectServerProtocolCommand command = KinectServerProtocolParser.parse(context.getSocket());
		processCommand(context, command);
		if (canTransitNextState(context)) {
			final State state = transitNextState(context);
			final KinectServerState nextState = findState(state);
			context.setCurrentState(nextState);
		}
	}
	
	public static class KinectServerStateTracked extends KinectServerState {
		public KinectServerStateTracked() {
			super(State.Tracked);
		}

		@Override
		protected State transitNextState(KinectServerStateContext context) {
			return State.Untracked;
		}

		@Override
		protected boolean canTransitNextState(KinectServerStateContext context) {
			return !context.isTracked();
		}

		@Override
		protected void processCommand(KinectServerStateContext context, KinectServerProtocolCommand command) {
			if (null == command) {
				return;
			}
			if (command instanceof KinectServerProtocolNotification) {
				final KinectServerProtocolNotification notification = (KinectServerProtocolNotification)command;
				final SkeletonFrame frame = notification.getSkeletonFrame();
				context.getRenderer().setSkeletonFrame(frame);
			}
		}
	}
	
	public static class KinectServerStateUntracked extends KinectServerState {
		public KinectServerStateUntracked() {
			super(State.Untracked);
		}

		@Override
		protected State transitNextState(KinectServerStateContext context) {
			return State.Tracked;
		}

		@Override
		protected boolean canTransitNextState(KinectServerStateContext context) {
			return context.isTracked();
		}

		@Override
		protected void processCommand(KinectServerStateContext context, KinectServerProtocolCommand command) {
			if (null == command) {
				return;
			}
			if (command instanceof KinectServerProtocolNotification) {
				final KinectServerProtocolNotification notification = (KinectServerProtocolNotification)command;
				final SkeletonFrame frame = notification.getSkeletonFrame();
				final SkeletonData[] skeletonData = frame.getSkeletons();
				for(final SkeletonData data : skeletonData) {
					if (data.getTrackingState() == SkeletonTrackingState.Tracked) {
						context.setTracked(true);
						break;
					}
				}
			}
		}
	}
}
