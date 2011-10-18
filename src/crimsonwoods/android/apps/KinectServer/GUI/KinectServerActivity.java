package crimsonwoods.android.apps.KinectServer.GUI;

import crimsonwoods.android.apps.KinectServer.KinectServer;
import crimsonwoods.android.apps.KinectServer.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class KinectServerActivity extends Activity {
	private KinectServer server;
	private KinectServerGLSurfaceView glSurfaceView;
	private TextView fpsView;
	private static Handler handler;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Debug.startMethodTracing("KinectServer");
        
        handler = new Handler();
        
        fpsView = new TextView(this);
        LayoutParams fpsViewLp = new LayoutParams(100, 80);
        fpsView.setBackgroundColor(Color.TRANSPARENT);
        fpsView.setTextColor(Color.WHITE);
        fpsView.setText("FPS = ");
        
        glSurfaceView = new KinectServerGLSurfaceView(this, fpsView);
        setContentView(glSurfaceView);
        
        addContentView(fpsView, fpsViewLp);
        
        KinectServerPreference.createInstance(this);
        
        startServer();
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		shutdownServer();
		//Debug.stopMethodTracing();
		Process.killProcess(Process.myPid());
	}
    
    public static Handler getHandler() {
    	return handler;
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!super.onCreateOptionsMenu(menu)) {
			return false;
		}
		getMenuInflater().inflate(R.menu.option_menu, menu);
		MenuItem item = menu.findItem(R.id.option_menu_config);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(KinectServerActivity.this, KinectServerPreferencesActivity.class);
				startActivityForResult(intent, 0);
				return true;
			}
		});
		item = menu.findItem(R.id.option_menu_quit);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				shutdownServer();
				KinectServerActivity.this.finish();
				return true;
			}
		});
		item = menu.findItem(R.id.option_menu_start);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				startServer();
				return true;
			}
		});
		item = menu.findItem(R.id.option_menu_stop);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				shutdownServer();
				return true;
			}
		});
		return true;
	}
    
    private void startServer() {
    	shutdownServer();
    	server = new KinectServer(glSurfaceView.getRenderer());
    	server.start();
    }
    
    private void shutdownServer() {
    	if (null != server) {
			try {
				server.shutdown();
				server = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

	@Override
	protected void onPause() {
		super.onPause();
		glSurfaceView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		glSurfaceView.onResume();
	}
}