package cn.edu.pku.hc;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;  
  
public class Model {  
  
    public ArrayList<Cluster> clusters = null;  
    public HashMap<String, Cluster> dict
    	= new HashMap<String, Cluster>();
    
    public Model() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Model(String inputPath,
    		String inputSeperator
    		) {
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
  
    public ArrayList<Cluster> hcluster(String outputDis) {
    	FileOutput foDis = new FileOutput (outputDis);
        int currentId = -1;
        int clustersNum = clusters.size();
        while (clusters.size() > 1) {
        	if (clusters.size() <= clustersNum * 0.0) {
        		break;
        	}
        	if (clusters.size() % 100 == 0) {
        		System.out.println(clusters.size());
        	}
            // 最短距离的两聚类id  
            int lowestpair1 = 0;  
            int lowestpair2 = 1;  
            // 最短距离  
            double closest = CosDistance.getDistance(
            		clusters.get(0).getVector(),
            		clusters.get(1).getVector());  
            
            for (int i = 0; i < clusters.size(); i ++) {  
                for (int j = i + 1; j < clusters.size(); j ++) {
                    double d = CosDistance.getDistance(  
                            clusters.get(i).getVector(),
                            clusters.get(j).getVector());
                    if (d > closest) {  
                        closest = d;  
                        lowestpair1 = i;  
                        lowestpair2 = j;  
                    }
                }  
            }  
  
            // 计算两个最短距离聚类的平均值  
            List<Double> midvec = mergevec(
            		clusters.get(lowestpair1),  
                    clusters.get(lowestpair2));            
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
        foDis.closeOutput();
        return clusters;  
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
    public void printClusterAsText(
    		String outputPath,
    		String outputPathSeq,
    		String outputPathNonSeq,
    		ArrayList<Cluster> clusters, int n) {
    	FileOutput fo = new FileOutput (outputPath);
    	FileOutput foSeq = new FileOutput (outputPathSeq);
    	FileOutput foNonSeq = new FileOutput (outputPathNonSeq);
    	for (int i = 0; i < clusters.size(); i ++) {
    		writeToFile(clusters.get(i), n, 0, fo, foSeq, foNonSeq);
    	}
        fo.closeOutput();
        foSeq.closeOutput();
        foNonSeq.closeOutput();
    }
    
    public void writeToFile(Cluster cluster, int n, int upN,
    		FileOutput fo, FileOutput foSeq, FileOutput foNonSeq) {
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
	        	writeToFile(cluster.getLeft(), n + 1, n, fo, foSeq, foNonSeq);  
	        }  
	        if (cluster.getRight() != null) {  
	        	writeToFile(cluster.getRight(), n + 1, n, fo, foSeq, foNonSeq);  
	        }
	        foNonSeq.t3.write("-");
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }
  
    public void save(String modelPath) {
		FileOutput fo = new FileOutput(modelPath);
//		ArrayList<Tree> treeList = new ArrayList<Tree>();
//		for (Integer id : trees.keySet()) {
//			HashMap<String, Tree> t = trees.get(id);
//			for (String label : t.keySet()) {
//				treeList.add(t.get(label));
//			}
//		}
		
		ObjectMapper om = new ObjectMapper();
		try {
			fo.t3.write(om.writeValueAsString(this));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
	}
    
    public void load(String modelPath) {
		FileInput fi = new FileInput(modelPath);
		ObjectMapper om = new ObjectMapper();
		try {
			Model model = om.readValue(fi.reader.readLine(), Model.class);
			this.clusters = (ArrayList<Cluster>) model.clusters.clone();
			this.dict = (HashMap<String, Cluster>) model.dict.clone();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
}  

