package crimsonwoods.android.apps.KinectServer.GUI;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.widget.TextView;

public class KinectServerGLSurfaceView extends GLSurfaceView {
	private KinectServerGLRenderer renderer;
	
	public KinectServerGLSurfaceView(Context context, TextView fpsView) {
		super(context);
		
		setDebugFlags(DEBUG_CHECK_GL_ERROR);
		
		renderer = new KinectServerGLRenderer(fpsView);
		setRenderer(renderer);
	}
	
	public KinectServerGLRenderer getRenderer() {
		return renderer;
	}
}
