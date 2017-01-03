package cn.edu.pku.lda;


public class Main implements Runnable{

	@Override
	public void run() {
		LDAOption option = new LDAOption();
		
		option.dir = "../processing";
		option.dfile = "tokens.dat";
		option.est = true;
		option.inf = false;
		option.modelName = "model-final";
		option.niters = 1000;
		Estimator estimator = new Estimator();
		estimator.init(option);
		estimator.estimate();
	}

	public static void main(String[] args) {
		new Main().run();
	}
	
}
