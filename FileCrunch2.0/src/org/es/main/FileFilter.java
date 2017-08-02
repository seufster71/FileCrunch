package org.es.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FileFilter extends ProcessManager {

	private String inputFile = null;
	private String outputPath = null;
	private String fileName = null;
	private String extention = "txt";
	private Long fileSize = 60000000l;
	private boolean singleFile = true;
	private ArrayList<FilterItem> filters = new ArrayList<FilterItem>();
	private LinkedList<String> queue = new LinkedList<String>();
	
	public FileFilter(String inputFile, String outputPath, String fileName, Long fileSize) {
		this.inputFile = inputFile;
		this.outputPath = outputPath + "\\";
		this.fileName = fileName;
		this.fileSize = fileSize;
	}
	
	
	public void process() {
		File file = new File(inputFile);
		if (!file.exists()){
			System.out.println("File does not exist " + file.getPath());
			return;
		}

		BufferedReader br;
		try {
			notifyListeners("Status: Calculating Line Count");
			FileReader fr1 = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(fr1);
		    int linenumber = 0;
	        while (lnr.readLine() != null){
	        	linenumber++;
	        }
	    //    System.out.println("Total lines : "+linenumber);
			lnr.close();
			fr1.close();
			double px = linenumber*.1;
			Long pl = Math.round(px);
			notifyListeners("Status: Processing File");
			
			FileReader fr2 = new FileReader(file);
			br = new BufferedReader(fr2);
			
			String line;
			Long lineCount = 1l;
			Long subCount = 1l;
			int offset = 1;
			Long size = 1l;
			String fileOut = outputPath + fileName + ".Filter" + offset + "." + extention;
			File outFile = new File(fileOut);
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			int percentComplete = 0;
			Long plTotal = pl;
			//Pattern.compile(Pattern.quote(f), Pattern.CASE_INSENSITIVE);
			while ((line = br.readLine()) != null) {
				if (lineCount > plTotal){
					plTotal = plTotal + pl;
					percentComplete = percentComplete + 10;
					notifyListeners("Status: Processing File -- Percentage: "+percentComplete);
				}
				byte[] chunks = line.getBytes("UTF-8");
				size = size + chunks.length;
				if (size > fileSize * offset &&  !singleFile) {
						bw.close();
						offset++;
						outFile = new File(outputPath + fileName + ".Filter" + offset + "." + extention);
						if (!outFile.exists()) {
							outFile.createNewFile();
						}
						fw = new FileWriter(outFile.getAbsoluteFile());
						bw = new BufferedWriter(fw);
				}
				// Filter entry
				for(FilterItem f : filters){
					//if (.matcher(line).find()){
					if (line.contains(f.getFilter())){
						int qsize = queue.size();
						
						if (qsize > 4 ) { 
							String myline = queue.get(qsize - 2);
							if (myline.contains("<env:Envelope xmlns:env") ){
								String[] x = myline.split("252\\) - ");
								this.processXML(x[1],bw);
							}
						}
						bw.write("(" + lineCount+ ")" + line + "\r\n");
						f.setCount(f.getCount() + 1);
					}
				}
				// Add to queue
				queue.addLast(line);
				if (queue.size() > 10){
					queue.pollFirst();
				}
				lineCount++;
	
				if (subCount > 50000){
					System.out.print("\r Line " + lineCount);
					subCount = 1l;
				} else {
					subCount++;
				}
			}
			bw.close();
			br.close();
			fr2.close();
			notifyListeners("Status: Complete");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\n");
		for(FilterItem f : filters){
			System.out.println("Filter: " + f.getFilter() + " count " + f.getCount());
		}
	}
	
	public void processXML(String xml, BufferedWriter bw){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Account account = new Account();
		try {
			builder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(xml));
			Document doc = builder.parse(inputSource);
			NodeList otList = doc.getElementsByTagName("DTOOutputItem");
			if (otList != null){
				Node node = otList.item(0);
				if (node.getNodeType() == Node.ELEMENT_NODE){
					Element element = (Element) node;
					account.setFormCd(element.getAttribute("FormCd"));
					account.setItemDescription(element.getAttribute("ItemDescription"));
				}
			}
			NodeList arpList = doc.getElementsByTagName("DTOARPolicy");
			if (arpList != null){
				Node node = arpList.item(0);
				if (node.getNodeType() == Node.ELEMENT_NODE){
					Element element = (Element) node;
					account.setPolicyNumber(element.getAttribute("PolicyNumber"));
				}
			}
			NodeList accList = doc.getElementsByTagName("DTOAccount");
			if (accList != null){
				Node node = accList.item(0);
				if (node.getNodeType() == Node.ELEMENT_NODE){
					Element element = (Element) node;
					account.setAccountNumber(element.getAttribute("AccountNumber"));
				}
			}
			bw.write("Request Info: FormCd=" + account.getFormCd() + " PolicyNumber=" + account.getPolicyNumber() + " AccountNumber=" + account.getAccountNumber() + " ItemDescription=" + account.getItemDescription() +"\n");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public Map<String, Object> call() {
		this.process();
		return null;
	}
	
}
