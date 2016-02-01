import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.lang.*;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 * 
 * You must add code for the 5 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	private List<String> labels; // ordered list of class labels
	private List<String> attributes; // ordered list of attributes
	private Map<String, List<String>> attributeValues; // map to ordered
	// discrete values taken
	// by attributes

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary
		// this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train: the training set
	 */
	DecisionTreeImpl(DataSet train) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		// TODO: add code here
		List<Instance> instList = new ArrayList<Instance>();
		instList.addAll(train.instances);
		List<Integer> attribs = new ArrayList<Integer>();
		for(int k = 0; k<attributes.size(); k++) attribs.add(k);
		//List<Instance> parentEx = new ArrayList<Instance>();
		
		int origLabelMaj = -1;
		int l0 = 0;
		int l1 = 0;
		for (Instance a : train.instances)
		{
			
			if(a.label == 0)
				l0++;
			else
				l1++;
			
		}
		if(l0>l1)
			origLabelMaj = 0;
		else
			origLabelMaj = 1;

		this.root = buildTree(instList, attribs, origLabelMaj);
		

	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning
	 * set.
	 * 
	 * @param train: the training set
	 * @param tune: the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		// TODO: add code here
		List<Instance> instList = new ArrayList<Instance>();
		instList.addAll(train.instances);
		List<Integer> attribs = new ArrayList<Integer>();
		for(int k = 0; k<attributes.size(); k++) attribs.add(k);
		//List<Instance> parentEx = new ArrayList<Instance>();
		
		int origLabelMaj = -1;
		int l0 = 0;
		int l1 = 0;
		for (Instance a : train.instances)
		{
			
			if(a.label == 0)
				l0++;
			else
				l1++;
			
		}
		if(l0>l1)
			origLabelMaj = 0;
		else
			origLabelMaj = 1;

		this.root = buildTree(instList, attribs, origLabelMaj);
		this.root = prune(this.root, tune);
		//toPrune.terminal = true;
	}

	private DecTreeNode buildTree(List<Instance> examples, List<Integer> attr, Integer parentsMajLabel)
	{
		//check that more instances exist
		if(examples.isEmpty())
		{
			//TODO it's value should be the most common label of the current node's parent node
			return new DecTreeNode(parentsMajLabel, -100, null, true);
		}

		//grab label of first instance, since we know atleast one instance exists
		String firstlabel = labels.get(examples.get(0).label);
		boolean matching = true;
		//for every instance left, if any labels do not match, return null
		for(int i = 0; i< examples.size(); i++)
		{
			if(!labels.get(examples.get(i).label).equals(firstlabel))
			{
				matching = false;
				break;
			}
		}
		if(matching)
		{
			return new DecTreeNode(labels.indexOf(firstlabel), null, null, true);
		}
		
		

		int labelMaj = -1;
		int l0 = 0;
		int l1 = 0;
		for (Instance j : examples)
		{
			
			if(j.label == 0)
				l0++;
			else
				l1++;
			
		}
		if(l0>=l1)
			labelMaj = 0;
		else
			labelMaj = 1;
		
		
		
		
		if(attr.isEmpty())
		{

			/*int label0 = countCZero(examples);
			int label1 = examples.size() - label0;
			int retLabel = -1;
			if(label0>=label1)
				retLabel = 0;
			else
				retLabel = 1;
			*/
				return new DecTreeNode(labelMaj, null, null, true);
		}


		Map<Integer, Double> gains = getIGains(examples, attr);
		int bestAttr = -1;
		double maxGain = -1.5;
		for(int y : gains.keySet())
		{
			if(gains.get(y) > maxGain)
			{
				maxGain = gains.get(y);
				bestAttr = y;
			}
		}
		
		DecTreeNode treeRoot = new DecTreeNode(labelMaj, bestAttr, -1, false);
		List<String> currQ = attributeValues.get(attributes.get(bestAttr));
		/*List<Integer> attrSubset = new ArrayList<Integer>();
		attrSubset.addAll(attr);
		attrSubset.remove((Integer)bestAttr);
		*/
		//looking at current attribute, subtree for each 
		for(int i = 0; i<currQ.size(); i++)
		{
			List<Integer> attrSubset = new ArrayList<Integer>();
			attrSubset.addAll(attr);
			attrSubset.remove((Integer)bestAttr);
			
			List<Instance> exSubset = createSubset(examples, bestAttr, currQ.get(i), currQ);
			DecTreeNode subTree = buildTree(exSubset, attrSubset, labelMaj);
			if(subTree.terminal == true)
			{
				subTree.parentAttributeValue = i;//currQ.indexOf(currQ.get(i));
				treeRoot.addChild(subTree);
			}
			else if(subTree.attribute == -100)
			{
				treeRoot.terminal = true;
				treeRoot.attribute = null;
				treeRoot.label = subTree.label;
			}
			else
			{
				subTree.parentAttributeValue = i;//currQ.indexOf(currQ.get(i));
				treeRoot.addChild(subTree);
			}
		}
		return treeRoot;


	}
	
	private DecTreeNode prune(DecTreeNode top, DataSet tuner)
	{
		double baseAcc = calcAccuracy(top, tuner.instances);
		double mostAccurate = baseAcc;
		DecTreeNode pruner = null;
		List<DecTreeNode> queue = new LinkedList<DecTreeNode>();
		queue.add(top);
		while(!queue.isEmpty())
		{
			DecTreeNode curr = queue.remove(0);
			curr.terminal = true;
			double currAcc = calcAccuracy(top, tuner.instances);
			if(currAcc>mostAccurate)
			{
				mostAccurate = currAcc;
				pruner = curr;
			}
			curr.terminal = false;
			Iterator<DecTreeNode> itr = curr.children.iterator();
			while(itr.hasNext())
			{
				DecTreeNode child = itr.next();
				if(!child.terminal)
				{
					queue.add(child);
				}
			}
		}
		if(pruner != null)
			pruner.terminal = true;
		return top;
	}
	
	private double calcAccuracy(DecTreeNode top, List<Instance> tune)
	{
		double total = (double) tune.size();
		int correct = 0;
		for(Instance i : tune)
		{
			String labelGuess = classify(i);
			if(labelGuess.equals(labels.get(i.label)))
				correct++;
		}
		
		return correct/total;
	}
	
	private List<Instance> createSubset(List<Instance> initEx, int Att, String currAtt, List<String> currQ)
	{
		List<Instance> subset = new ArrayList<Instance>();
		for (int k = 0; k<initEx.size(); k++)
		{
			if(currQ.get(initEx.get(k).attributes.get(Att)).equals(currAtt))
			{
				subset.add(initEx.get(k));
			}
		}
		
		return subset;
	}

	@Override
	public String classify(Instance instance) {

		// TODO: add code here
		DecTreeNode curr =  root;
		while(!curr.terminal)
		{
			//choose edge
			int attr = curr.attribute;
			DecTreeNode kid = curr.children.get(instance.attributes.get(curr.attribute));
			//TODO make sure curr chooses next curr
			curr = kid;
		}

		//now curr is terminal, check its label and return
		return labels.get(curr.label);
	}

	@Override
	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {

		printTreeNode(root, null, 0);
	}

	/**
	 * Prints the subtree of the node
	 * with each line prefixed by 4 * k spaces.
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else{
			String parentAttribute = attributes.get(parent.attribute);
			value = attributeValues.get(parentAttribute).get(p.parentAttributeValue);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + labels.get(p.label) + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + attributes.get(p.attribute) + "?}");
			System.out.println(sb.toString());
			for(DecTreeNode child: p.children) {
				printTreeNode(child, p, k+1);
			}
		}
	}

	@Override
	public void rootInfoGain(DataSet train) {


		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		// TODO: add code here
		List<Integer> attrlist = new ArrayList<Integer>();
		for (int i = 0; i < attributes.size(); i++)
		{
			attrlist.add(i);
		}
		Map<Integer, Double> igains = getIGains(train.instances, attrlist);
		for(int d = 0; d<igains.size(); d++)
		{
			System.out.format(attributes.get(d) + " %.5f", igains.get(d));
			System.out.println();
		}

	}

	private int countCZero(List<Instance> list)
	{
		int label0 = 0;
		for (int i = 0; i<list.size(); i++)
		{
			if(labels.get(list.get(i).label) == labels.get(0))
			{
				label0++;
			}
		}
		return label0;
	}

	private Map<Integer, Double> getIGains(List<Instance> instList, List<Integer> attrList)
	{
		Map<Integer, Double> igains = new HashMap<Integer, Double>();
		int numY0 = countCZero(instList);
		int numY1 = instList.size() - numY0;
		double numYAll = (double) instList.size();
		double hofY = 0;
		double probY0 = numY0/numYAll;
		double probY1 = numY1/numYAll;
		if(probY0 == 0 || probY1 ==0){
			hofY = 0;
		}
		else {
			double left = -1*probY0*(Math.log(probY0)/Math.log(2));
			double right = -1* probY1*(Math.log(probY1)/Math.log(2));
			hofY = left + right;
		}
		
		//TESTING
		//System.out.println("hofY = " + hofY);

		for (int i = 0; i<attrList.size(); i++)
		{
			List<String> currAtt = attributeValues.get(attributes.get(attrList.get(i)));

			//in slides as Pr(X = vi)
			//ie probability that Attribute is a certain attribute
			//double probAttisatt = 0;
			List<Double> condEnt = new ArrayList<Double>();


			for (int j = 0; j<currAtt.size(); j++)
			{

				double hYgivenA = 0;
				double numInstWithAtt = 0;
				int numInstWith1 = 0;
				int numInstWith0 = 0;
				double probY0att;
				double probY1att;

				for (int k = 0; k<instList.size(); k++)
				{
					//System.out.println("i = " + i + " j = " + j + " k = " + k);
					if(currAtt.get(instList.get(k).attributes.get(attrList.get(i))).equals(currAtt.get(j)) )
					{
						numInstWithAtt++;
						if(labels.get(instList.get(k).label).equals(labels.get(0))) numInstWith0++;
						else numInstWith1++;
					}
				}

				//TEST
				if(numInstWithAtt == 0) {
					hYgivenA = 0;
				}
				else
				{
					probY0att = numInstWith0/numInstWithAtt;
					probY1att = numInstWith1/numInstWithAtt;
					if(probY0att == 0 || probY1att == 0){
						hYgivenA = 0;
					} else {
						hYgivenA = (-probY0att*(Math.log(probY0att)/Math.log(2))) + (-probY1att*(Math.log(probY1att)/Math.log(2))); 
					}
					
				}


				condEnt.add(((double)numInstWithAtt/instList.size())*hYgivenA);
			}
			double sum = 0;
			for(int xx = 0; xx<condEnt.size(); xx++)
			{
				sum += condEnt.get(xx);
				//debug
				//System.out.format("%.5f" + "   ", condEnt.get(xx));
			}
			double igain = hofY - sum;
			igains.put(attrList.get(i), igain);
			/*
			for(int key: igains.keySet())
			{
				System.out.println(key + " = " + igains.get(key));
			}
			*/
			//debug
			//System.out.println(condEnt.size());
			//System.out.println(sum);

		}
		
		return igains;
	}
}
