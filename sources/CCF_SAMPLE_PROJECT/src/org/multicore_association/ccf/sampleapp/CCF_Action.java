/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.ccf.sampleapp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.jface.action.Action;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class CCF_Action extends Action {
	
	String ccf_result;
	String _att_uri;
	String _att_xpt;
	String _postfix = "";

	CCF_Action(String result, String uri, String path) {
		ccf_result = result;
		_att_uri = uri;
		_att_xpt = path;
	}
	CCF_Action(String result, String uri, String path, String postfix) {
		ccf_result = result;
		_att_uri = uri;
		_att_xpt = path;
		_postfix = postfix;
	}
	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return super.getText();
	}
	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub
		super.setText(text);
	}

	@Override
	public void run() {
	
		System.out.println("CCF Action :: run()");
		
		super.run();
		
		DocumentBuilder builder;
		
		if(_att_uri == null) {
			System.out.println("No Output File URI");
			return;
		}
		if(_att_xpt == null) {
			System.out.println("No Outpur XPath ");
			return;
		}
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new FileInputStream(_att_uri));

			// Expression for getting single node.
			String expression1 = _att_xpt;

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
			Node node = (Node) xpath.compile(expression1).evaluate(doc,
					XPathConstants.NODE);

			NodeList nodeList = (NodeList) xpath.compile(expression1).evaluate(
					doc, XPathConstants.NODESET);
			
			System.out.println("[XPath]" + expression1);
			System.out.println("[URL]" + _att_uri);
			
			System.out.println("*nodeList size="+nodeList.getLength());

			for (int i = 0; i < nodeList.getLength(); i++) {

				Node _node = nodeList.item(i);
				System.out.println(i + ")" + "  NodeName="
						+ _node.getNodeName());
				System.out.println(i + ")" + "  NodeValue="
						+ _node.getNodeValue());

				// int idx = ccf_combo.getSelectionIndex();
				// String item = ccf_combo.getItem(idx);
				String item = ccf_result;

				System.out.println("ccf_result (item) =" + item);

				_node.setNodeValue(item);

			}
			System.out.println("split uri "+_att_uri);
		
			int idx = _att_uri.lastIndexOf(".");
			
			String fnwoex = _att_uri.substring(0, idx);
			String extension = _att_uri.substring(idx);

			System.out.println("file name without extension =  "+fnwoex);
			System.out.println("file name         extension =  "+extension);
			
			//File aFile = new File(_att_uri);
			
			File aFile = new File(fnwoex+_postfix+extension);
			//File aFile = new File(fnwoex+extension);
			
			System.out.println("SaveDocument Pre filename="+aFile.getName());
			
			saveDocument(aFile, doc);
			
			System.out.println("SaveDocument Post");
			

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean saveDocument(File aFile, Document aDocument) {

		// ---------------------------------
		// step1. Transformerの準備
		// ---------------------------------
		Transformer transformer = null;
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			transformer = factory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// 通常はありえない。
			e.printStackTrace();
			return false;
		}

		// ---------------------------------
		// step2. Transformerの動作設定
		// ---------------------------------
		transformer.setOutputProperty("indent", "yes");
		transformer.setOutputProperty("encoding", "Shift_JIS");

		// -------------------------------------
		// step3. Documentをファイルに書き出す
		// ------------------------------------
		try {
			transformer.transform(new DOMSource(aDocument), new StreamResult(
					aFile));
		} catch (TransformerException e) {
			// 書き出しエラー発生
			e.printStackTrace();
			return false;
		}

		// 終了
		return true;
	}

	


}
