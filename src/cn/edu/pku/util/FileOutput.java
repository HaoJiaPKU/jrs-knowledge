package cn.edu.pku.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class FileOutput {
	
	public FileOutputStream t1;
	public OutputStreamWriter t2;
	public BufferedWriter t3;
	
	public static final String EncodingOutput = "UTF-8";
	
	public FileOutput () {
		
	}
	
	public FileOutput (String outputPath) {
		initOutput(outputPath);
	}
	
	public void initOutput(String outputPath) {
		try {
			File file = new File(outputPath);
			this.t1 = new FileOutputStream(file);
			try {
				this.t2 = new OutputStreamWriter(this.t1, EncodingOutput);
				this.t3 = new BufferedWriter(this.t2);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeOutput() {
		try {
			if (this.t3 != null) {
				this.t3.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) {}
}
