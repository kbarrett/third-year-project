package ch.idsia.agents.controllers.kbarrett;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ActionsIndex
{
	// Note: left, right, down, jump, speed, up
	public static boolean[] ai0 = {false, false, false, false, false, false};
	public static boolean[] ai1 = {false, false, true, false, false, false};
	public static boolean[] ai2 = {false, false, false, true, false, false};
	public static boolean[] ai3 = {false, false, false, false, false, true};
	
	public static boolean[] ai4 = {true, false, false, false, false, false};
	public static boolean[] ai5 = {true, false, true, false, false, false};
	public static boolean[] ai6 = {true, false, false, true, false, false};
	public static boolean[] ai7 = {true, false, false, false, false, true};
	
	public static boolean[] ai8 = {false, true, false, false, false, false};
	public static boolean[] ai9 = {false, true, true, false, false, false};
	public static boolean[] ai10 = {false, true, false, true, false, false};
	public static boolean[] ai11 = {false, true, false, false, false, true};
	
	public static boolean[] ai12 = {false, false, false, false, true, false};
	public static boolean[] ai13 = {false, false, true, false, true, false};
	public static boolean[] ai14 = {false, false, false, true, true, false};
	public static boolean[] ai15 = {false, false, false, false, true, true};
	
	public static boolean[] ai16 = {true, false, false, false, true, false};
	public static boolean[] ai17 = {true, false, true, false, true, false};
	public static boolean[] ai18 = {true, false, false, true, true, false};
	public static boolean[] ai19 = {true, false, false, false, true, true};
	
	public static boolean[] ai20 = {false, true, false, false, true, false};
	public static boolean[] ai21 = {false, true, true, false, true, false};
	public static boolean[] ai22 = {false, true, false, true, true, false};
	public static boolean[] ai23 = {false, true, false, false, true, true};
	
	public static final int NumberOfArrayPossibilities = 24;
	
	public static int getMatch(boolean[] array)
	{
		for(int i = 0; i < NumberOfArrayPossibilities; ++i)
		{
			try
			{
				Field f = ActionsIndex.class.getField("ai" + i);
				boolean[] o = (boolean[])f.get(null);
				if(Arrays.equals(array,o))
				{
					return i;
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return -1;
	}
	
	public static boolean[] getArray(int number)
	{
		try
		{
			return (boolean[]) ActionsIndex.class.getField("ai" + number).get(null);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}

}
