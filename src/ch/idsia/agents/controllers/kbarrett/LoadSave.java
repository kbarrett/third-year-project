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

public class LoadSave
{
	private static final String RootElementName = "Root";
	
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
		PrintStream saveStream = new PrintStream(filename);
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
		
		System.out.println("The current population (" + list.size() + ") has been saved successfully at " + new Date().toString() + ".");
	}

}
