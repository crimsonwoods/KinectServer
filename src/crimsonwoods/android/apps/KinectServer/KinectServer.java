package crimsonwoods.android.apps.KinectServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.net.ServerSocketFactory;

import crimsonwoods.android.apps.KinectServer.GUI.KinectServerGLRenderer;
import crimsonwoods.android.apps.KinectServer.GUI.KinectServerPreference;
import crimsonwoods.android.apps.KinectServer.error.KinectServerRuntimeException;

import android.util.Log;

public class KinectServer extends Thread {
	public final static String LOG_TAG = "KinectServer";
	
	private KinectServerGLRenderer renderer;
	private volatile boolean isQuit = false;
	
	public KinectServer(KinectServerGLRenderer renderer) {
		this.renderer = renderer;
		setName("KinectServerThread");
	}
	
	public void shutdown() throws InterruptedException {
		synchronized(this) {
			isQuit = true;
		}
		join();
	}
	
	private synchronized boolean isShutdown() {
		return isQuit;
	}
	
	@Override
	public void run() {
		ServerSocket sock = null;
		try {
			try {
				sock = ServerSocketFactory.getDefault().createServerSocket(KinectServerPreference.getInstance().getPortNumber());
				sock.setReuseAddress(true);
				sock.setSoTimeout(100);
				Log.i(LOG_TAG, "start KinectServer");
				for(; !isShutdown();) {
					Socket client = null;
					try {
						try {
							client = sock.accept();
						} catch (SocketTimeoutException e) {
							continue;
						}
						
						Log.i(LOG_TAG, "connect client from " + client.getInetAddress().getHostAddress());
						
						KinectServerStateContext context = new KinectServerStateContext(client, renderer);
						
						try {
							for (; !context.getSocket().isClosed() && !isShutdown();) {
								try {
									// set socket options.
									client.setSoTimeout(0);
									
									context.perform();
								} catch (SocketTimeoutException e) {
									// nothing to do
								}
							}
						} catch (IOException e) {
							Log.e(LOG_TAG, "I/O Exception", e);
						} catch (KinectServerRuntimeException e) {
							Log.e(LOG_TAG, "Uncaught Runtime Exception", e);
						}
					} finally {
						if (null != client) {
							client.close();
							Log.i(LOG_TAG, "disconnect client");
						}
					}
				}
			} finally {
				if (null != sock) {
					sock.close();
				}
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Uncaught Exception", e);
		} finally {
			Log.i(LOG_TAG, "finish KinectServer");
		}
	}
}
