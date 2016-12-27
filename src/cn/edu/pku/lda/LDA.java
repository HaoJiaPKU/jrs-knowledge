package cn.edu.pku.lda;


public class LDA implements Runnable{

	@Override
	public void run() {
		LDAOption option = new LDAOption();
		
		option.dir = "model";
		option.dfile = "lda.dat";
		option.est = true;
		option.inf = false;
		option.modelName = "model-final";
		option.niters = 10000;
		Estimator estimator = new Estimator();
		estimator.init(option);
		estimator.estimate();
	}

	public static void main(String[] args) {
		new LDA().run();
	}
	
}
