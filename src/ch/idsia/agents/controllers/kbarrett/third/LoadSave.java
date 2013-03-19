package ch.idsia.agents.controllers.kbarrett.third;

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

import ch.idsia.agents.controllers.kbarrett.Evolver;

/**
 * @deprecated
 * @author Kim
 *
 */
public class LoadSave
{
	private static final String RootElementName = "Root";
	public static boolean saving;
	
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
		while(!oldFile.delete()){};
		while(!newFile.renameTo(oldFile)){};
		
		System.out.println("The current population (" + list.size() + ") has been saved successfully at " + new Date().toString() + ".");
		
		saving = false;
	}

}
