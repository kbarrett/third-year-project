package ch.idsia.agents.controllers.kbarrett.third;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

import ch.idsia.agents.controllers.kbarrett.Evolver;
/**
 * Saves and loads a population of {@link LevelSceneMovement}s.
 * @author Kim Barrett
 */
public class LSMLoadSave
{
	/**
	 * Whether a save action is currently occurring.
	 */
	public static boolean saving = false;
	/**
	 * Save the given list into the given file.
	 * @param filename - the name of the file to use
	 * @param list - the list to save
	 * @throws IOException - if an error occurs while saving
	 */
	public static void saveToFile(String filename,
			ArrayList<LevelSceneMovement> list) throws IOException
	{
		//The save function has begun, so update the global variable
		saving = true;
		try
		{
			//Create the output stream to a file with ending ".part"
			FileOutputStream saveStream = new FileOutputStream(filename + ".part");
			ObjectOutputStream oos = new ObjectOutputStream(saveStream);
			//Write the size of the list to the file, to enable the list to be loaded more easily
			oos.writeInt(list.size());
			//Write the list to the file
			for(LevelSceneMovement lsm : list)
			{
				oos.writeObject(lsm);
			}
			//Close the stream
			oos.close();
			//Get the file that has just been written
			File newFile = new File(filename+".part");
			//This file contains previously saved population
			File oldFile = new File(filename);
			//Delete the old file
			if(oldFile.exists())
			{
				while(!oldFile.delete()){};
			}
			//Rename the newly created file to not have the ".part" at the end
			while(!newFile.renameTo(oldFile)){};
			//Inform the user that the save function has ended
			System.out.println("The current population (" + list.size() + ") has been saved successfully at " + new Date().toString() + ".");
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			//Update that the saving function has ended.
			saving = false;
		}
	}
	/**
	 * Load the list stored in the file into the given list
	 * @param filename - the name of the file to use
	 * @param list - the list into which the population should be put
	 * @throws IOException - if an error occurs when loading the file
	 * @throws ClassNotFoundException - if an error occurs while reading the list
	 */
	public static void loadFromFile(String filename,
			ArrayList<LevelSceneMovement> list) throws IOException, ClassNotFoundException
	{
		//Open the stream
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
		//Get the size of the population
		int size = ois.readInt();
		//Get each element of the population from the file
		while(size-- > 0)
		{
			//Add the elements to the list
			list.add((LevelSceneMovement)ois.readObject());
		}
		//Close the stream
		ois.close();
	}
}
