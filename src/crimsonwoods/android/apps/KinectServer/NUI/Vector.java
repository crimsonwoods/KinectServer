package crimsonwoods.android.apps.KinectServer.NUI;

public class Vector {
	private float x;
	private float y;
	private float z;
	private float w;
	
	public Vector() {
	}
	
	public Vector(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("X = ").append(x).append(", Y = ").append(y).append(", Z = ").append(z).append(", W = ").append(w);
		return builder.toString();
	}
}
