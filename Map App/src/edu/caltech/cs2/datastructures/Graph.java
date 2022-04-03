package edu.caltech.cs2.datastructures;

import com.sun.net.httpserver.Filter;
import edu.caltech.cs2.interfaces.IGraph;
import edu.caltech.cs2.interfaces.ISet;


public class Graph<V, E> implements IGraph<V, E> {

    private ChainingHashDictionary<V, ChainingHashDictionary<V,E>> adjList;

    public Graph(){
        this.adjList = new ChainingHashDictionary<V, ChainingHashDictionary<V, E>>(MoveToFrontDictionary::new);
    }

    @Override
    public boolean addVertex(V vertex) {
        if(adjList.keySet().contains(vertex)){
            return false;
        }
        adjList.put(vertex, new ChainingHashDictionary<V, E>(MoveToFrontDictionary::new));
        return true;
    }

    @Override
    public boolean addEdge(V src, V dest, E e) {        // directed edge
        if(!adjList.keySet().contains(src) || !adjList.keySet().contains(dest)){
            throw new IllegalArgumentException("Vertices do not exist");
        }
        if(adjList.get(src).keySet().contains(dest)){
            adjList.get(src).put(dest, e);
            return false;
        }
        else{
            adjList.get(src).put(dest, e);
            return true;
        }
    }

    @Override
    public boolean addUndirectedEdge(V n1, V n2, E e) {
        if(!adjList.keySet().contains(n1) || !adjList.keySet().contains(n2)){
            throw new IllegalArgumentException("Vertices do not exist");
        }
        if(adjList.get(n1).keySet().contains(n2) || adjList.get(n2).keySet().contains(n1)){
            adjList.get(n1).put(n2, e);
            adjList.get(n2).put(n1, e);
            return false;
        } else{
            adjList.get(n1).put(n2, e);
            adjList.get(n2).put(n1, e);
            return true;
        }
    }

    @Override
    public boolean removeEdge(V src, V dest) {
        if(!adjList.keySet().contains(src) || !adjList.keySet().contains(dest)){
            throw new IllegalArgumentException("Vertices do not exist");
        }
        if(!adjList.get(src).keySet().contains(dest)){
            return false;
        }
        if(adjList.get(src).keySet().contains(dest)){
            adjList.get(src).remove(dest);
        }
        return true;
    }

    @Override
    public ISet<V> vertices() {
        return adjList.keySet();
    }

    @Override
    public E adjacent(V i, V j) {
        if(!adjList.keySet().contains(i) || !adjList.keySet().contains(j)){
            throw new IllegalArgumentException("Vertices do not exist");
        }
        if(adjList.get(i).keySet().contains(j)){
            return adjList.get(i).get(j);
        }
        return null;
    }

    @Override
    public ISet<V> neighbors(V vertex) {
        if(!adjList.keySet().contains(vertex)){
            throw new IllegalArgumentException("Vertex does not exist");
        }
        return adjList.get(vertex).keySet();
    }
}