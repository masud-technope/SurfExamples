package core;

public interface ScoreWeights {
	
	//lexical weights
	public double CloneWeight=460.8358; // 1.0; //estimated as 5.00
	public double CosineSimWeight=78.506;// 1.0;
	
	//structural weights
	public double CodeObjectMatchWeight=1.4526;// .29384;// .27336;
	public double CodeObjectFieldMatchWeight=7.8486;// .3708;//0.0;
	public double CodeObjectMethodMatchWegiht=1.5182;// .30884;// .27402;
	public double CodeObjectDependencyWegiht=.8389;// .19444;//.43536;
	
	public double CodeObjectMatchSuperMatchWeight=1.0;
	public double CodeObjectDependencyCompleteMatchWeight=1.0;
	public double CodeObjectDependencyPartialMatchWeight=.5;
	
	//quality weights
	public double ReadabilityWeight=.6704;// .14182;//0.1367;
	public double AvgStmtWeight=1.134;// .3124;// 0.23178;
	public double H2cRatioWeight=2.4083;// .65812;//0.64092;
	
	//overall scores
	public double LexicalWeight= 1.0152;// 1.0;
	public double StructuralWeight=1.2787;// 1.0;
	public double QualityWeight=1.1588;// 1.0;
	
	//heuristic weights
	public double HandlesWeight=5.0;
	public double UsesWeight=1.0;
	public double CallsWeight=1.0;
	
	//thresholds
	public double HCR_THRESHOLD=0.4;
	public double EXAMPLE_SIZE_THRESHOLD=0;
	
}
