package ch.idsia.agents.controllers.kbarrett;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


/**
 * Loads a population from an xml representation and saves an xml representation of a population into a file. 
 * @author Kim Barrett
 */
public class LoadSave
{
	/**The name given to the root element of the xml tree.*/
	private static final String RootElementName = "Root";
	/**Whether the {@link #saveToFile(String, Collection, Evolver)} method is currently executing.*/
	public static boolean saving;
	/**
	 * Loads the xml representation of a population into the given list.
	 * @param filename - the file in which the xml representation is saved.
	 * @param list - the collection into which the population should be stored.
	 * @param evolver - the Evolver containing the instructions for how to create an object from the xml.
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static <T> void loadFromFile(String filename, Collection<T> list, Evolver<T> evolver) throws JDOMException, IOException
	{	
		Document doc = new SAXBuilder().build(new File(filename));
		Element root = doc.getRootElement();
		for(Object o : root.getChildren())
		{
			Element element = (Element)o;
			T agent = evolver.fromSaveFormat(element);
			list.add(agent);
		}
	}
	/**
	 * Saves a population into the given file in xml format.
	 * @param filename - the file into which the population should be saved. 
	 * @param list - the list containing the population to be saved.
	 * @param evolver - the Evolver containing the instruction for how to create an xml tree for a given object.
	 * @throws IOException
	 */
	public static <T> void saveToFile(String filename, Collection<T> list, Evolver<T> evolver) throws IOException
	{
		saving = true;
		
		PrintStream saveStream = new PrintStream(filename+".part");
		Element root = new Element(RootElementName);
		for(T element : list)
		{
			root.addContent(evolver.toSaveFormat(element));
		}
		
		Document doc = new Document();
		doc.setRootElement(root);
		StringWriter writer = new StringWriter();
		XMLOutputter outputter = new XMLOutputter();
		outputter.output(doc, writer);
		saveStream.print(writer.toString());
		saveStream.close();
		
		File newFile = new File(filename+".part");
		File oldFile = new File(filename);
		while(oldFile.exists() && !oldFile.delete()){};
		while(!newFile.renameTo(oldFile)){};
		
		System.out.println("The current population (" + list.size() + ") has been saved successfully at " + new Date().toString() + ".");
		
		saving = false;
	}

}
