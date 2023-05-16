/* WUGraph.java */

package graph;

/**
 * The WUGraph class represents a weighted, undirected graph.  Self-edges are
 * permitted.
 */
import list.*;
import dict.*;

public class WUGraph {
	
	//maps a vertex to that vertex's node in the list
	private HashTableChained vertexHash;
	//maps an edge to that edge's nodes in the adjacency lists
	private HashTableChained edgeHash;
	//maps a vertex's node in the list to its adjacency list
	private HashTableChained vertexToAdjacencyList;
	//maintains a list of all vertices
	private DList vertexList;
	
  /**
   * WUGraph() constructs a graph having no vertices or edges.
   *
   * Running time:  O(1).
   */
  public WUGraph()
  {
	  //initializes all hashtables and lists to empty
	  vertexHash = new HashTableChained();
	  edgeHash = new HashTableChained();
	  vertexToAdjacencyList = new HashTableChained();
	  vertexList = new DList();
  }

  /**
   * vertexCount() returns the number of vertices in the graph.
   *
   * Running time:  O(1).
   */ 
  public int vertexCount()
  {
	  return vertexList.length();
  }

  /**
   * edgeCount() returns the total number of edges in the graph.
   *
   * Running time:  O(1).
   */
  public int edgeCount()
  {
	  return edgeHash.size();
  }

  /**
   * getVertices() returns an array containing all the objects that serve
   * as vertices of the graph.  The array's length is exactly equal to the
   * number of vertices.  If the graph has no vertices, the array has length
   * zero.
   *
   * (NOTE:  Do not return any internal data structure you use to represent
   * vertices!  Return only the same objects that were provided by the
   * calling application in calls to addVertex().)
   *
   * Running time:  O(|V|).
   */
  public Object[] getVertices()
  {
	  //array to be returned
	  Object[] retArray = new Object[vertexList.length()];
	  //checks whether the array should be empty
	  if(vertexList.length() != 0)
	  {
		  DListNode current = vertexList.front();
		  retArray[0] = current.item;
		  //the for loop runs |V| times, so the runtime is O(|V|)
		  for(int i = 1; i < vertexList.length(); i++)
		  {
			  current = vertexList.next(current);
			  retArray[i] = current.item;
		  }
	  }
	  return retArray;
  }

  /**
   * addVertex() adds a vertex (with no incident edges) to the graph.
   * The vertex's "name" is the object provided as the parameter "vertex".
   * If this object is already a vertex of the graph, the graph is unchanged.
   *
   * Running time:  O(1).
   */
  public void addVertex(Object vertex)
  {
	  //checks whether the vertex already exists
	  if(!isVertex(vertex))
	  {
		  //adds the vertex to the list
		  vertexList.insertBack(vertex);
		  //adds the vertex to the hash, vertex -> vertex's node in the list
		  vertexHash.insert(vertex, vertexList.back());
		  //creates a pathway from the vertex's node in the list to a new adjacency list
		  vertexToAdjacencyList.insert(vertexList.back(), new DList());
	  }
  }
  
  /**
   * removeVertex() removes a vertex from the graph.  All edges incident on the
   * deleted vertex are removed as well.  If the parameter "vertex" does not
   * represent a vertex of the graph, the graph is unchanged.
   *
   * Running time:  O(d), where d is the degree of "vertex".
   */
  public void removeVertex(Object vertex)
  {
	  //checks whether the vertex even exists
	  if(isVertex(vertex))
	  {
		  //defines the adjacency list
		  DList adjacencyList = (DList)(vertexToAdjacencyList.find(vertexHash.find(vertex).value()).value());
		  //checks to see if the vertex has any edges
		  if(!adjacencyList.isEmpty())
		  {
			  //creates 2 arrays, one full of vertex1's of all edges, and one full of vertex2's of all edges which will later be used to delete all edges
			  Object[] vertex1 = new Object[adjacencyList.length()];
			  Object[] vertex2 = new Object[adjacencyList.length()];
			  DListNode currentNode = adjacencyList.front();
			  vertex1[0] = ((Edge)(currentNode.item)).getVertices()[0];
			  vertex2[0] = ((Edge)(currentNode.item)).getVertices()[1];
			  //note that the length of this list should be equal to the vertex's degree, so the following traversal has a time complexity of O(d)
			  for(int i = 1; i < adjacencyList.length(); i++)
			  {
				  currentNode = adjacencyList.next(currentNode);
				  vertex1[i] = ((Edge)(currentNode.item)).getVertices()[0];
				  vertex2[i] = ((Edge)(currentNode.item)).getVertices()[1];
			  }
			  //the following traversal is also O(d), and O(2d) = O(d)
			  for(int i = 0; i < vertex1.length; i++)
			  {
				  //removes the edges
				  removeEdge(vertex1[i],vertex2[i]);
				  //i decrements to account for the removal
			  }
		  }
		  //after having removed all edges, remove the vertex from the hashtables and list
		  //remove and find are both O(1) operations
		  vertexList.remove((DListNode)(vertexHash.find(vertex).value()));
		  vertexToAdjacencyList.remove(vertexHash.remove(vertex).value());
	  }
  }

  /**
   * isVertex() returns true if the parameter "vertex" represents a vertex of
   * the graph.
   *
   * Running time:  O(1).
   */
  public boolean isVertex(Object vertex)
  {
	  boolean result = false;
	  //uses hashtable's find(), which should be O(1).
	  if(vertexHash.find(vertex) != null)
	  {
		  result = true;
	  }
	  return result;
  }
  /**
   * degree() returns the degree of a vertex.  Self-edges add only one to the
   * degree of a vertex.  If the parameter "vertex" doesn't represent a vertex
   * of the graph, zero is returned.
   *
   * Running time:  O(1).
   */
  public int degree(Object vertex)
  {
	  //checks whether the vertex even exists
	  if(isVertex(vertex))
	  {
		  //vertex's value in vertexHash returns the list's node for the vertex, whose value in vertexToAdjacencyList returns the vertex's adjacency list, whose length should be the degree
		  //all find operations should be O(1), so the whole method should have O(1) runtime
		  return ((DList)(vertexToAdjacencyList.find(vertexHash.find(vertex).value()).value())).length();
	  }
	  else
	  {
		  return 0;
	  }
  }

  /**
   * getNeighbors() returns a new Neighbors object referencing two arrays.  The
   * Neighbors.neighborList array contains each object that is connected to the
   * input object by an edge.  The Neighbors.weightList array contains the
   * weights of the corresponding edges.  The length of both arrays is equal to
   * the number of edges incident on the input vertex.  If the vertex has
   * degree zero, or if the parameter "vertex" does not represent a vertex of
   * the graph, null is returned (instead of a Neighbors object).
   *
   * The returned Neighbors object, and the two arrays, are both newly created.
   * No previously existing Neighbors object or array is changed.
   *
   * (NOTE:  In the neighborList array, do not return any internal data
   * structure you use to represent vertices!  Return only the same objects
   * that were provided by the calling application in calls to addVertex().)
   *
   * Running time:  O(d), where d is the degree of "vertex".
   */
  public Neighbors getNeighbors(Object vertex)
  {
	  if(isVertex(vertex))
	  {
		  //we need two arrays for the neighbors class, an Object array for vertices and an int array for weights
		  Object[] vertices = new Object[degree(vertex)];
		  int[] weights = new int[degree(vertex)];
		  //the adjacency list, note again that all find operations should be O(1)
		  DList adjacencyList = ((DList)(vertexToAdjacencyList.find(vertexHash.find(vertex).value()).value()));
		  //checks whether the list is empty
		  if(adjacencyList.isEmpty())
		  {
			  return null;
		  }
		  else
		  {
			  DListNode currentNode = adjacencyList.front();
			  //note that edge's vertices array always has a length of 2, so this traversal has a time complexity of O(1)
			  for(int i = 0; i < 2; i++)
			  {
				  //finds the vertex which is not the argument vertex
				  if(((Edge)(currentNode.item)).getVertices()[i] != vertex)
				  {
					  vertices[0] = ((Edge)(currentNode.item)).getVertices()[i];
				  }
			  }
			  //in case the edge is self-referential
			  if(vertices[0] == null)
			  {
				  vertices[0] = ((Edge)(currentNode.item)).getVertices()[0];
			  }
			  weights[0] = ((Edge)(currentNode.item)).getWeight();
			  //note that the length of this list should be equal to the vertex's degree, so the following traversal has a time complexity of O(d)
			  for(int i = 1; i < adjacencyList.length(); i++)
			  {
				  //repeat of the front node case
				  currentNode = adjacencyList.next(currentNode);
				  for(int j = 0; j < 2; j++)
				  {
					  if(!((Edge)(currentNode.item)).getVertices()[j].equals(vertex))
					  {
						  vertices[i] = ((Edge)(currentNode.item)).getVertices()[j];
					  }
				  }
				  if(vertices[i] == null)
				  {
					  vertices[i] = ((Edge)(currentNode.item)).getVertices()[0];
				  }
				  weights[i] = ((Edge)(currentNode.item)).getWeight();
			  }
			  //creates the neighbors object we will return and initializes it with our two arrays
			  Neighbors retNeighbors = new Neighbors();
			  retNeighbors.neighborList = vertices;
			  retNeighbors.weightList = weights;
			  return retNeighbors;
		  }
		  
	  }
	  else
	  {
		  return null;
	  }
  }
  /**
   * addEdge() adds an edge (u, v) to the graph.  If either of the parameters
   * u and v does not represent a vertex of the graph, the graph is unchanged.
   * The edge is assigned a weight of "weight".  If the graph already contains
   * edge (u, v), the weight is updated to reflect the new value.  Self-edges
   * (where u.equals(v)) are allowed.
   *
   * Running time:  O(1).
   */
  public void addEdge(Object u, Object v, int weight)
  {
	  //makes sure both vertices exist
	  if(isVertex(u) && isVertex(v))
	  {
		  //in case the edge is already in the map, in which case we update the weight
		  if(isEdge(u,v))
		  {
			  //defines theEdge as the Edge we're looking for
			  Edge theEdge = ((Edge)(((DListNode)(edgeHash.find(new VertexPair(u,v)).value())).item));
			  theEdge.changeWeight(weight);
			  //if the edge has a twin, we want to update that too
			  if(theEdge.getTwinNode() != null)
			  {
				  ((Edge)(theEdge.getTwinNode().item)).changeWeight(weight);
			  }
		  }
		  //the default case, which adds the edge for the first time
		  else
		  {
			  //adds the new edge to the first vertex's adjacency list
			  //all find operations should be O(1), so the whole method should have O(1) runtime
			  ((DList)(vertexToAdjacencyList.find(vertexHash.find(u).value()).value())).insertBack(new Edge(u,v,weight));
			  //creates a new variable that keeps track of the edge's node in the adjacency list
			  DListNode uEdgeNode = ((DList)(vertexToAdjacencyList.find(vertexHash.find(u).value()).value())).back();
			  //if u != v, then we need to add another "twin" edge to the adjacency list of the other vertex
			  if(!u.equals(v))
			  {
				  ((DList)(vertexToAdjacencyList.find(vertexHash.find(v).value()).value())).insertBack(new Edge(u,v,weight));
				  DListNode vEdgeNode = ((DList)(vertexToAdjacencyList.find(vertexHash.find(v).value()).value())).back();
				  //because there will be two twin edges in this case, we need to have each edge have a pointer to the other twin's node for quick deletion
				  ((Edge)(uEdgeNode.item)).updateTwinNode(vEdgeNode);
				  ((Edge)(vEdgeNode.item)).updateTwinNode(uEdgeNode);
			  }
			  //hashes the vertex pair to the edge's node
			  edgeHash.insert(new VertexPair(u,v), uEdgeNode);
		  }
	  }
  }
  /**
   * removeEdge() removes an edge (u, v) from the graph.  If either of the
   * parameters u and v does not represent a vertex of the graph, the graph
   * is unchanged.  If (u, v) is not an edge of the graph, the graph is
   * unchanged.
   *
   * Running time:  O(1).
   */
  public void removeEdge(Object u, Object v)
  {
	  //checks whether the vertices and edge are in place, all methods should be O(1)
	  if(isVertex(u) && isVertex(v) && isEdge(u,v))
	  {
		  //the node which contains the edge is defined
		  DListNode uEdgeNode = (DListNode)(edgeHash.remove(new VertexPair(u,v)).value());
		  //the adjacency list, note again that all find operations should be O(1)
		  DList uAdjacencyList = ((DList)(vertexToAdjacencyList.find(vertexHash.find(u).value()).value()));
		  //determines whether the edge has a "twin"
		  if(((Edge)(uEdgeNode.item)).getTwinNode() != null)
		  {
			  DListNode vEdgeNode = ((Edge)(uEdgeNode.item)).getTwinNode();
			  DList vAdjacencyList = ((DList)(vertexToAdjacencyList.find(vertexHash.find(v).value()).value()));
			  vAdjacencyList.remove(vEdgeNode);
		  }
		  uAdjacencyList.remove(uEdgeNode);
		  System.out.println(u + ", " + v);
	  }
  }

  /**
   * isEdge() returns true if (u, v) is an edge of the graph.  Returns false
   * if (u, v) is not an edge (including the case where either of the
   * parameters u and v does not represent a vertex of the graph).
   *
   * Running time:  O(1).
   */
  public boolean isEdge(Object u, Object v)
  {
	  boolean result = false;
	  //uses hashtable's find(), which should be O(1).
	  if(edgeHash.find(new VertexPair(u, v)) != null)
	  {
		  result = true;
	  }
	  return result;
  }

  /**
   * weight() returns the weight of (u, v).  Returns zero if (u, v) is not
   * an edge (including the case where either of the parameters u and v does
   * not represent a vertex of the graph).
   *
   * (NOTE:  A well-behaved application should try to avoid calling this
   * method for an edge that is not in the graph, and should certainly not
   * treat the result as if it actually represents an edge with weight zero.
   * However, some sort of default response is necessary for missing edges,
   * so we return zero.  An exception would be more appropriate, but also more
   * annoying.)
   *
   * Running time:  O(1).
   */
  public int weight(Object u, Object v)
  {
	  //we can only have this be true if both vertices and the edge exist, because we can't add edges without the vertices already existing, and removing vertices removes the edges tied to them
	  if(isEdge(u,v))
	  {
		  return ((Edge)((DListNode)(edgeHash.find(new VertexPair(u,v)).value())).item).getWeight();
	  }
	  else
	  {
		  return 0;
	  }
  }

}
