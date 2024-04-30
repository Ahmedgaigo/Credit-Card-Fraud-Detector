import edu.princeton.cs.algs4.Point2D;

import java.util.ArrayList;

public class BoostingAlgorithm {
    // holds weakerLearner for array
    private ArrayList<WeakLearner> weakLearner = new ArrayList<>();
    private Clustering clustering;
    private int[][] input;
    private int[] labels;
    private Point2D[] locations; //
    private int k; // number of clusters
    private int m; // length of locations
    private double[] weights; //
    private double totalWeight; //
    private int lenOfInput; //

    // create the clusters and initialize your data structures
    public BoostingAlgorithm(int[][] input, int[] labels, Point2D[] locations,
                             int k) {

        if (input == null || locations == null || labels == null || input.length
                != locations.length || input.length != labels.length || k < 1 ||
                k > locations.length)
            throw new IllegalArgumentException("null argument or length of "
                                                       + "arguments are "
                                                       + "incompatible or K "
                                                       + "should be between 1 "
                                                       + "and length of location");

        for (int label : labels)
            if (label != 0 && label != 1)
                throw new IllegalArgumentException("Label should be 0 or 1");

        this.input = input;
        this.labels = labels;
        this.locations = locations;
        this.k = k;
        m = locations.length;
        lenOfInput = input.length;
        weights = new double[lenOfInput];

        // Reduces dimensions
        clustering = new Clustering(locations, k);
        for (int i = 0; i < lenOfInput; i++) {
            this.input[i] = clustering.reduceDimensions(input[i]);
        }

        // initializes weight to 1.0/lenOfInput
        for (int i = 0; i < lenOfInput; i++)
            weights[i] = 1.0 / lenOfInput;
    }

    // return the current weight of the ith point
    public double weightOf(int i) {
        return weights[i];
    }

    // apply one step of the boosting algorithm
    public void iterate() {
        // Creates a weak learner using the current weights and the input.
        // and adds to list
        WeakLearner newLearner = new WeakLearner(input, weights, labels);
        weakLearner.add(newLearner);

        // doubles weight of input if the weak learner mislabels it
        doubleWeightOf(newLearner);

        // Re-normalizes the weights, so that it sums to 1 again
        reNormalizeWeights();
    }

    // private method to help double each input if weak learner mislabels it
    private void doubleWeightOf(WeakLearner learner) {
        totalWeight = 0.0;
        // update weights based on learner predictions
        for (int i = 0; i < lenOfInput; i++) {
            if (learner.predict(input[i]) != labels[i])
                weights[i] *= 2.0;
            totalWeight += weights[i];
        }
    }

    // private method to help normalized weights
    private void reNormalizeWeights() {
        for (int i = 0; i < weights.length; i++)
            weights[i] /= totalWeight;
    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        if (sample == null || sample.length != input[0].length)
            throw new IllegalArgumentException("argument is null or its length"
                                                       + "is incompatible with "
                                                       + "input dimensions.");

        // dimensionally reduce sample
        sample = clustering.reduceDimensions(sample);

        int countZeroes = 0, countOnes = 0;
        for (WeakLearner newlearner : weakLearner) {
            int prediction = newlearner.predict(sample);
            if (prediction == 0) countZeroes++;
            else countOnes++;
        }

        if (countZeroes >= countOnes) return 0;
        return 1;

        // unit testing (required)
        public static void main (String[] args) {}
    }
}


