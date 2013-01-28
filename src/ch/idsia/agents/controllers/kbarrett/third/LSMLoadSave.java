package ch.idsia.agents.controllers.kbarrett.third;

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

import ch.idsia.agents.controllers.kbarrett.Evolver;

public class LSMLoadSave
{
	public static boolean saving = true;

	public static void saveToFile(String filename,
			ArrayList<LevelSceneMovement> list,
			Evolver<LevelSceneMovement> evolver) throws IOException
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
			if(oldFile.exists())
			{
				while(!oldFile.delete()){};
			}
			while(!newFile.renameTo(oldFile)){};
			
			System.out.println("The current population (" + list.size() + ") has been saved successfully at " + new Date().toString() + ".");
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			throw e;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			saving = false;
		}
	}

	public static void loadFromFile(String filename,
			ArrayList<LevelSceneMovement> list,
			Evolver<LevelSceneMovement> evolver) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
		int size = ois.readInt();
		while(size-- > 0)
		{
			list.add((LevelSceneMovement)ois.readObject());
		}
		ois.close();
		
	}

}
