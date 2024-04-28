import java.util.ArrayList;
import java.util.List;

public class WeakLearner {
    private int[][] input; //
    private double[] weights; //
    private int[] labels; //
    private int signPredictor; //
    private int dimensionPredictor; //
    private int valuePredictor; //
    private int k; // holds length of dimension in the input array
    private int size; // holds length of input

    // train the weak learner
    public WeakLearner(int[][] input, double[] weights, int[] labels) {


        if (input == null || weights == null || labels == null || input.length
                != weights.length || input.length != labels.length)
            throw new IllegalArgumentException("null argument or length of "
                                                       + "arguments are "
                                                       + "incompatible");

        for (double weight : weights)
            if (weight < 0)
                throw new IllegalArgumentException("Weights should be positive");

        for (int label : labels)
            if (label != 0 && label != 1)
                throw new IllegalArgumentException("Label should be 0 or 1");

        if (input[0] == null)
            throw new IllegalArgumentException("Item can't be null");

        k = input[0].length;

        for (int[] arr : input)
            if (arr.length != k)
                throw new IllegalArgumentException("Array in input can't be less"
                                                           + "than k");

        this.input = input;
        this.weights = weights;
        this.labels = labels;
        size = input.length;

        trainModel();

    }

    // Node that holds  three items
    private class Triple {
        private double weight; //
        private int label; //
        private int value; //

        //
        private Triple(double weight, int label, int value) {
            this.weight = weight;
            this.label = label;
            this.value = value;
        }
    }

    // training the model to find optimal stump parameters.
    private void trainModel() {
        int optimalDimensionPredictor = -1; // holds the optimal dimension predictor
        int optimalValuePredictor = 0; // holds the optimal value predictor
        int optimalSignPredictor = -1; // holds the optimal sign predictor
        double maxWeight = 0.0;

        // sort inputs based on dimensions
        for (int dp = 0; dp < k; dp++) {
            List<Triple> sortedInput = new ArrayList<>(); // holds sorted inputs
            for (int i = 0; i < size; i++) {
                Triple triple = new Triple(weights[i], labels[i], input[i][dp]);
                sortedInput.add(triple);
            }
            sortedInput.sort((a, b) -> Integer.compare(a.value, b.value));

            // computes total weight for correctly classified inputs
            double totalWeight = 0.0;
            for (Triple triple : sortedInput) {
                if (triple.label == 1)
                    totalWeight += triple.weight;

                // check if current stump is better than the best one found
                if (totalWeight > maxWeight) {
                    maxWeight = totalWeight;
                    optimalDimensionPredictor = dp;
                    optimalValuePredictor = triple.value;
                    if (triple.label == 0)
                        optimalSignPredictor = 1;
                    else optimalSignPredictor = 0;
                }
            }
        }

        signPredictor = optimalSignPredictor;
        dimensionPredictor = optimalDimensionPredictor;
        valuePredictor = optimalValuePredictor;

    }


    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        int decision = 1;
        if (sample == null || sample.length != input[0].length)
            throw new IllegalArgumentException("argument is null or its length"
                                                       + "is incompatible with "
                                                       + "input dimensions.");
        if (signPredictor == 0)
            if (sample[dimensionPredictor] <= valuePredictor)
                return 0;
            else return 1;

        else if (sample[dimensionPredictor] <= valuePredictor)
            return 1;

        return 0;
    }

    // return the dimension the learner uses to separate the data
    public int dimensionPredictor() {
        return dimensionPredictor;
    }

    // return the value the learner uses to separate the data
    public int valuePredictor() {
        return valuePredictor;
    }

    // return the sign the learner uses to separate the data
    public int signPredictor() {
        return signPredictor;
    }

    // unit testing (required)
    public static void main(String[] args) {
    }
}

