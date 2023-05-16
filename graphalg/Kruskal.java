package graphalg;

import graph.*;
import set.*;
import dict.*;

/**
 * The Kruskal class contains the method minSpanTree(), which implements
 * Kruskal's algorithm for computing a minimum spanning tree of a graph.
 */
public class Kruskal {

  /**
   * minSpanTree() returns a WUGraph that represents the minimum spanning tree
   * of the WUGraph inputGraph. The original WUGraph inputGraph is NOT changed.
   *
   * @param inputGraph The input graph to find the minimum spanning tree of.
   * @return A new WUGraph representing the minimum spanning tree of inputGraph.
   */
  public static WUGraph minSpanTree(WUGraph inputGraph) {
    // Step 1: Create a new graph T with the same vertices as G, but no edges (yet).
    WUGraph minSpanTree = new WUGraph();
    Object edgeArray[][] = new Object[2 * inputGraph.edgeCount()][3];
    HashTableChained vertexHashTable = new HashTableChained(inputGraph.vertexCount());
    DisjointSets disjointSet = new DisjointSets(inputGraph.vertexCount());
    Neighbors neighborList;
    int edgeCounter = 0;
    int vertexMapping = 0;

    // Add vertices to the new graph and create a hash table for vertex mapping.
    for (Object vertex : inputGraph.getVertices()) {
      minSpanTree.addVertex(vertex);
      vertexHashTable.insert(vertex, vertexMapping++);
      neighborList = inputGraph.getNeighbors(vertex);

      // Step 2: Make a list of all the edges in G.
      for (int i = 0; i < neighborList.neighborList.length; i++) {
        edgeArray[edgeCounter][0] = neighborList.weightList[i];
        edgeArray[edgeCounter][1] = vertex;
        edgeArray[edgeCounter][2] = neighborList.neighborList[i];
        edgeCounter++;
      }
    }

    // Refine the edge array.
    Object refinedEdges[][] = new Object[edgeCounter][3];
    for (int i = 0; i < refinedEdges.length; i++) {
      refinedEdges[i] = edgeArray[i];
    }

    // Step 3: Sort the edges by weight in O(|E| log |E|) time.
    edgeArray = mergeSort(refinedEdges);

    // Step 4: Find the edges of T using disjoint sets.
    int vertex1;
    int vertex2;
    for (Object[] edge : edgeArray) {
      vertex1 = (Integer) vertexHashTable.find(edge[1]).value();
      vertex2 = (Integer) vertexHashTable.find(edge[2]).value();
      vertex1 = disjointSet.find(vertex1);
      vertex2 = disjointSet.find(vertex2);
      if (vertex1 != vertex2) {
        disjointSet.union(vertex1, vertex2);
        minSpanTree.addEdge(edge[1], edge[2], (Integer) edge[0]);
      }
    }

    return minSpanTree;
  }

  /**
   * mergeSort() sorts the input array of edges by weight using the merge sort algorithm.
   *
   * @param elements The input array of edges to be sorted.
   * @return A sorted array of edges by weight.
   */
  private static Object[][] mergeSort(Object elements[][]) {
    if (elements.length < 2) {
      return elements;
    }
    Object[][] leftHalf, rightHalf;
    int middle = elements.length / 2;
    leftHalf = new Object[middle][];
    for (int i = 0; i < middle; i++) {
      leftHalf[i] = elements[i];
    }
    rightHalf = new Object[elements.length - middle][];
    for (int i = middle; i < elements.length; i++) {
      rightHalf[i - middle] = elements[i];
    }
    leftHalf = mergeSort(leftHalf);
    rightHalf = mergeSort(rightHalf);
    return merge(leftHalf, rightHalf);
  }

  /**
   * merge() merges two sorted arrays of edges into a single sorted array.
   *
   * @param left  The first sorted array of edges.
   * @param right The second sorted array of edges.
   * @return A merged sorted array of edges.
   */
  private static Object[][] merge(Object left[][], Object right[][]) {
    Object[][] sortedArray = new Object[left.length + right.length][];
    int rightIndex = 0, leftIndex = 0, sortedIndex = 0;
    while (leftIndex < left.length && rightIndex < right.length) {
      if ((left[leftIndex][0] != null && right[rightIndex][0] != null) && (Integer) left[leftIndex][0] < (Integer) right[rightIndex][0]) {
        sortedArray[sortedIndex++] = left[leftIndex++];
      } else {
        sortedArray[sortedIndex++] = right[rightIndex++];
      }
    }
    while (leftIndex < left.length) {
      sortedArray[sortedIndex++] = left[leftIndex++];
    }
    while (rightIndex < right.length) {
      sortedArray[sortedIndex++] = right[rightIndex++];
    }
    return sortedArray;
  }
}