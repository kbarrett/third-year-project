package ch.idsia.agents.controllers.kbarrett;

import java.util.List;

import org.jdom.Element;

public interface Evolver<T>
{
	
	public int fitnessFunction(T element);
	public List<T> crossover(T element1, T element2);
	public void mutate(T element);
	public float getProbabilityOfMutation();
	public int getSizeOfGeneration();
	
	public Element toSaveFormat(T element);
	public T fromSaveFormat(Element element);
	
}
