package ch.idsia.agents.controllers.kbarrett.test;

import java.util.ArrayList;
import java.util.Stack;

import org.objectweb.asm.tree.IntInsnNode;

import ch.idsia.agents.controllers.kbarrett.first.MapSquare;
import ch.idsia.agents.controllers.kbarrett.first.MapUpdater;
import ch.idsia.agents.controllers.kbarrett.first.PlanStorer;

public class ReplanTester {
	
	static byte[][] levelScene = {
									{0,0,0,0,0},
									{0,0,0,0,0},
									{0,0,0,0,0},
									{0,0,0,0,0},
									{0,0,-60,0,-60},
									{0,0,-60,0,-60},
									{0,0,0,0,-60},
									{-60,-60,-60,-60,-60},
									{-60,-60,-60,-60,-60}};
	static ArrayList<ArrayList<MapSquare>> map = new ArrayList<ArrayList<MapSquare>>();
	
	static int[][] points =      {{6,2},{6,3},{5,3},{4,3},{3,3},{3,2}};
	static int[][] initialPlan = {{6,2},{6,3},{5,3},{4,3},{3,3},{3,2}};
	
	static int[] marioMapLoc = {6,1};
	
	public static void main(String[] args)
	{
		map.add(new ArrayList<MapSquare>());
		MapUpdater.updateMap(map, levelScene, new int[]{4,2});
		
		PlanStorer planStorer = new PlanStorer();
		planStorer.plan = new Stack<MapSquare>();
		for(int i = initialPlan.length - 1; i >= 0; --i)
		{
			planStorer.plan.add(map.get(initialPlan[i][0]).get(initialPlan[i][1]));
		}
		
		for(int i = 0; i<points.length; ++i)
		{
			marioMapLoc = points[i];
			planStorer.replan(map.get(marioMapLoc[0]).get(marioMapLoc[1]), 2);
			System.out.println("On iteration " + i + " when Mario is at" + marioMapLoc[0] + "," + marioMapLoc[1] + " the plan is: " + planStorer.plan);
			System.out.println("Location to move to: " + planStorer.getLocationToMoveTo(map.get(marioMapLoc[0]).get(marioMapLoc[1]), null));
		}
	}

}
