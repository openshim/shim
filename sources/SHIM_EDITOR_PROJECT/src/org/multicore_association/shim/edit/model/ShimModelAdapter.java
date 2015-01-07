/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.multicore_association.shim.api.ObjectFactory;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.xml.sax.SAXException;

import com.sun.xml.internal.ws.util.StringUtils;

public class ShimModelAdapter {

	private static final Logger log = ShimLoggerUtil
			.getLogger(ShimModelAdapter.class);

	public static final String DOES_NOT_HAVE_A_NAME_FIELD = "-";

	public static final String CLASS_NAME_INTEGER_WRAPPER = Integer.class
			.getName();
	public static final String CLASS_NAME_LONG_WRAPPER = Long.class.getName();
	public static final String CLASS_NAME_FLOAT_WRAPPER = Float.class.getName();
	public static final String CLASS_NAME_INTEGER_PRIMITIVE = "int";
	public static final String CLASS_NAME_LONG_PRIMITIVE = "long";
	public static final String CLASS_NAME_FLOAT_PRIMITIVE = "float";
	public static final String CLASS_NAME_OBJECT = Object.class.getName(); // Added.
																			// 2014/06/17

	/**
	 * Parses the "shim-schema.xsd" as a schema and returns it as a Schema.
	 * 
	 * @return New Schema from parsing schema.
	 * @throws SAXException
	 *             If a SAX error occurs during parsing.
	 */
	public static Schema getShimSchema() throws SAXException {
		SchemaFactory scf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		schema = scf.newSchema(new File("schema/shim.xsd"));
		return schema;
	}

	/**
	 * Returns a new JAXBContext instance of SHIM Data, whose root element is
	 * SystemConfiguration.
	 * 
	 * @return the new JAXBContext instance
	 * @throws JAXBException
	 */
	public static JAXBContext getJAXBContext() throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(SystemConfiguration.class);
		return jc;
	}

	private static Class<?> rootElement = null;
	private static boolean isSerched = false;
	private static HashMap<String, ClassInformation> classMap = new HashMap<String, ClassInformation>();

	/**
	 * Returns the ClassInformation of the specified class.
	 * 
	 * @param className
	 *            the name of class to return its infomation
	 * @return the ClassInformation of the specified class
	 */
	private static ClassInformation getClassInformation(String className) {
		ClassInformation cInfo = classMap.get(className);
		if (classMap.isEmpty() || cInfo == null) {
			Class<?> rootObj = getRootElement();
			getInfomation(rootObj, ClassInformation.REGEX);
			cInfo = classMap.get(className);
		}
		return cInfo;
	}

	/**
	 * An information of the class such as whether has a child element, or
	 * whether has a super class, or which class is parent.
	 */
	private static class ClassInformation {
		private ClassInformation() {
		}

		private HashSet<String> histories = new HashSet<String>();
		private HashSet<Class<?>> children = new HashSet<Class<?>>();
		@SuppressWarnings("unused")
		private boolean hasChildren = false;
		private boolean hasSuper = false;
		private static String REGEX = "#";
		private HashMap<String, ParentInformation> parentInfornations = new HashMap<String, ParentInformation>();

		/**
		 * An information of the parent element class.
		 */
		static class ParentInformation {
			private ParentInformation() {
			}

			private boolean hasSiblings = false;
			private Class<?> parent = null;
		}

		/**
		 * Returns whether the list type field of the specified parent contains
		 * the element.
		 * 
		 * @param parentName
		 *            the parent
		 * @return Returns true if the list type field of the specified parent
		 *         contains the element, and false otherwise.
		 */
		private boolean hasSiblings(String parentName) {
			boolean result = true;
			for (String history : histories) {
				if (history.endsWith(REGEX + parentName + REGEX)) {
					if (!parentInfornations.get(history).hasSiblings) {
						result = false;
					}
				}
			}
			return result;
		}

		/**
		 * Returns the ParentInformation with the specified history.
		 * 
		 * @param history
		 *            history
		 * @return ParentInformation
		 */
		private ParentInformation getParentInfo(String history) {
			ParentInformation parentInfo = parentInfornations.get(history);
			if (parentInfo == null) {
				parentInfo = new ParentInformation();
				parentInfornations.put(history, parentInfo);
			}
			return parentInfo;
		}

		/**
		 * Sets the information to the ParentInformation with the specified
		 * history.
		 * 
		 * @param history
		 *            history
		 * @param hasSiblings
		 *            true if the list type field of the specified parent
		 *            contains the element, and false otherwise.
		 * @param parent
		 *            the class of parent
		 */
		private void setParentInfo(String history, boolean hasSiblings,
				Class<?> parent) {
			ParentInformation parentInfo = getParentInfo(history);
			parentInfo.hasSiblings = hasSiblings;
			parentInfo.parent = parent;
		}
	}

	/**
	 * Returns the XmlRootElement defined in schema.
	 * 
	 * @return the XmlRootElement defined in schema
	 */
	public static Class<?> getRootElement() {
		if (!isSerched) {
			isSerched = true;
			ObjectFactory factory = new ObjectFactory();
			Class<? extends ObjectFactory> fc = factory.getClass();
			Method[] methods = fc.getMethods();

			Object oFactory;
			try {
				for (Method mtd : methods) {
					if (mtd.getParameterTypes().length == 0) {
						oFactory = mtd.invoke(factory, new Object[0]);
						XmlRootElement re = oFactory.getClass().getAnnotation(
								XmlRootElement.class);
						if (re != null) {
							rootElement = oFactory.getClass();
							return rootElement;
						}
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				log.log(Level.SEVERE,
						"[ERROR] There is no class having '@XmlRootElement'.",
						e);
			}
			return null;
		} else {
			return rootElement;
		}
	}

	/**
	 * Returns whether the list type field of the specified parent contains the
	 * element.
	 * 
	 * @param obj
	 *            the element
	 * @param parentName
	 *            the name of parent
	 * @return Returns true if the list type field of the specified parent
	 *         contains the element, and false otherwise.
	 */
	public static boolean hasSiblings(Object obj, String parentName) {
		// It isn't XmlRootElement.
		if (isRootElement(obj)) {
			return false;
		}

		// It has Siblings.
		String objName = obj.getClass().getSimpleName();
		ClassInformation cInfo = getClassInformation(objName);
		log.finest("Does " + objName + " have Siblings? : "
				+ cInfo.hasSiblings(parentName));
		return cInfo.hasSiblings(parentName);
	}

	/**
	 * Returns whether the specified element is root element or not.
	 * 
	 * @param obj
	 *            the element
	 * @return Returns true if the specified element is root element, and false
	 *         otherwise.
	 */
	private static boolean isRootElement(Object obj) {
		Class<?> rootObj = getRootElement();
		if (rootObj != null && obj.getClass().equals(rootObj)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns whether the element has the reference to itself or not.
	 * 
	 * @param obj
	 *            Must be a member of the Shim API.
	 * @return Returns true if the element has the reference to itself, and
	 *         false otherwise.
	 */
	public static boolean isSelfReferencing(Object obj) {
		String selfField = obj.getClass().getSimpleName();
		boolean isSelfReferencing = false;

		ClassInformation clsInfo = getClassInformation(selfField);
		if (clsInfo != null && clsInfo.children.contains(obj.getClass())) {
			selfField = StringUtils.decapitalize(selfField);
			Object children = ShimModelAdapter.getValObject(obj, selfField);

			if (children instanceof List) {
				ArrayList<?> _children = ((ArrayList<?>) children);
				if (_children.size() > 0) {
					isSelfReferencing = true;
				}
			} else {
				if (children != null) {
					isSelfReferencing = true;
				}
			}
		}
		return isSelfReferencing;
	}

	/**
	 * Returns the child element's classes of the specified element.
	 * 
	 * @param obj
	 *            Must be a member of the Shim API.
	 * @return the child element's classes
	 */
	public static ArrayList<Class<?>> getChildren(Object obj) {
		Class<?> cls = obj.getClass();
		HashSet<Class<?>> _children = getChildren(cls);

		ArrayList<Class<?>> children = new ArrayList<Class<?>>();
		for (Class<?> child : _children) {
			children.add(child);
		}
		Collections.sort(children, new ChildrenComparator());
		return children;
	}

	/**
	 * A Comparator implementation for child classes.
	 */
	private static class ChildrenComparator implements Comparator<Class<?>> {

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final Class<?> c1, final Class<?> c2) {
			String c1Name = c1.getSimpleName();
			String c2Name = c2.getSimpleName();
			return c1Name.compareTo(c2Name);
		}
	}

	/**
	 * Returns the child element's classes of the specified element.
	 * 
	 * @param cls
	 *            the class of the element
	 * @return the child element's classes of the specified element
	 */
	private static HashSet<Class<?>> getChildren(Class<?> cls) {
		HashSet<Class<?>> children = new HashSet<Class<?>>();
		String objName = cls.getSimpleName();
		ClassInformation info = getClassInformation(objName);
		if (info != null) {
			children = info.children;
			if (info.hasSuper) {
				Class<?> superClass = cls.getSuperclass();
				log.finest("superClass:" + superClass.toString());
				children.addAll(getChildren(superClass));
			}
		}
		return children;
	}

	/**
	 * Investigates the connection of the schema from root object. And record to
	 * the map that is provided.
	 * 
	 * @param rootObj
	 *            Start searching.
	 * @param history
	 *            The way to trace back to root Element.
	 * @see #classMap
	 */
	private static void getInfomation(Class<?> rootObj, String history) {

		ObjectFactory factory = new ObjectFactory();
		Class<? extends ObjectFactory> fc = factory.getClass();

		XmlType type = rootObj.getAnnotation(XmlType.class);
		String[] props = type.propOrder();
		String rootObjName = rootObj.getSimpleName();
		ClassInformation rootClsInfo = initClassInformation(rootObjName);
		Class<?>[] types = new Class[] {};

		String object_calss_name = new Object().getClass().getName();
		String err_msg = null;

		rootClsInfo.histories.add(history);

		StringBuffer _historyBuffer = new StringBuffer();
		_historyBuffer.append(history);
		_historyBuffer.append(rootObjName);
		_historyBuffer.append(ClassInformation.REGEX);
		String history_of_child = _historyBuffer.toString();

		for (String prop : props) {
			try {
				if (prop.isEmpty()) {
					Class<?> su = rootObj.getSuperclass();
					if (!su.getName().equals(object_calss_name)) {
						rootClsInfo.hasSuper = true;
						getInfomation(su, history_of_child);
					} else {
						rootClsInfo.hasChildren = false;
					}
					break;
				}

				Field field = rootObj.getDeclaredField(prop);

				String methodName = field.getType().getSimpleName();
				rootClsInfo.hasChildren = true;
				boolean isList = false;
				err_msg = methodName;
				if (methodName.equals("List")) {
					// multivalued property
					isList = true;

					String genericName0[] = field.toGenericString().split("<");
					String genericName1[] = genericName0[1].split(">");
					String genericName2[] = genericName1[0].split("\\.");
					methodName = genericName2[genericName2.length - 1];
				}

				Method method = fc.getMethod("create" + methodName, types);
				Object result = method.invoke(factory, new Object[0]);
				Class<?> child = result.getClass();
				rootClsInfo.children.add(child);
				if (rootObjName.equals(methodName)) {
					// self-referencing
					rootClsInfo.histories.add(history_of_child);
					rootClsInfo.setParentInfo(history_of_child, true, rootObj);
					continue;

				} else {

					ClassInformation cInfo = initClassInformation(methodName);
					if (!isList) {
						cInfo.setParentInfo(history_of_child, false, rootObj);

					} else {
						cInfo.setParentInfo(history_of_child, true, rootObj);
					}
					getInfomation(child, history_of_child);
				}

			} catch (NoSuchMethodException ne) {
				log.info("The object isn't included in SHIM API. ::" + err_msg
						+ ":" + prop + ":" + rootObjName);
				continue;
			} catch (SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchFieldException e) {
				log.log(Level.WARNING, "Fail to get infomation.", e);
			}
		}
	}

	/**
	 * Creates and initializes the ClassInformation with the specified element.
	 * 
	 * @param objName
	 *            the element
	 * @return the ClassInformation with the specified element
	 */
	private static ClassInformation initClassInformation(String objName) {
		ClassInformation cls = classMap.get(objName);
		if (cls == null) {
			classMap.put(objName, new ClassInformation());
		}
		return classMap.get(objName);
	}

	private static List<ShimObject> objectsList = new ArrayList<ShimObject>();
	private static HashSet<String> objectSet = null; // To eliminate duplicates.

	/**
	 * Returns the ShimObject list.
	 * 
	 * @param cls
	 *            the class of the root element
	 * @param rootInstance
	 *            the instance of the root element
	 * @return {@link #objectsList}
	 */
	public static List<ShimObject> getObjectsList(Class<?> cls,
			Object rootInstance) {
		objectsList = new ArrayList<ShimObject>();
		objectSet = new HashSet<String>();

		String className = cls.getSimpleName();
		getObjectsList(className, rootInstance);

		log.finest("========== OBJECT LIST ==========");
		for (ShimObject obj : objectsList) {
			log.finest("ParentName:" + obj.getParentName() + " ; "
					+ obj.getObj().getClass().getSimpleName() + ":"
					+ obj.getObj().toString() + " ; ");
		}
		log.finest("========== ========== ==========");

		objectSet = null;
		return objectsList;
	}

	/**
	 * Creates the ShimObject list.
	 * 
	 * @param className
	 *            the class name of the root element
	 * @param rootInstance
	 *            the instance of the root element
	 */
	private static void getObjectsList(String className, Object rootInstance) {
		log.finest("getObjectsList(String=" + className + ", Object="
				+ rootInstance + "):");
		if (!isShimApiPackage(rootInstance)) {
			log.finest(rootInstance.getClass().getSimpleName()
					+ " class isn't member of the SHIM API.");
			return;
		}

		// It corresponds to the case it is not the root element. 2014/05/27
		HashSet<String> histories = getHistories(className, rootInstance);

		// Generating the XPath. 2014/05/27
		String xp = "";
		if (!isRootElement(rootInstance)) {
			xp = xp.concat("/");
		}
		xp = createXPath(xp, rootInstance);

		for (String history : histories) {
			log.finest("->history:" + history);
			String[] historyArray = history.split(ClassInformation.REGEX, 0);
			List<ShimObject> parentObjectList = new ArrayList<ShimObject>();
			String currentHistory = "";
			ShimObject so = new ShimObject(rootInstance, xp);
			parentObjectList.add(so);

			for (int i = 0; i < historyArray.length; i++) {
				currentHistory = currentHistory + historyArray[i]
						+ ClassInformation.REGEX;
				if (historyArray[i].length() > 0 && parentObjectList.size() > 0) {
					String childName = className;
					if (i < historyArray.length - 1) {
						childName = historyArray[i + 1];
					}
					log.finest("--> currentHistory:" + currentHistory);
					log.finest("--> childName:" + childName);

					parentObjectList = resolveSelfReferencing(parentObjectList,
							currentHistory, childName);

					ClassInformation info = getClassInformation(historyArray[i]);

					if (!info.hasSuper) {
						HashSet<Class<?>> children = info.children;
						parentObjectList = getNextObjectList(parentObjectList,
								childName, children, className);
					}
				}
			}
		}
	}

	/**
	 * Returns whether the specified object's class is in shim package or not.
	 * 
	 * @param obj
	 *            the object
	 * @return Returns true if the specified object's class is in shim package,
	 *         and false otherwise.
	 */
	private static boolean isShimApiPackage(Object obj) {
		Package basePackage = ObjectFactory.class.getPackage();
		Package insPackage = null;
		if (obj instanceof Class) {
			insPackage = ((Class<?>) obj).getPackage();
		} else {
			insPackage = obj.getClass().getPackage();
		}
		return (basePackage.equals(insPackage));
	}

	/**
	 * Returns histories.
	 * 
	 * @param objName
	 * @param rootInstance
	 * @return
	 */
	private static HashSet<String> getHistories(String objName,
			Object rootInstance) {
		HashSet<String> histories = new HashSet<String>();

		ClassInformation cInfo = getClassInformation(objName);
		String rootClassName = rootInstance.getClass().getSimpleName();

		if (cInfo == null) {
			log.finest(objName + " class doesn't exist in SHIM API.");

		} else if (objName.equals(rootClassName)) {
			log.finest("Root instance is same to " + objName + " class.");

		} else {
			if (isRootElement(rootInstance)) {
				histories = cInfo.histories;
			} else {
				HashSet<String> _histories = cInfo.histories;
				for (String _history : _histories) {
					int idx = _history.indexOf(ClassInformation.REGEX
							+ rootClassName + ClassInformation.REGEX);
					if (idx > 0) {
						String history = _history.substring(idx,
								_history.length());
						log.finest("new_history:" + history);
						histories.add(history);
					}
				}
			}
		}
		return histories;
	}

	/**
	 * @param parentObjectList
	 * @param currentHistory
	 * @param childName
	 * @return
	 */
	private static List<ShimObject> resolveSelfReferencing(
			List<ShimObject> parentObjectList, String currentHistory,
			String childName) {
		log.finest("resolveSelfReferencing:" + parentObjectList.toString());

		List<ShimObject> nestObjects = new ArrayList<ShimObject>();

		for (ShimObject _p : parentObjectList) {
			nestObjects.add(_p);
			nestObjects.addAll(getSelfReference(_p, currentHistory));
		}
		return nestObjects;
	}

	/**
	 * @param parentObject
	 * @param currentHistory
	 * @return
	 */
	private static List<ShimObject> getSelfReference(ShimObject parentObject,
			String currentHistory) {
		log.finest("getSelfReference:" + parentObject.toString());

		String parentName = parentObject.getObj().getClass().getSimpleName();
		ShimModelAdapter.ClassInformation.ParentInformation parentInfo = getClassInformation(
				parentName).getParentInfo(currentHistory);

		String nestParentName = "";
		if (parentInfo.parent != null) {
			nestParentName = parentInfo.parent.getSimpleName();
		}

		List<ShimObject> nestObjects = new ArrayList<ShimObject>();
		if (parentName.equals(nestParentName)) {
			List<ShimObject> childObject = getChildList(parentObject,
					parentName);
			for (ShimObject _o : childObject) {
				if (_o.getObj().getClass().getSimpleName().equals(parentName)) {
					nestObjects.add(_o);
					nestObjects.addAll(getSelfReference(_o, currentHistory));
				}
			}
		}
		return nestObjects;
	}

	/**
	 * @param parentObject
	 * @param childName
	 * @param children
	 * @param objName
	 * @return
	 */
	private static List<ShimObject> getNextObjectList(
			List<ShimObject> parentObject, String childName,
			HashSet<Class<?>> children, String objName) {

		List<ShimObject> nextObjectList = new ArrayList<ShimObject>();
		for (ShimObject _o : parentObject) {
			nextObjectList
					.addAll(getChildList(_o, childName, children, objName));
		}

		return nextObjectList;
	}

	/**
	 * @param parentObject
	 * @param childName
	 * @param children
	 * @param objName
	 * @return
	 */
	private static List<ShimObject> getChildList(ShimObject parentObject,
			String childName, HashSet<Class<?>> children, String objName) {
		log.finest("getChildList(Object, String, HashSet<Class<?>>, String):");
		log.finest("  parentObject:" + parentObject.getObj().toString()
				+ ", childName:" + childName + ", children:" + children
				+ ", objName:" + objName);

		List<ShimObject> childList = new ArrayList<ShimObject>();
		for (Class<?> cid : children) {
			log.finest("  cid:" + cid.toString());
			if (childName.equals(cid.getSimpleName())) {
				childList = getChildList(parentObject, childName);
				if (childList != null) {
					for (ShimObject _c : childList) {
						setObjectList(parentObject, _c, objName);
					}
				}
			}
		}
		return childList;
	}

	/**
	 * Returns the name field value.
	 * 
	 * @param object
	 *            the object to return the name field value
	 * @return Returns the name field value if the object has the name field,
	 *         and "-" otherwise.
	 */
	private static String getName(Object object) {
		log.finest("** getName(Object=" + object.toString() + "):");

		Object result = null;
		try {
			Class<?>[] types = new Class[] {};
			Method method = (object).getClass().getMethod("getName", types);
			result = method.invoke(object, new Object[0]);

		} catch (NoSuchMethodException ne) {
			log.finest("Object doesn't have name.");
			result = DOES_NOT_HAVE_A_NAME_FIELD;
		} catch (IllegalAccessException | IllegalArgumentException
				| SecurityException | InvocationTargetException e) {
		}
		return (String) result;
	}

	/**
	 * @param parent
	 * @param childName
	 * @return
	 */
	private static List<ShimObject> getChildList(ShimObject parent,
			String childName) {
		log.finest("* getChildList(Object=" + parent.toString() + ", String= "
				+ childName + "):");

		List<ShimObject> result = new ArrayList<ShimObject>();
		Object _result = getValObject(parent.getObj(), childName);
		String _xpath = parent.getXPath();

		if (_result == null) {
			return result;
		} else if (_result instanceof List) {
			ArrayList<?> resultList = ((ArrayList<?>) _result);
			if (resultList.size() == 0) {
				result = new ArrayList<ShimObject>();
			} else {
				for (Object rt : resultList) {
					String xpath = createXPath(_xpath, rt);
					ShimObject so = new ShimObject(rt, xpath);
					result.add(so);
				}
			}
		} else {
			String xpath = createXPath(_xpath, _result);
			ShimObject so = new ShimObject(_result, xpath);
			result.add(so);
		}
		return result;
	}

	/**
	 * This method gets the value from the object that is specified in the
	 * argument.
	 * 
	 * @param obj
	 *            Must be a member of the Shim API. Otherwise, those that
	 *            conform to the class definition of the Shim API.
	 * @param valueName
	 *            the field name to return
	 * @return the value of the specified field
	 */
	public static Object getValObject(Object obj, String valueName) {
		Object _result = null;
		try {
			String name = StringUtils.capitalize(valueName);
			Class<?>[] types = new Class[] {};
			Method method = (obj).getClass().getMethod("get" + name, types);
			_result = method.invoke(obj, new Object[0]);

			return _result;

		} catch (IllegalAccessException | IllegalArgumentException
				| SecurityException | InvocationTargetException
				| NoSuchMethodException e) {
			log.log(Level.WARNING, "reflection error.", e);
		}
		return _result;
	}

	/**
	 * @param parentObject
	 * @param _targetObject
	 * @param simpleClassName
	 *            the simple name of class (= Class#getSimpleName())
	 * @return
	 */
	private static boolean setObjectList(ShimObject parentObject,
			ShimObject _targetObject, String simpleClassName) {
		log.finest("**** Object add to List. ****");
		Object targetObject = _targetObject.getObj();

		boolean isAdded = false;
		if (simpleClassName.equals(targetObject.getClass().getSimpleName())) {

			String parentName = getName(parentObject.getObj());

			log.finest("    targetObject:" + targetObject.toString());
			log.finest("    parentName:" + parentName);
			if (parentName == null) {
				parentName = "";
			}

			String hashCodeStr = parentObject.getObj().hashCode() + ":"
					+ targetObject.hashCode();
			if (objectSet.add(hashCodeStr)) {
				String xpath = createXPath(parentObject.getXPath(),
						targetObject);
				ShimObject so = new ShimObject(targetObject, parentName, xpath);
				objectsList.add(so);
				isAdded = true;
			}
		}
		return isAdded;
	}

	/**
	 * Sets the value to the specified field of the instance.
	 * 
	 * @param obj
	 *            Must be a member of the Shim API. Otherwise, those that
	 *            conform to the class definition of the Shim API.
	 * @param valueName
	 *            the field name of the value
	 * @param val
	 * @param value
	 *            Use this parameter to set null.
	 * @return
	 */
	public static Object setValObject(Object obj, String valueName, Object val,
			Object... value) {
		Object _result = null;
		try {
			Class<? extends Object> valClass = val.getClass();
			Class<?>[] types = new Class[] { valClass };

			if (isRequired(obj, valueName)) {
				String valClassName = valClass.getName();
				if (valClassName.equals(CLASS_NAME_INTEGER_WRAPPER)) {
					types = new Class[] { int.class };
				} else if (valClassName.equals(CLASS_NAME_LONG_WRAPPER)) {
					types = new Class[] { long.class };
				} else if (valClassName.equals(CLASS_NAME_FLOAT_WRAPPER)) {
					types = new Class[] { float.class };
				}
			}

			String name = StringUtils.capitalize(valueName);
			Method method = (obj).getClass().getMethod("set" + name, types);
			if (value.length == 0) {
				_result = method.invoke(obj, new Object[] { val });
			} else {
				_result = method.invoke(obj, new Object[] { value[0] });
			}

			return _result;

		} catch (IllegalAccessException | IllegalArgumentException
				| SecurityException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}
		return _result;
	}

	/**
	 * Sets a null value to the specified field.
	 * 
	 * @param obj
	 *            the object which has the field to be set null
	 * @param valueName
	 *            the field name of tje value
	 */
	public static void setNull(Object obj, String valueName) {
		Field declaredField = ReflectionUtils.getDeclaredField(obj.getClass(), valueName);
		if (declaredField != null) {
			try {
				declaredField.setAccessible(true);
				declaredField.set(obj, null);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Returns whether the specified element is required to set to non-null
	 * value or not.
	 * 
	 * @param obj
	 *            element or attribute
	 * @param valueName
	 *            the field name
	 * @return Returns true if the specified element is required to set to
	 *         non-null value, and false otherwise.
	 */
	public static boolean isRequired(Object obj, String valueName) {
		boolean _isRequired = false;

		if (isShimApiPackage(obj)) {
			XmlAttribute attribute = getXmlAttribute(obj, valueName);
			if (attribute != null && attribute.required()) {
				_isRequired = true;

			} else {
				XmlElement element = getXmlElement(obj, valueName);
				if (element != null && element.required()) {
					_isRequired = true;
				}
			}
		}
		return _isRequired;
	}

	/**
	 * Returns the xml attribute.
	 * 
	 * @param obj
	 *            the element whitch has the specified attribute to return
	 * @param valueName
	 *            the field name
	 * @return the xml attribute
	 */
	private static XmlAttribute getXmlAttribute(Object obj, String valueName) {
		XmlAttribute attribute = null;
		try {
			Field field = null;
			if (obj instanceof Class) {
				field = ((Class<?>) obj).getDeclaredField(valueName);

			} else {
				field = obj.getClass().getDeclaredField(valueName);
			}
			attribute = field.getAnnotation(XmlAttribute.class);

		} catch (NoSuchFieldException e) {
			Class<?> superClazz = null;
			if (obj instanceof Class) {
				superClazz = ((Class<?>) obj).getSuperclass();
			} else {
				superClazz = obj.getClass().getSuperclass();
			}
			try {
				if (isShimApiPackage(superClazz)) {
					attribute = superClazz.getDeclaredField(valueName)
							.getAnnotation(XmlAttribute.class);
				}
			} catch (NoSuchFieldException e2) {
				log.info(valueName
						+ " field doesn't exist in "
						+ (obj instanceof Class ? ((Class<?>) obj)
								.getSimpleName() : obj.getClass()
								.getSimpleName()) + " class.");
			}
		}
		return attribute;
	}

	/**
	 * Returns the xml element.
	 * 
	 * @param obj
	 *            the element whitch has the specified element to return
	 * @param valueName
	 *            the field name
	 * @return the xml element
	 */
	private static XmlElement getXmlElement(Object obj, String valueName) {
		XmlElement element = null;
		try {
			Field field = null;
			if (obj instanceof Class) {
				field = ((Class<?>) obj).getDeclaredField(valueName);

			} else {
				field = obj.getClass().getDeclaredField(valueName);
			}
			element = field.getAnnotation(XmlElement.class);

		} catch (NoSuchFieldException e) {
			Class<?> superClazz = null;
			if (obj instanceof Class) {
				superClazz = ((Class<?>) obj).getSuperclass();
			} else {
				superClazz = obj.getClass().getSuperclass();
			}
			try {
				if (isShimApiPackage(superClazz)) {
					element = superClazz.getDeclaredField(valueName)
							.getAnnotation(XmlElement.class);
				}
			} catch (NoSuchFieldException e2) {
				log.info(valueName
						+ " field doesn't exist in "
						+ (obj instanceof Class ? ((Class<?>) obj)
								.getSimpleName() : obj.getClass()
								.getSimpleName()) + " class.");
			}
		}
		return element;
	}

	/**
	 * Creates XPath of the specified element.
	 * 
	 * @param oldXPath
	 *            the old XPath
	 * @param obj
	 *            the element
	 * @return XPath
	 */
	private static String createXPath(String oldXPath, Object obj) {
		String xPath = oldXPath;

		xPath = xPath.concat("/");
		String className = obj.getClass().getSimpleName();
		xPath = xPath.concat(className);

		Object nameFieldValue = getName(obj);
		if (nameFieldValue != null
				&& !nameFieldValue.equals(DOES_NOT_HAVE_A_NAME_FIELD)) {
			xPath = xPath + "[@name='" + nameFieldValue + "']";
		}
		return xPath;
	}

	/**
	 * Creates a new instance
	 * 
	 * @param cls
	 *            the class to create a new instance
	 * @return new instance
	 */
	public static Object getNewInstance(Class<?> cls) {
		Object obj = null;
		try {
			obj = cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Returns whether the element has a name attribute or not.
	 * 
	 * @param obj
	 *            the element
	 * @return Returns true if the element has a name attribute, and false
	 *         otherwise.
	 */
	public static boolean hasNameAttribute(Object obj) {
		boolean _hasName = false;

		if (isShimApiPackage(obj)) {
			XmlAttribute attribute = getXmlAttribute(obj,
					CommonConstants.FIELD_NAME);
			if (attribute != null) {
				_hasName = true;
			}
		}
		return _hasName;
	}
}
