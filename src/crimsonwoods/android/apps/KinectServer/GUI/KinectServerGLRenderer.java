package crimsonwoods.android.apps.KinectServer.GUI;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import crimsonwoods.android.apps.KinectServer.NUI.*;
import crimsonwoods.android.apps.KinectServer.NUI.SkeletonData.SkeletonTrackingState;
import crimsonwoods.android.apps.KinectServer.NUI.Joint.JointID;

import android.graphics.Color;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.widget.TextView;

public class KinectServerGLRenderer implements Renderer {
	private ColorX bgColor;
	private ColorX boneColor;
	private SkeletonFrame[] skeletonFrames;
	private int skeletonFrameIndex;
	private final IntBuffer jointColorBuffer;
	private final IntBuffer boneColorBuffer;
	private SkeletonFrame previousFrame = null;
	private final TextView fpsView;
	private long startTime;
	private int frameCount;
	private int dataFrameCount;
	private final FixedPoint pointSize = new FixedPoint(10.0f);
	private final FixedPoint lineWidth = new FixedPoint(5.0f);
	private final IntBuffer pointBuffer = makeIntBuffer(3 * JointID.Count.toInt());
	private final IntBuffer lineBuffer = makeIntBuffer(3 * 5);
	private final int[] pointArray = new int[3];
	private final int[] lineArray = new int[3];
	
	private final static float Z_MAX = 5.0f;
	private final static int SKELETON_FRAME_COUNT = 4;
	
	public KinectServerGLRenderer(TextView fpsView) {
		skeletonFrames = new SkeletonFrame[SKELETON_FRAME_COUNT];
		skeletonFrameIndex = 0;
		
		bgColor = new ColorX();
		boneColor = new ColorX(Color.RED);
		
		Random r = new Random(System.currentTimeMillis());
		final int jointCount = Joint.JointID.Count.toInt();
		jointColorBuffer = makeIntBuffer(Joint.JointID.Count.toInt() * ColorF.getFloatBufferSize());
		boneColorBuffer = makeIntBuffer(5 * ColorF.getFloatBufferSize());
		
		final float[] hsv = new float[3];
		int color;
		for (int i = 0; i < jointCount; ++i) {
			do {
				color = r.nextInt();
				Color.colorToHSV(color, hsv);
			} while (hsv[2] < 0.4f);
			new ColorX(color).putIntBuffer(jointColorBuffer);
		}
		
		for (int i = 0; i < 5; ++i) {
			boneColor.putIntBuffer(boneColorBuffer);
		}
		
		jointColorBuffer.position(0);
		boneColorBuffer.position(0);
		
		this.fpsView = fpsView;
	}
	
	public void onDrawFrame(GL10 gl) {
		gl.glClearColorx(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), bgColor.getAlpha());
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		SkeletonFrame frame = null;
		synchronized(skeletonFrames) {
			frame = skeletonFrames[skeletonFrameIndex];
			skeletonFrames[skeletonFrameIndex] = null;
			skeletonFrameIndex = (skeletonFrameIndex + 1) % skeletonFrames.length;
		}
		if (null == frame) {
			if (null != previousFrame) {
				frame = previousFrame;
			} else {
				return;
			}
		}
		
		gl.glPointSizex(pointSize.getValue());
		gl.glLineWidthx(lineWidth.getValue());
		
		final SkeletonData[] skeletonData = frame.getSkeletons();
		for(SkeletonData data : skeletonData) {
			if (data.getTrackingState() != SkeletonTrackingState.Tracked) {
				continue;
			}
			final JointCollection jointCollection = data.getJoints();
			drawBones(gl, jointCollection);
			drawJoints(gl, jointCollection);
		}
		
		if (frame != previousFrame) {
			++dataFrameCount;
		}
		++frameCount;
		
		final long t = SystemClock.elapsedRealtime();
		if ((t - startTime) >= 1000) {
			synchronized (skeletonFrames) {
				final int c = dataFrameCount;
				KinectServerActivity.getHandler().post(new Runnable() {
					public void run() {
						fpsView.setText("FPS = " + frameCount + "\r\nDFPS = " + c);
					}
				});
			}
			//Log.i(KinectServer.LOG_TAG, "FPS = " + frameCount + " SPAN = " + (t - startTime));
			frameCount = 0;
			dataFrameCount = 0;
			startTime = SystemClock.elapsedRealtime();
		}
		
		previousFrame = frame;
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		//gl.glOrthof(0.0f, 1.0f, 1.0f, 0.0f, 2.0f, 0.1f);
		//GLU.gluLookAt(gl, 0.0f, 0.0f, 2.0f, 0.0f, 0.0f, 0.5f, 0.0f, 1.0f, 0.0f);
		gl.glFrustumf(-1.0f, 1.0f, 1.0f, -1.0f, Z_MAX, 0.1f);
		GLU.gluLookAt(gl, 0.5f, 0.5f, Z_MAX, 0.5f, 0.5f, 0.1f, 0.0f, 1.0f, 0.0f);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
		startTime = SystemClock.elapsedRealtime();
		frameCount = 0;
		dataFrameCount = 0;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	}

	public void setBackgroundColor(float red, float green, float blue) {
		bgColor.setRGBA(red, green, blue, 1.0f);
	}
	
	public void setBackgroundColor(ColorX color) {
		bgColor = color;
	}
	
	public void setSkeletonFrame(SkeletonFrame frame) {
		synchronized(skeletonFrames) {
			skeletonFrames[skeletonFrameIndex] = frame;
			++dataFrameCount;
		}
	}
	
	private static IntBuffer makeIntBuffer(int count) {
		final ByteBuffer byteBuffer = ByteBuffer.allocateDirect((Integer.SIZE >> 3) * count);
		byteBuffer.order(ByteOrder.nativeOrder());
		return byteBuffer.asIntBuffer();
	}
	
	private void drawJoints(GL10 gl, final JointCollection joints) {
		final int jointCount = joints.getCount();
		final IntBuffer buffer = pointBuffer;
		buffer.position(0);
		final int[] jointPos = pointArray;
		for(Joint joint : joints) {
			final Vector position = joint.getPosition();
			jointPos[0] = FixedPoint.toInt(position.getX());
			jointPos[1] = FixedPoint.toInt(position.getY());
			jointPos[2] = FixedPoint.toInt(Z_MAX - position.getZ());
			buffer.put(jointPos);
		}
		drawPoints(gl, jointCount, buffer, jointColorBuffer);
	}
	
	private void drawLines(GL10 gl, Joint... args) {
		final int jointCount = args.length;
		final IntBuffer buffer = lineBuffer;
		buffer.position(0);
		final int[] jointPos = lineArray;
		for(Joint joint : args) {
			final Vector position = joint.getPosition();
			jointPos[0] = FixedPoint.toInt(position.getX());
			jointPos[1] = FixedPoint.toInt(position.getY());
			jointPos[2] = FixedPoint.toInt(Z_MAX - position.getZ());
			buffer.put(jointPos);
		}
		drawLineStrip(gl, jointCount, buffer, boneColorBuffer);
	}
	
	private void drawBones(GL10 gl, JointCollection joints) {
		drawLines(gl, joints.get(JointID.HipCenter), joints.get(JointID.Spine), joints.get(JointID.ShoulderCenter), joints.get(JointID.Head));
		drawLines(gl, joints.get(JointID.ShoulderCenter), joints.get(JointID.ShoulderLeft), joints.get(JointID.ElbowLeft), joints.get(JointID.WristLeft), joints.get(JointID.HandLeft));
		drawLines(gl, joints.get(JointID.ShoulderCenter), joints.get(JointID.ShoulderRight), joints.get(JointID.ElbowRight), joints.get(JointID.WristRight), joints.get(JointID.HandRight));
		drawLines(gl, joints.get(JointID.HipCenter), joints.get(JointID.HipLeft), joints.get(JointID.KneeLeft), joints.get(JointID.AnkleLeft), joints.get(JointID.FootLeft));
		drawLines(gl, joints.get(JointID.HipCenter), joints.get(JointID.HipRight), joints.get(JointID.KneeRight), joints.get(JointID.AnkleRight), joints.get(JointID.FootRight));
	}
	
	private void drawPoints(GL10 gl, int count, final IntBuffer points, final IntBuffer colors) {
		points.position(0);
		colors.position(0);
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, points);
		gl.glColorPointer(ColorF.getFloatBufferSize(), GL10.GL_FIXED, 0, colors);
		gl.glDrawArrays(GL10.GL_POINTS, 0, count);
	}
	
	private void drawLineStrip(GL10 gl, int count, final IntBuffer points, final IntBuffer colors) {
		points.position(0);
		colors.position(0);
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, points);
		gl.glColorPointer(ColorF.getFloatBufferSize(), GL10.GL_FIXED, 0, colors);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, count);
	}
	
	/**
	 * @summary Floating pointer color format.
	 */
	public static class ColorF {
		private float red;
		private float green;
		private float blue;
		private float alpha;
		
		public ColorF() {
			this(0.0f, 0.0f, 0.0f, 1.0f);
		}
		
		public ColorF(float red, float green, float blue, float alpha) {
			setRGBA(red, green, blue, alpha);
		}
		
		public ColorF(int color) {
			setRGBA(color);
		}
		
		public int toInt() {
			int red = (int)(this.red * 255.0f);
			int green = (int)(this.green * 255.0f);
			int blue = (int)(this.blue * 255.0f);
			int alpha = (int)(this.alpha * 255.0f);
			return Color.argb(alpha, red, green, blue);
		}
		
		public void setRGBA(float red, float green, float blue, float alpha) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}
		
		public void setRGBA(int color) {
			this.red = (float)Color.red(color) / 255.0f;
			this.green = (float)Color.green(color) / 255.0f;
			this.blue = (float)Color.blue(color) / 255.0f;
			this.alpha = (float)Color.alpha(color) / 255.0f;
		}
		
		public void putFloatBuffer(FloatBuffer buffer) {
			buffer.put(red);
			buffer.put(green);
			buffer.put(blue);
			buffer.put(alpha);
		}
		
		public static int getFloatBufferSize() {
			return 4; // RGBA : 4 elements.
		}

		public float getRed() {
			return red;
		}

		public void setRed(float red) {
			this.red = red;
		}

		public float getGreen() {
			return green;
		}

		public void setGreen(float green) {
			this.green = green;
		}

		public float getBlue() {
			return blue;
		}

		public void setBlue(float blue) {
			this.blue = blue;
		}

		public float getAlpha() {
			return alpha;
		}

		public void setAlpha(float alpha) {
			this.alpha = alpha;
		}
	}
	
	/**
	 * @summary Fixed pointer(Q16) color format.
	 */
	public static class ColorX {
		private int red;
		private int green;
		private int blue;
		private int alpha;
		
		public ColorX() {
			this(0, 0, 0, 0x10000);
		}
		
		public ColorX(int red, int green, int blue, int alpha) {
			// store fixed pointer color elements value.
			setRGBA(red, green, blue, alpha);
		}
		
		public ColorX(float red, float green, float blue, float alpha) {
			// store floating pointer color elements value.
			setRGBA(red, green, blue, alpha);
		}
		
		public ColorX(int color) {
			// convert 32bit RGBA color to fixed pointer color format.
			setRGBA(color);
		}
		
		public ColorX(ColorF color) {
			setRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		}
		
		public int toInt() {
			int red   = (this.red   * 255) >> 16;
			int green = (this.green * 255) >> 16;
			int blue  = (this.blue  * 255) >> 16;
			int alpha = (this.alpha * 255) >> 16;
			return Color.argb(alpha, red, green, blue);
		}
		
		public void setRGBA(int red, int green, int blue, int alpha) {
			this.red   = red;
			this.green = green;
			this.blue  = blue;
			this.alpha = alpha;
		}
		
		public void setRGBA(float red, float green, float blue, float alpha) {
			this.red   = (int)(red * 65535.0f);
			this.green = (int)(green * 65535.0f);
			this.blue  = (int)(blue * 65535.0f);
			this.alpha = (int)(alpha * 65535.0f);
		}
		
		public void setRGBA(int color) {
			this.red   = (Color.red(color)   << 16) / 255;
			this.green = (Color.green(color) << 16) / 255;
			this.blue  = (Color.blue(color)  << 16) / 255;
			this.alpha = (Color.alpha(color) << 16) / 255;
		}
		
		public void putIntBuffer(IntBuffer buffer) {
			buffer.put(red);
			buffer.put(green);
			buffer.put(blue);
			buffer.put(alpha);
		}
		
		public static int getIntBufferSize() {
			return 4; // RGBA : 4 elements.
		}

		public int getRed() {
			return red;
		}

		public void setRed(int red) {
			this.red = red;
		}

		public int getGreen() {
			return green;
		}

		public void setGreen(int green) {
			this.green = green;
		}

		public int getBlue() {
			return blue;
		}

		public void setBlue(int blue) {
			this.blue = blue;
		}

		public int getAlpha() {
			return alpha;
		}

		public void setAlpha(int alpha) {
			this.alpha = alpha;
		}
	}
	
	private static class FixedPoint {
		private int value;
		
		public FixedPoint(float value) {
			this.value = toInt(value); 
		}
		
		public int getValue() {
			return value;
		}
		
		public static int toInt(float value) {
			return (int)(value * 65535.0f);
		}
		
		public boolean equals(FixedPoint fp) {
			return value == fp.value;
		}

		@Override
		public boolean equals(Object o) {
			return equals((FixedPoint)o);
		}

		@Override
		public int hashCode() {
			return value;
		}
	}
}
