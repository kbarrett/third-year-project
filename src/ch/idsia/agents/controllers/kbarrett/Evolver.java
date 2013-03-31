package ch.idsia.agents.controllers.kbarrett;

import java.util.List;

import org.jdom.Element;

/**
 * Defines the methods used by a {@link GeneticAlgorithm}.
 * @author Kim Barrett
 *
 * @param <T> the type that is being evolved.
 */
public interface Evolver<T>
{
	/**
	 * Assesses the object for how "good" a solution it is for the given problem.
	 * @param element - the object to be assessed
	 * @return int representing how "good" the object is.
	 */
	int fitnessFunction(T element);
	/**
	 * Creates two children elements from the given parents.
	 * @param element1 - parent element
	 * @param element2 - parent element
	 * @return List<T> of length 2 containing the children.
	 */
	public List<T> crossover(T element1, T element2);
	/**
	 * Mutates the given element.
	 * Note: does not need to include choosing whether or not to mutate this element.
	 * @param element - object to be mutated.
	 */
	public void mutate(T element);
	/**
	 * @return float representing how frequently objects will be mutated.
	 */
	public float getProbabilityOfMutation();
	/**
	 * @return int representing the size of each generation.
	 */
	public int getSizeOfGeneration();
	/**
	 * Creates the given object into an xml format for saving.
	 * @param element - the object for conversion.
	 * @return Element containing the correct xml format.
	 */
	public Element toSaveFormat(T element);
	/**
	 * Creates an object from given xml.
	 * @param element - xml from which the object should be created.
	 * @return object containing the given xml data.
	 */
	public T fromSaveFormat(Element element);
	
}
