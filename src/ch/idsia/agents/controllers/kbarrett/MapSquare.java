package ch.idsia.agents.controllers.kbarrett;

import java.util.ArrayList;

public class MapSquare {

	private int encoding;
	private ArrayList<MapSquare> reachableSquares;
	
	public MapSquare(int encoding)
	{
		this.encoding = encoding;
	}
	
	public int getEncoding() {
		return encoding;
	}

	public void setEncoding(int encoding) {
		this.encoding = encoding;
	}
	
	public boolean isReachable(MapSquare square)
	{
		return reachableSquares.contains(square);
	}
	
	public String toString()
	{
		return ""+encoding;
	}
}
