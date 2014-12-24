/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.ccf.sampleapp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.multicore_association.ccf.api.Configuration;
import org.multicore_association.ccf.api.Def;
import org.multicore_association.ccf.api.DefineSet;
import org.multicore_association.ccf.api.Expression;
import org.multicore_association.ccf.api.FormType;
import org.multicore_association.ccf.api.Item;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import swing2swt.layout.BorderLayout;
import swing2swt.layout.BoxLayout;

/**
 * CCF Composition CCFを読み込んで、対応のGUIを生成する.
 * また、CCFに記述されている出力先のURIとXPathの指す場所にCCFで決定した値を 書きこんだ XMLを出力する事ができる。
 * 
 * @author user
 * 
 */
public class CCF_ConfigurationComposite extends Composite implements
		SelectionListener {

	public static Composite root = null;

	public Label lbl_name;
	public Combo ccf_select_combo;

	private String _ccf_result = "";

	int _selectedIdx = 0;

	private String _ccf_name = null;
	private Configuration _cf = null;

	public String getCCF_Name() {
		return _ccf_name;
	}

	public String getCcf_result() {
		return _ccf_result;
	}

	public void setCcf_result(String ccf_result) {
		this._ccf_result = ccf_result;
	}

	boolean valid = true;

	Button output_button;
	Label output_uri;
	Label output_path;

	List<Composite> composite_list = new ArrayList<Composite>();
	public Composite composite_ccf;
	DefineSet _defset = null;

	public boolean isValid() {
		return valid;
	}

	public String get_att_uri() {
		return _att_uri;
	}

	public void set_att_uri(String _att_uri) {
		this._att_uri = _att_uri;
	}

	public String get_att_xpt() {
		return _att_xpt;
	}

	public void set_att_xpt(String _att_xpt) {
		this._att_xpt = _att_xpt;
	}

	private String _att_uri;
	private String _att_xpt;
	private String _postfix = "";

	public String get_postfix() {
		return _postfix;
	}

	public void set_postfix(String _postfix) {
		this._postfix = _postfix;
	}

	Integer minvalue = null;
	Integer maxvalue = null;

	DocumentBuilder builder;

	Map<String, String> defmap = new HashMap<String, String>();
	public Composite composite_by_type;
	public Text ccf_text_text;
	public Text ccf_float_text;
	public Text ccf_boolean_text;
	public Text ccf_expression_text;
	public Text ccf_integer_text;
	public Button ccf_boolean_chkbtn;
	public Composite composite_formType;
	public Label lbl_formType;
	public Composite composite_outLocation;
	public Composite composite_outValue;
	public Label lbl_outValue;
	public Label lblNewLabel;
	public Label lblNewLabel_1;
	public Label lbl_formTypeTitle;
	public Label lblReuslt;
	public Label lbl_confname;
	public Button btnEnabel;
	public Composite composite_main;
	private Composite composite_forNest;
	private Composite composite_top;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param cf
	 */
	public CCF_ConfigurationComposite(Composite parent, int style) {
		super(parent, SWT.NONE);

		if (root == null) {
			root = parent;
		}
		setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		
		composite_top = new Composite(this, SWT.BORDER);
		composite_top.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

		composite_main = new Composite(composite_top, SWT.NONE);
		composite_main.setLayout(new GridLayout(2, false));

		composite_formType = new Composite(composite_main, SWT.NONE);
		composite_formType.setLayout(new GridLayout(4, false));

		lbl_confname = new Label(composite_formType, SWT.BORDER);
		lbl_confname.setText("*confname*");

		lbl_formTypeTitle = new Label(composite_formType, SWT.NONE);
		lbl_formTypeTitle.setText("FormType");

		lbl_formType = new Label(composite_formType, SWT.BORDER
				| SWT.SHADOW_NONE);
		lbl_formType.setText("*formType*");

		output_button = new Button(composite_formType, SWT.NONE);
		output_button.setSize(116, 25);
		output_button.addSelectionListener(this);
		output_button.setText("Configurre");

		composite_outValue = new Composite(composite_main, SWT.NONE);
		composite_outValue.setLayout(new GridLayout(3, false));

		lblReuslt = new Label(composite_outValue, SWT.NONE);
		lblReuslt.setText("Reuslt");

		lbl_outValue = new Label(composite_outValue, SWT.BORDER);
		GridData gd_lbl_outValue = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lbl_outValue.widthHint = 307;
		lbl_outValue.setLayoutData(gd_lbl_outValue);
		lbl_outValue.setBounds(0, 0, 61, 15);
		lbl_outValue.setText("*outValue*");

		btnEnabel = new Button(composite_outValue, SWT.CHECK);
		btnEnabel.addSelectionListener(this);
		btnEnabel.setText("enable");

		composite_ccf = new Composite(composite_main, SWT.BORDER);
		composite_ccf.setLayout(new GridLayout(2, false));

		lbl_name = new Label(composite_ccf, SWT.NONE);
		lbl_name.setText("*name*");

		composite_by_type = new Composite(composite_ccf, SWT.BORDER);
		composite_by_type.setLayout(new StackLayout());

		ccf_select_combo = new Combo(composite_by_type, SWT.NONE);

		ccf_text_text = new Text(composite_by_type, SWT.BORDER);
		ccf_text_text.setText("*text*");

		ccf_float_text = new Text(composite_by_type, SWT.BORDER);
		ccf_float_text.setText("0.0");

		ccf_boolean_text = new Text(composite_by_type, SWT.BORDER);
		ccf_boolean_text.setText("true");

		ccf_expression_text = new Text(composite_by_type, SWT.BORDER);
		ccf_expression_text.setEditable(false);
		ccf_expression_text.setText("0+0");

		ccf_integer_text = new Text(composite_by_type, SWT.BORDER);
		ccf_integer_text.setText("0");

		ccf_boolean_chkbtn = new Button(composite_by_type, SWT.CHECK);
		ccf_boolean_chkbtn.addSelectionListener(this);
		ccf_boolean_chkbtn.setText("false");
		System.out.println("In Constructor: combo.size="
				+ ccf_select_combo.getItemCount());

		composite_outLocation = new Composite(composite_main, SWT.BORDER);
		composite_outLocation.setLayout(new GridLayout(2, false));

		lblNewLabel = new Label(composite_outLocation, SWT.NONE);
		lblNewLabel.setText("URI");
		output_uri = new Label(composite_outLocation, SWT.NONE);
		GridData gd_output_uri = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_output_uri.widthHint = 488;
		output_uri.setLayoutData(gd_output_uri);
		output_uri.setSize(31, 15);
		output_uri.setText("*file*");

		lblNewLabel_1 = new Label(composite_outLocation, SWT.NONE);
		lblNewLabel_1.setText("XPath");
		output_path = new Label(composite_outLocation, SWT.NONE);
		GridData gd_output_path = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_output_path.widthHint = 374;
		output_path.setLayoutData(gd_output_path);
		output_path.setSize(47, 15);
		output_path.setText("*xpath*");

		composite_forNest = new Composite(composite_top, SWT.NONE);
		composite_forNest.setLayout(new BorderLayout(100, 0));
//		
		root.pack();
		root.layout();
//		
//		root.getParent().getParent().pack();
//		root.getParent().getParent().layout();

	}

	public void reSetInput() {
		setInput(_cf, _defset);
	}

	public void setInput(final Configuration cf, DefineSet defset) {

		System.out.println("SelectComposite:setInput");

		String defstrs = "";
		Object result = "";

		_cf = cf;
		_ccf_name = cf.getName();

		_defset = defset;

		_att_uri = cf.getUri();
		_att_xpt = cf.getPath();

		if (this._att_uri != null) {
			output_uri.setText(this._att_uri);
		}
		if (this._att_uri != null) {
			output_path.setText(this._att_xpt);
		}

		minvalue = cf.getMin();
		maxvalue = cf.getMax();

		// DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			if (cf.getUri() != null) {

				// Output要素の既存値を取り出す
				Document docCf = builder
						.parse(new FileInputStream(cf.getUri()));
				XPathFactory factoryCf = XPathFactory.newInstance();
				XPath xpathCf = factoryCf.newXPath();
				String expressionCf = cf.getPath();
				System.out.println("[Concifugration Outpur XPath]"
						+ expressionCf);

				Node nodeCf = (Node) xpathCf.compile(expressionCf).evaluate(
						docCf, XPathConstants.NODE);
				String valueCf = nodeCf.getNodeValue();

				System.out.println("valueCf = " + valueCf);
				_ccf_result = valueCf;
			}

			defstrs = makeDefStr(defset, builder);

		} catch (ParserConfigurationException | SAXException | IOException
				| XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("****defstrs = " + defstrs);

		this.btnEnabel.setText("enable");
		this.btnEnabel.setSelection(true);
		valid = true;

		lbl_confname.setText(cf.getName());

		lbl_formType.setText(cf.getFormType().value());

		if (cf.getFormType() == FormType.TEXT) {
			TextConfiguration(cf);
		} else if (cf.getFormType() == FormType.HEX_INTEGER) {
			HexIntegerConfiguration(cf);
		} else if (cf.getFormType() == FormType.INTEGER) {
			IntegerConfiguratin(cf);
		} else if (cf.getFormType() == FormType.FLOAT) {
			FloatConfiguratin(cf);
		} else if (cf.getFormType() == FormType.BOOL) {
			BoolConfiguration(cf);
		} else if (cf.getFormType() == FormType.EXPRESSION) {

			System.out.println("*** FormType : EXPRESSION");

			StackLayout lot = (StackLayout) composite_by_type.getLayout();
			lot.topControl = ccf_expression_text;
			composite_by_type.layout();

			Expression expression = cf.getExpression();
			String exp = String.format("%s", expression.getExp());
			System.out.println("expression string = " + exp);

			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");

			try {
				// using Script Engine(Javascript)
				// String script = "3 + 4";
				// String script = defstrs + exp; result = engine.eval(script);

				// using Xpath
				String exp1 = exp;
				for (Iterator it = defmap.entrySet().iterator(); it.hasNext();) {
					Map.Entry entry = (Map.Entry) it.next();
					String keystr = (String) entry.getKey();
					String valuestr = (String) entry.getValue();
					System.out.println("key=" + keystr + ", value=" + valuestr);

					exp1 = exp1.replace(keystr, valuestr);
					System.out.println("exp1=" + exp1);
				}

				String uri = cf.getUri();
				System.out.println("uri=" + uri);
				Document doc;
				try {
					doc = builder.parse(new FileInputStream(uri));
					XPathFactory factory = XPathFactory.newInstance();
					XPath xpath = factory.newXPath();
					Object n = xpath.compile(exp1).evaluate(doc,
							XPathConstants.NUMBER);
					result = n;

				} catch (SAXException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("result = " + result);

			} catch (XPathExpressionException exception) {
				exception.printStackTrace();
			}

			lbl_name.setText(cf.getName());

			// ccf_expression_text.setText(result.toString());
			ccf_expression_text.setText(exp);

			_ccf_result = result.toString();

			// composite_ccf.setLayoutData(composite_ccf_exp);

		} else if (cf.getFormType() == FormType.SELECT) {

			SelectConfiguration(cf);

		} else {
			System.out.println("*** This FormType is not supported yet");
		}

		lbl_outValue.setText(_ccf_result);
		composite_by_type.layout();
		composite_ccf.layout();
		composite_main.layout();
		composite_main.getParent().pack();
		
		composite_main.pack();

		System.out
				.println(" End setInput() layout executed ---------------------------------------");

	}

	private String makeDefStr(DefineSet defset, DocumentBuilder builder)
			throws FileNotFoundException, XPathExpressionException {
		String defstrs = "";

		if (defset != null) {

			// ConfigurationSetのDefineSetの処理
			for (Def def : defset.getDef()) {
				String uri = def.getUri();
				Document doc = null;
				try {
					doc = builder.parse(new FileInputStream(uri));
				} catch (SAXException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Using 'javax.xml.xpath.XPath'.
				{
					System.out.println("#1 Using 'javax.xml.xpath.XPath'.");
					XPathFactory factory = XPathFactory.newInstance();
					XPath xpath = factory.newXPath();

					// Expression for getting single node.
					String varName = def.getName();
					String path = def.getPath(); // XPath String
					String expression1 = path;

					// Getting single node.
					System.out.println("[XPath]" + expression1);

					Node node = (Node) xpath.compile(expression1).evaluate(doc,
							XPathConstants.NODE);

					System.out.println("    NodeName=" + node.getNodeName());
					System.out.println("    NodeName=" + node.getNodeValue());

					NamedNodeMap map = node.getAttributes();

					String defstr = String.format("%s = %s;\n", varName,
							node.getNodeValue());

					System.out.println("defstr = " + defstr);

					defmap.put(varName, node.getNodeValue());
					System.out.println("   --- defmap.key=" + varName);
					System.out.println("   --- defmap.value="
							+ node.getNodeValue());

					defstrs += defstr;
				}
			}
		}
		return defstrs;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == btnEnabel) {
			do_btnEnabel_widgetSelected(e);
		}
		if (e.getSource() == ccf_boolean_chkbtn) {
			do_ccf_boolean_chkbtn_widgetSelected(e);
		}
		if (e.getSource() == output_button) {
			do_output_button_widgetSelected(e);
		}
	}

	protected void do_output_button_widgetSelected(SelectionEvent ev) {

		System.out.println("Configure Button Pushed!");

		if (valid) {
			System.out.println("data is Valid");
			if (_att_uri == null) {
				System.out.println("uri is null");
				return;
			}
			File aFile = new File(_att_uri); // for output file

			CCF_Action act = new CCF_Action(_ccf_result, _att_uri, _att_xpt);
			act.run();
			reSetInput();

		} else {
			MessageDialog dialog = new MessageDialog(getShell(),
					"CCF Sample Error Dialog", null, "Input value error",
					MessageDialog.QUESTION, new String[] { "OK" }, 1);
			int ret = dialog.open();

		}

	}

	protected void do_ccf_boolean_chkbtn_widgetSelected(SelectionEvent e) {
		Button btn = (Button) e.getSource();
		if (btn.getSelection() == true) {
			System.out.println("selection = true");
			btn.setText("ture");
			_ccf_result = "true";
		} else {
			System.out.println("selection = false");
			btn.setText("false");
			_ccf_result = "false";
		}
		lbl_outValue.setText(_ccf_result);
	}

	String TextConfiguration(Configuration cf) {
		System.out.println("*** FormType : TEXT");

		StackLayout lot = (StackLayout) composite_by_type.getLayout();
		lot.topControl = ccf_text_text;
		lbl_name.setText(cf.getName());

		ccf_text_text.setText(_ccf_result);
		// ccf_result = ccf_text_text.getText();

		ccf_text_text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				_ccf_result = ccf_text_text.getText();
				lbl_outValue.setText(_ccf_result);
			}
		});

		composite_by_type.layout();
		return "";
	}

	String IntegerConfiguratin(Configuration cf) {

		System.out.println("*** FormType : INTEGER");

		StackLayout lot = (StackLayout) composite_by_type.getLayout();
		lot.topControl = ccf_integer_text;
		lbl_name.setText(cf.getName());

		// ccf_result = ccf_integer_text.getText();
		ccf_integer_text.setText(_ccf_result);

		System.out.println("(default)  ccf_result = " + _ccf_result);

		CCF_IntegerValidator validator = new CCF_IntegerValidator(minvalue,
				maxvalue);
		String v = validator.isValid(_ccf_result);
		if (v != null) {
			valid = false;
		}
		lbl_outValue.setText(_ccf_result);

		ccf_integer_text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {

				String input = ccf_integer_text.getText();

				CCF_IntegerValidator validator = new CCF_IntegerValidator(
						minvalue, maxvalue);
				String v = validator.isValid(input);

				if (v == null) {
					_ccf_result = input;
					valid = true;

					lbl_outValue.setForeground(SWTResourceManager
							.getColor(SWT.COLOR_BLACK));
				} else {
					_ccf_result = v;
					valid = false;

					lbl_outValue.setForeground(SWTResourceManager
							.getColor(SWT.COLOR_RED));
				}

				lbl_outValue.setText(_ccf_result);

			}
		});

		composite_by_type.layout();
		return v;
	}

	String FloatConfiguratin(Configuration cf) {

		System.out.println("*** FormType : FLOAT");

		StackLayout lot = (StackLayout) composite_by_type.getLayout();
		lot.topControl = ccf_float_text;
		lbl_name.setText(cf.getName());

		ccf_float_text.setText(_ccf_result);

		Float fmin = null;
		Float fmax = null;
		if (minvalue != null) {
			fmin = minvalue.floatValue();
		}
		if (maxvalue != null) {
			fmax = maxvalue.floatValue();
		}

		CCF_FloatValidator validator = new CCF_FloatValidator(fmin, fmax);
		String v = validator.isValid(_ccf_result);
		if (v != null) {
			valid = false;
		}
		lbl_outValue.setText(_ccf_result);

		ccf_float_text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {

				String input = ccf_float_text.getText();

				Float fmin = null;
				Float fmax = null;

				if (minvalue != null) {
					fmin = minvalue.floatValue();
				}
				if (maxvalue != null) {
					fmax = maxvalue.floatValue();
				}

				CCF_FloatValidator validator = new CCF_FloatValidator(fmin,
						fmax);
				String v = validator.isValid(input);

				if (v == null) {
					_ccf_result = input;
					valid = true;

					lbl_outValue.setForeground(SWTResourceManager
							.getColor(SWT.COLOR_BLACK));
				} else {
					_ccf_result = v;
					valid = false;

					lbl_outValue.setForeground(SWTResourceManager
							.getColor(SWT.COLOR_RED));
				}

				lbl_outValue.setText(_ccf_result);

			}
		});

		composite_by_type.layout();
		
		return v;
	}

	String HexIntegerConfiguration(Configuration cf) {
		System.out.println("*** FormType : HEX_INTEGER");

		StackLayout lot = (StackLayout) composite_by_type.getLayout();
		lot.topControl = ccf_integer_text;
		lbl_name.setText(cf.getName());

		// ccf_result = ccf_integer_text.getText();
		ccf_integer_text.setText(Integer.toHexString(Integer
				.valueOf(_ccf_result)));

		CCF_HexIntegerValidator validator = new CCF_HexIntegerValidator(
				minvalue, maxvalue);
		String v = validator.isValid(_ccf_result);
		if (v != null) {
			valid = false;
		}
		lbl_outValue.setText(_ccf_result);

		ccf_integer_text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {

				String input = ccf_integer_text.getText();

				CCF_HexIntegerValidator validator = new CCF_HexIntegerValidator(
						minvalue, maxvalue);
				String v = validator.isValid(input);

				if (v == null) {
					_ccf_result = "" + Integer.valueOf(input, 16);
					valid = true;

					lbl_outValue.setForeground(SWTResourceManager
							.getColor(SWT.COLOR_BLACK));
				} else {
					_ccf_result = v;
					valid = false;

					lbl_outValue.setForeground(SWTResourceManager
							.getColor(SWT.COLOR_RED));
				}

				lbl_outValue.setText(_ccf_result);

			}
		});

		composite_by_type.layout();
		return "";
	}

	String BoolConfiguration(Configuration cf) {
		System.out.println("*** FormType : BOOL");

		StackLayout lot = (StackLayout) composite_by_type.getLayout();
		lot.topControl = ccf_boolean_chkbtn;
		lbl_name.setText(cf.getName());

		_ccf_result = ccf_boolean_chkbtn.getText();

		composite_by_type.layout();
		return "";
	}

	String SelectConfiguration(final Configuration cf) {

		System.out.println("*** FormType : SELECT");

		StackLayout lot = (StackLayout) composite_by_type.getLayout();
		lot.topControl = ccf_select_combo;
		composite_by_type.layout();

		lbl_name.setText(cf.getName());
		ccf_select_combo.removeAll();
		for (Item item : cf.getItem()) {
			System.out.println("combo add item =  " + item.getValue());
			ccf_select_combo.add(item.getValue().toString());
		}
		ccf_select_combo.setText(ccf_select_combo.getItem(0));
		ccf_select_combo.select(_selectedIdx);
		ccf_select_combo.redraw();

		String item = ccf_select_combo.getItem(_selectedIdx);
		_ccf_result = item;
		// _ccf_result = "*select item*";

		lbl_outValue.setText(_ccf_result);

		ccf_select_combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				Combo combo = (Combo) e.getSource();
				int idx = combo.getSelectionIndex();
				_selectedIdx = idx;
				Item itm = cf.getItem().get(idx);

				System.out.println("Select item index=" + idx);

				Composite ccf_base = combo.getParent().getParent();

				for (Control child : ccf_base.getChildren()) {
					if (child instanceof CCF_ConfigurationComposite) {
						if (child != combo.getParent()) {
							child.dispose();
						}
					}
				}

				for (Control ctrl : composite_forNest.getChildren()) {
					ctrl.dispose();
				}
//				composite_forNest.pack();
//				composite_forNest.getParent().pack();
				
				for (Configuration cf3 : itm.getConfiguration()) {

					CCF_ConfigurationComposite ccfc3 = new CCF_ConfigurationComposite(
							composite_forNest, SWT.NONE);

					ccfc3.setLayout(new GridLayout(2, true));
					ccfc3.setInput(cf3, _defset);

					// composite_forNest.setSize(1000, 200);
					//composite_forNest.layout();
//					root.getParent().pack();
//					root.getParent().layout();

					System.out.println("Nested Configuration creteated");
				}

				// composite_ccf.setLayoutData(composite_ccf_select);

				String item = ccf_select_combo.getItem(idx);
				_ccf_result = item;
				lbl_outValue.setText(_ccf_result);

				System.out.println("Selected Combo item=" + item);

				ccf_base.layout();
				ccf_base.getParent().layout();
				
				Composite parent = root.getParent();
				if (parent instanceof ScrolledComposite) {
					Point computeSize = root.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					ScrolledComposite scrolledComposite = (ScrolledComposite) parent;
					scrolledComposite.setContent(root);
					scrolledComposite.setMinHeight(computeSize.y);
				}
				parent.layout();
			}

		});

		System.out.println("In SetInput: combo.size="
				+ ccf_select_combo.getItemCount());

		composite_by_type.layout();

		return "";
	}

	String ExpressionConfiguration() {
		return "";
	}

	protected void do_btnEnabel_widgetSelected(SelectionEvent e) {
		Button btn = (Button) e.getSource();
		if (btn.getSelection() == true) {
			System.out.println("enable = true");
			btn.setText("enable");
			valid = true;
			// composite_ccf.setEnabled(true);

		} else {
			System.out.println("enable = false");
			btn.setText("disable");
			valid = false;
			// composite_ccf.setVisible(false);
		}
	}
}
