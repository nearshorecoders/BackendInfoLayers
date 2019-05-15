package nearshore.infolayers.backend.documents;

import java.util.ArrayList;

public class Geometry {
	private String type;
	private ArrayList<Object> coordinates;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<Object> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(ArrayList<Object> coordinates) {
		this.coordinates = coordinates;
	}
	
}