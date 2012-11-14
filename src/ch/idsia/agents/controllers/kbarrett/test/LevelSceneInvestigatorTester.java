package ch.idsia.agents.controllers.kbarrett.test;

import java.util.ArrayList;

import ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator;
import ch.idsia.agents.controllers.kbarrett.MapSquare;
import ch.idsia.agents.controllers.kbarrett.MapUpdater;
import ch.idsia.agents.controllers.kbarrett.Movement;

public class LevelSceneInvestigatorTester {
	
	static LevelSceneInvestigator levelSceneInvestigator;
	static int[] marioLoc = {5,3};
	static float[] marioScreenPos = {0,0};
	static Movement movement;
	static byte[][] levelScene = {
				{0, 	0,	 0,	  0,   0, 	0,	 0},
				{0, 	0,	 0,	  0,   0, 	0,	 0},
				{0, 	0,	 0,	  0,   2, 	0,	 0},
				{0, 	0,	 0,/**/0, -60, 	0, 	 0},
				{0, 	0,	 0,	  0, -60,	0, 	 0},
				{0, 	0,	 0,	  0, -60, 	0, 	 0},
				{-60, -60, -60, -60, -60, -60, -60}};
	
	public static void main(String[] args)
	{
		/*while(true)
		{
		levelSceneInvestigator = new LevelSceneInvestigator();
		movement = new Movement();
		
		levelSceneInvestigator.giveMapSize(7, 7);
		levelSceneInvestigator.setMarioLoc(marioLoc, movement);
		
		//Give LevelSceneInvestigator the new LevelScene & Mario's new screen position
			levelSceneInvestigator.updateMapFromLevelScene(levelScene);
			levelSceneInvestigator.setMarioScreenPos(marioScreenPos);
			
			if(movement.isJumping() && true)
			{
				movement.land();
			}
			//Decide next movement and pass this to Movement to be acted upon
			else
			{
				movement.moveTowards(
						levelSceneInvestigator.getNextLocation(movement.isFacingRight(), movement.isJumping()));
			}
			
		movement.reset();
		}*/
		
		ArrayList<ArrayList<MapSquare>> map = new ArrayList<ArrayList<MapSquare>>();
		map.set(0, new ArrayList<MapSquare>()).set(0,new MapSquare((byte) 0, null, 0, 0));
		byte[][] levelScene = {{1,0,0},{0,1,1},{0,0,1}};
		byte[][] levelScene2 = {{2,0,0},{0,2,0},{0,0,2}};
		int[] marioMapLoc = {0,0};
		
		for(int i = 0; i<5; ++i)
		{
			map = (i%2==0 ? MapUpdater.updateMap(map, levelScene, marioMapLoc) : MapUpdater.updateMap(map, levelScene2, marioMapLoc));
			printMap(map);
			System.out.println("---------");
			if(i%3==0){marioMapLoc[1]++;}
			if(i%2 == 0) {marioMapLoc[0]++;}
		}
	}
	
	private static void printMap(ArrayList<ArrayList<MapSquare>> map)
	{
		for(int i = 0; i<map.size(); ++i)
		{
			for(int j = 0; j<map.get(i).size(); ++j)
			{
				String s = "";
				if(map.get(i).get(j) == null) {s += ""+map.get(i).get(j);}
				else {s += ""+map.get(i).get(j).getEncoding();}
				while(s.length()<4)
				{
					s+=" ";
				}
				System.out.print(s);
			}
			System.out.println();
		}
	}

}
