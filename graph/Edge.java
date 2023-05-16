package graph;
import list.*;
//an object used to store data related to edges
public class Edge {
	private Object vertex1;
	private Object vertex2;
	private int weight;
	private DListNode twinNode;
	
	public Edge(Object vertex1, Object vertex2, int weight)
	{
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.weight = weight;
		this.twinNode = null;
	}
	
	public Object[] getVertices()
	{
		Object[] retObject = {vertex1, vertex2};
		return retObject;
	}
	
	public int getWeight()
	{
		return weight;
	}
	
	public void changeWeight(int newWeight)
	{
		this.weight = newWeight;
	}
	
	public void updateTwinNode(DListNode twin)
	{
		this.twinNode = twin;
	}
	
	public DListNode getTwinNode()
	{
		return twinNode;
	}
}
