import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.Point2D;

public class Clustering {
    private Point2D[] locations; // holds the locations
    private int k; // holds the number of clusters
    private int m; // holds length of locations
    private int[] clusters; // hods the calculated clusters

    // run the clustering algorithm and create the clusters
    public Clustering(Point2D[] locations, int k) {
        if (locations == null || k < 1 || k > locations.length) {
            throw new IllegalArgumentException("locations can't be null or k "
                                                       + "should be between 1 "
                                                       + "and length of "
                                                       + "locations");
        }
        m = locations.length;
        this.locations = locations;
        this.k = k;
        clusters = computeClusters();
    }

    // computes the clusters
    private int[] computeClusters() {
        // Creates a complete edge weighted graph
        EdgeWeightedGraph graph = new EdgeWeightedGraph(m, m - 1);
        for (int i = 0; i < m; i++)
            for (int j = i + 1; j < m; j++) {
                double weight = locations[i].distanceTo(locations[j]);
                Edge e = new Edge(i, j, weight);
                graph.addEdge(e);
            }

        // finds minimum spanning tree
        KruskalMST MST = new KruskalMST(graph);
        Iterable<Edge> mstEdges = MST.edges();  // sorted already

        // constructs cluster graph for only the m−k edges with lowest weight
        Graph clusterGraph = new Graph(m - k);
        int count = 0;
        for (Edge e : mstEdges) {
            int v = e.either();
            int w = e.other(v);
            clusterGraph.addEdge(v, w);
            count++;
            if (count == (m - k))
                break;
        }

        // puts connected components into clusters
        int[] cluster = new int[m];
        CC cc = new CC(clusterGraph);
        for (int i = 0; i < m; i++) {
            int clusterIndex = cc.id(i);
            cluster[i] = clusterIndex;
        }
        return cluster;

    }

    // return the cluster of the ith point
    public int clusterOf(int i) {
        if (i < 0 || i >= m - 1)
            throw new IllegalArgumentException("i is out of range.");
        return clusters[i];
    }

    // use the clusters to reduce the dimensions of an input
    public int[] reduceDimensions(int[] input) {
        if (input == null || input.length != m)
            throw new IllegalArgumentException("input array length should be "
                                                       + "equal to number of "
                                                       + "locations");
        int[] reducedInput = new int[k];
        for (int i = 0; i < m; i++) {
            int clusterID = clusterOf(i);
            reducedInput[clusterID] += input[i];
        }
        return reducedInput;
    }

    // unit testing (required)
    public static void main(String[] args) {
    }
}



