package ch.idsia.agents.controllers.kbarrett;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;

public class LSMLoadSave
{
	public static boolean saving = true;

	public static void saveToFile(String filename,
			ArrayList<LevelSceneMovement> list,
			Evolver<LevelSceneMovement> evolver)
	{
		saving = true;
		try
		{
			FileOutputStream saveStream = new FileOutputStream(filename + ".part");
			ObjectOutputStream oos = new ObjectOutputStream(saveStream);
			
			oos.writeInt(list.size());
			
			for(LevelSceneMovement lsm : list)
			{
				oos.writeObject(lsm);
			}

			oos.close();
			
			File newFile = new File(filename+".part");
			File oldFile = new File(filename);
			while(!oldFile.delete()){};
			while(!newFile.renameTo(oldFile)){};
			
			System.out.println("The current population (" + list.size() + ") has been saved successfully at " + new Date().toString() + ".");
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			saving = false;
		}
	}

	public static void loadFromFile(String filename,
			ArrayList<LevelSceneMovement> list,
			Evolver<LevelSceneMovement> evolver)
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
			int size = ois.readInt();
			while(size-- > 0)
			{
				list.add((LevelSceneMovement)ois.readObject());
			}
			ois.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		
	}

}
