package crimsonwoods.android.apps.KinectServer;

import java.io.IOException;
import java.net.Socket;

import crimsonwoods.android.apps.KinectServer.GUI.KinectServerGLRenderer;

public class KinectServerStateContext {
	private Socket mSocket;
	private KinectServerGLRenderer mRenderer;
	private KinectServerState mCurrentState;
	private boolean isTracked = false;
	
	static {
		KinectServerState.registerState(KinectServerState.State.Tracked, new KinectServerState.KinectServerStateTracked());
		KinectServerState.registerState(KinectServerState.State.Untracked, new KinectServerState.KinectServerStateUntracked());
	}
	
	public KinectServerStateContext(Socket client, KinectServerGLRenderer renderer) {
		mSocket = client;
		mRenderer = renderer;
		mCurrentState = KinectServerState.findState(KinectServerState.State.Untracked);
	}
	
	public Socket getSocket() {
		return mSocket;
	}
	
	public KinectServerGLRenderer getRenderer() {
		return mRenderer;
	}
	
	public KinectServerState getCurrentState() {
		return mCurrentState;
	}
	
	public void setCurrentState(KinectServerState state) {
		mCurrentState = state;
	}
	
	public boolean isTracked() {
		return isTracked;
	}
	
	public void setTracked(boolean isTracked) {
		this.isTracked = isTracked;
	}
	
	public void perform() throws IOException {
		mCurrentState.perform(this);
	}
}
