package cn.edu.pku.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class FileInput {
	
	public InputStreamReader isr;
	public BufferedReader reader;
	
	public static final String InputIncoding = "UTF-8";
	
	public FileInput () {
		
	}
	
	public FileInput (String inputPath) {
		initInput(inputPath);
	}
	
	public void initInput(String inputPath)
	{
		try {
			try {
				this.isr = new InputStreamReader(
						new FileInputStream(inputPath), InputIncoding);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.reader = new BufferedReader(this.isr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeInput()
	{
		try {
			this.reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) {}
}
