package cn.edu.pku.hc;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;

import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;  
  
public class HierarchicalClustering {  
  
    private List<Cluster> clusters = null;  
    private HashMap<String, Cluster> dict
    	= new HashMap<String, Cluster>();
    private FileOutput fo;
    private FileOutput foSeq;
    private FileOutput foNonSeq;
    private FileOutput foDis;
    
    public HierarchicalClustering(String inputPath, String inputSeperator
    		, String outputDis) { 
    	foDis = new FileOutput (outputDis);
        initData(inputPath, inputSeperator);
    }  
  
    /** 
     * 初始化数据集 
     *  
     * @throws IOException 
     */  
    private void initData(String inputPath, String inputSeperator) {
    	
        clusters = new ArrayList<Cluster>();  
        FileInput fi = new FileInput(inputPath); 
        String line = null;  
        int i = 0;  
        try {
			while ((line = fi.reader.readLine()) != null) {  
			    String[] s = line.split(inputSeperator);  
			    Cluster cluster = new Cluster();  
			    List<Double> list = new ArrayList<Double>();
			    cluster.setName(s[0]);
			    for (int j = 1; j < s.length; j ++) {
			    	list.add(Double.parseDouble(s[j]));
			    }
			    cluster.setId(i ++);  
			    cluster.setVector(list);
			    clusters.add(cluster);
			    dict.put(s[0], cluster);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
//        System.out.print(CosDistance.getDistance(
//        			dict.get("java").getVector(),
//        			dict.get("ppt").getVector()));
    }  
  
    public Cluster hcluster() {   
  
        int currentId = -1;  
  
        while (clusters.size() > 1) {  
        	System.out.println(clusters.size());
            // 最短距离的两聚类id  
            int lowestpair1 = 0;  
            int lowestpair2 = 1;  
            // 最短距离  
            double closest = CosDistance.getDistance(
            		clusters.get(0).getVector(),
            		clusters.get(1).getVector());  
//            // 用distances来缓存任意两聚类之间的距离,其中map集合的键为两个聚类的id  
//            Map<Integer[], Double> distances = new HashMap<Integer[], Double>(); 
            
            for (int i = 0; i < clusters.size(); i ++) {  
                for (int j = i + 1; j < clusters.size(); j ++) {  
//                  Integer[] key = {
//                    		clusters.get(i).getId(),  
//                            clusters.get(j).getId()};
                    double d = CosDistance.getDistance(  
                            clusters.get(i).getVector(),
                            clusters.get(j).getVector());
//                    if (!distances.containsKey(key)) {  
//                        distances.put(key, CosDistance.getDistance(  
//                                clusters.get(i).getVector(),
//                                clusters.get(j).getVector()));  
//                    }
//  
//                    double d = distances.get(key);  
                    if (d > closest) {  
                        closest = d;  
                        lowestpair1 = i;  
                        lowestpair2 = j;  
                    }  
//                    System.out.println(i + " " + j);
                }  
            }  
  
            // 计算两个最短距离聚类的平均值  
            List<Double> midvec = mergevec(
            		clusters.get(lowestpair1),  
                    clusters.get(lowestpair2));  
//            System.out.println(
//            		clusters.get(lowestpair1).getName() + "	"
//            		+ clusters.get(lowestpair2).getName() + " "
//            		+ closest);
            
            try {
				foDis.t3.write(String.valueOf(closest));
				foDis.t3.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            Cluster cluster = new Cluster(
            		clusters.get(lowestpair1),
            		clusters.get(lowestpair2),
            		midvec,
            		currentId,
            		closest);  
  
            currentId -= 1;  
              
            //注意删除顺序，先删除大的id号，否则会出现越界  
            if (lowestpair1 < lowestpair2) {  
                clusters.remove(clusters.get(lowestpair2));  
                clusters.remove(clusters.get(lowestpair1));  
            } else {  
                clusters.remove(clusters.get(lowestpair1));  
                clusters.remove(clusters.get(lowestpair2));  
            }  
            clusters.add(cluster);  
        }  
        return clusters.get(0);  
    }  
  
    private List<Double> mergevec(Cluster cluster1, Cluster cluster2) {  
        List<Double> midvec = new ArrayList<Double>();  
        for (int i = 0; i < cluster1.getVector().size(); i++) {  
            midvec.add((cluster1.getVector().get(i) + cluster2.getVector().get(i)) / 2.0);  
        }  
        return midvec;    
    }  
      
    /** 
     * 打印输出 
     */  
    public void saveCluster(
    		String outputPath,
    		String outputPathSeq,
    		String outputPathNonSeq,
    		Cluster cluster, int n) {
    	fo = new FileOutput (outputPath);
    	foSeq = new FileOutput (outputPathSeq);
    	foNonSeq = new FileOutput (outputPathNonSeq);
    	writeToFile(cluster, n, 0);
        fo.closeOutput();
        foSeq.closeOutput();
        foNonSeq.closeOutput();
        foDis.closeOutput();
    }
    
    public void writeToFile(Cluster cluster, int n, int upN) {
    	try {
    		for (int i = 0; i < upN; i++) {
				fo.t3.write("  ");
	        }
	    	for (int i = upN; i < n; i++) {
				fo.t3.write("┗━");
	        }
	    	foNonSeq.t3.write("-");
	        //负数标记代表这是一个分支  
	        if (cluster.getId() < 0) {  
	        	fo.t3.write("┓");
	        	fo.t3.newLine();
	        	foSeq.t3.write("-");
	        } else {  
	            //代表是一个叶子节点   
	        	fo.t3.write("━");
	        	fo.t3.write(cluster.getName());
	        	fo.t3.newLine();
	        	foSeq.t3.write(cluster.getName());
	        	foNonSeq.t3.write(cluster.getName());
	        }
	        if (cluster.getLeft() != null) {  
	        	writeToFile(cluster.getLeft(), n + 1, n);  
	        }  
	        if (cluster.getRight() != null) {  
	        	writeToFile(cluster.getRight(), n + 1, n);  
	        }
	        foNonSeq.t3.write("-");
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }
  
    public static void main(String[] args) {  
        HierarchicalClustering hierarchicalClustering
        	= new HierarchicalClustering("../proIndustry/vec.txt", "	",
        			"../proIndustry/hclusterDis.txt");  
        hierarchicalClustering.saveCluster(
        		"../proIndustry/hcluster.txt",
        		"../proIndustry/hclusterSeq.txt",
        		"../proIndustry/hclusterNonSeq.txt",
        		hierarchicalClustering.hcluster(), 0);  
    }  
  
}  

