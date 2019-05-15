package nearshore.infolayers.backend.documents;

import java.util.ArrayList;

public class MultiPolygon {
	private String type;
	private ArrayList<ArrayList<Object>> coordinates;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<ArrayList<Object>> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(ArrayList<ArrayList<Object>> coordinates) {
		this.coordinates = coordinates;
	}
	@Override
	public String toString() {
		return "MultiPolygon [type=" + type + ", coordinates=" + coordinates + "]";
	}
	
	
}