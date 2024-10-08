import java.io.Serializable;
import java.util.ArrayList;
import java.text.*;
import java.lang.Math;

public class DecisionTree implements Serializable {

	DTNode rootDTNode;
	int minSizeDatalist; //minimum number of datapoints that should be present in the dataset so as to initiate a split
	
	// Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
	public static final long serialVersionUID = 343L;
	
	public DecisionTree(ArrayList<Datum> datalist , int min) {
		minSizeDatalist = min;
		rootDTNode = (new DTNode()).fillDTNode(datalist);
	}

	class DTNode implements Serializable{
		//Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
		public static final long serialVersionUID = 438L;
		boolean leaf;
		int label = -1;      // only defined if node is a leaf
		int attribute; // only defined if node is not a leaf
		double threshold;  // only defined if node is not a leaf

		DTNode left, right; //the left and right child of a particular node. (null if leaf)

		DTNode() {
			leaf = true;
			threshold = Double.MAX_VALUE;
		}

		
		// this method takes in a datalist (ArrayList of type datum). It returns the calling DTNode object 
		// as the root of a decision tree trained using the datapoints present in the datalist variable and minSizeDatalist.
		// Also, KEEP IN MIND that the left and right child of the node correspond to "less than" and "greater than or equal to" threshold
		DTNode fillDTNode(ArrayList<Datum> datalist) {

			//ADD CODE HERE
			if (datalist.size() < minSizeDatalist){
				//double check these statements here and see if you have to create a new DTnode instead of adding leaf
				this.label = findMajority(datalist);
				return this;

			}
			else if (sameLabel(datalist)){
				this.leaf = true;
				this.label = datalist.get(0).y;
				return this;
			}

			else{
				double[] bestSplit = findBestSplit(datalist);
				this.attribute = (int) bestSplit[0];
				this.threshold = bestSplit[1];
				this.leaf = false;
				//just added this part
				if ((attribute <0) || attribute >=datalist.get(0).x.length){
					label = findMajority(datalist);
					return this;
				}



				ArrayList<Datum> leftSubset = new ArrayList<>();
				ArrayList<Datum> rightSubset = new ArrayList<>();
				for (Datum datum : datalist) {
					if (datum.x[this.attribute] < this.threshold) {
						leftSubset.add(datum);
					} else {
						rightSubset.add(datum);
					}
				}

				// Recursively create child nodes
				this.left = new DTNode().fillDTNode(leftSubset);
				this.right = new DTNode().fillDTNode(rightSubset);

				return this;


			}

		}
		private boolean sameLabel(ArrayList<Datum> datalist){
			int first_label = datalist.get(0).y;
			for (Datum datum : datalist){
				if(datum.y != first_label){
					return false;
				}
			}
			return true;
		}
		private double avg_entropy(ArrayList<Datum> datalist, int attribute, double thresh){
			ArrayList<Datum> leftSplit = new ArrayList<Datum>();
			ArrayList<Datum> rightSplit = new ArrayList<Datum>();

			for (Datum datum : datalist){
				if (datum.x[attribute] < thresh){
					leftSplit.add(datum);
				}
				else{
					rightSplit.add(datum);
				}
			}
			return((double)leftSplit.size()/datalist.size())*calcEntropy(leftSplit) + ((double)rightSplit.size()/datalist.size()) * calcEntropy(rightSplit);
		}

		private double[] findBestSplit(ArrayList<Datum> datalist) {
			double bestEntropy = Double.POSITIVE_INFINITY;
			int bestAttributeIndex = -1;
			double bestThreshold = -1;

			// Iterate over all attributes
			for (int attributeIndex = 0; attributeIndex < datalist.get(0).x.length; attributeIndex++) {
				// For each attribute, iterate over all data points to find the best threshold
				for (Datum datum : datalist) {
					double avg_entropy = avg_entropy(datalist, attributeIndex, datum.x[attributeIndex]);
					if(bestEntropy > avg_entropy){
						bestEntropy = avg_entropy;
						bestAttributeIndex = attributeIndex;
						bestThreshold = datum.x[attributeIndex];
					}

					}
				}


			return new double[]{bestAttributeIndex, bestThreshold};
		}





		// This is a helper method. Given a datalist, this method returns the label that has the most
		// occurrences. In case of a tie it returns the label with the smallest value (numerically) involved in the tie.
		int findMajority(ArrayList<Datum> datalist) {
			
			int [] votes = new int[2];

			//loop through the data and count the occurrences of datapoints of each label
			for (Datum data : datalist)
			{
				votes[data.y]+=1;
			}
			
			if (votes[0] >= votes[1])
				return 0;
			else
				return 1;
		}




		// This method takes in a datapoint (excluding the label) in the form of an array of type double (Datum.x) and
		// returns its corresponding label, as determined by the decision tree
		int classifyAtNode(double[] xQuery) {
			
			//ADD CODE HERE
			if (this.label != -1){
				return this.label;
			}
			else{
				if (xQuery[attribute] < this.threshold){
					return this.left.classifyAtNode(xQuery);
				}
				else{
					return this.right.classifyAtNode(xQuery);
				}
			}
		}


		//given another DTNode object, this method checks if the tree rooted at the calling DTNode is equal to the tree rooted
		//at DTNode object passed as the parameter
		public boolean equals(Object dt2)
		{

			//ADD CODE HERE
			if (dt2 == null || getClass() != dt2.getClass()){
				return false;
			}
			DTNode node_2 = (DTNode) dt2;

			if (this.leaf && node_2.leaf){
				return this.label == node_2.label; //double check this
			}
			else if(!this.leaf && !node_2.leaf){
				return this.threshold == node_2.threshold
						&& this.attribute == node_2.attribute
						&& this.left.equals(node_2.left)
						&& this.right.equals(node_2.right);
			}

			return false; //dummy code.  Update while completing the assignment.
		}
	}



	//Given a dataset, this returns the entropy of the dataset
	double calcEntropy(ArrayList<Datum> datalist) {
		double entropy = 0;
		double px = 0;
		float [] counter= new float[2];
		if (datalist.size()==0)
			return 0;
		double num0 = 0.00000001,num1 = 0.000000001;

		//calculates the number of points belonging to each of the labels
		for (Datum d : datalist)
		{
			counter[d.y]+=1;
		}
		//calculates the entropy using the formula specified in the document
		for (int i = 0 ; i< counter.length ; i++)
		{
			if (counter[i]>0)
			{
				px = counter[i]/datalist.size();
				entropy -= (px*Math.log(px)/Math.log(2));
			}
		}

		return entropy;
	}


	// given a datapoint (without the label) calls the DTNode.classifyAtNode() on the rootnode of the calling DecisionTree object
	int classify(double[] xQuery ) {
		return this.rootDTNode.classifyAtNode( xQuery );
	}

	// Checks the performance of a DecisionTree on a dataset
	// This method is provided in case you would like to compare your
	// results with the reference values provided in the PDF in the Data
	// section of the PDF
	String checkPerformance( ArrayList<Datum> datalist) {
		DecimalFormat df = new DecimalFormat("0.000");
		float total = datalist.size();
		float count = 0;

		for (int s = 0 ; s < datalist.size() ; s++) {
			double[] x = datalist.get(s).x;
			int result = datalist.get(s).y;
			if (classify(x) != result) {
				count = count + 1;
			}
		}

		return df.format((count/total));
	}


	//Given two DecisionTree objects, this method checks if both the trees are equal by
	//calling onto the DTNode.equals() method
	public static boolean equals(DecisionTree dt1,  DecisionTree dt2)
	{
		boolean flag = true;
		flag = dt1.rootDTNode.equals(dt2.rootDTNode);
		return flag;
	}

}
