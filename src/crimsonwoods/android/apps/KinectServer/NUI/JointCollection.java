package crimsonwoods.android.apps.KinectServer.NUI;

import java.util.HashMap;
import java.util.Iterator;

public class JointCollection implements Iterable<Joint> {
	private HashMap<Joint.JointID, Joint> jointMap;

	public JointCollection() {
		jointMap = new HashMap<Joint.JointID, Joint>();
	}
	
	public Iterator<Joint> iterator() {
		return jointMap.values().iterator();
	}
	
	public void set(Joint value) {
		if (jointMap.containsKey(value.getJointId())) {
			jointMap.remove(value.getJointId());
		}
		jointMap.put(value.getJointId(), value);
	}
	
	public void clear() {
		jointMap.clear();
	}
	
	public Joint get(Joint.JointID key) {
		return jointMap.get(key);
	}
	
	public int getCount() {
		return jointMap.values().size();
	}
}
