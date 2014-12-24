/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;

/**
 * A control that is a set of hierarchical ComponentSet data.
 */
public class ShimComponentTree {

	private static final Logger log = ShimLoggerUtil
			.getLogger(ShimComponentTree.class);

	ComponentSet rootComponentSet = null;
	TreeNode root;

	private Map<String, TreeNode> masterComponentMap = new HashMap<String, TreeNode>();
	private Map<String, TreeNode> slaveComponentMap = new HashMap<String, TreeNode>();

	/**
	 * Constructs a new instance of Shim_ComponentTree
	 * 
	 * @param sys
	 */
	public ShimComponentTree(SystemConfiguration sys) {
		log.finest("Shim_ComponentTree Constructor");

		rootComponentSet = sys.getComponentSet();
		root = buildTree(null, rootComponentSet);
	}

	/**
	 * Retuns a root node of component tree.
	 * 
	 * @return the root node of component tree
	 */
	public TreeNode getRoot() {
		return root;
	}

	/**
	 * Builds component tree, and replaces a root node with a new on.
	 */
	public void reBuild() {
		masterComponentMap.clear();
		slaveComponentMap.clear();
		root = buildTree(null, rootComponentSet);
	}

	/**
	 * Builds component tree recursively.
	 * 
	 * @param parent_node
	 *            the ComponentSet's parent node
	 * @param cset
	 *            the ComponentSet instance
	 * @return the TreeNode instance of the ComponentSet
	 */
	protected TreeNode buildTree(TreeNode parent_node, ComponentSet cset) {

		TreeNode node = new TreeNode(parent_node, cset);

		log.finest("BuildTree CS node=" + node + ", node=" + node);

		for (ComponentSet cs : cset.getComponentSet()) {
			buildTree(node, cs);
		}
		for (MasterComponent mc : cset.getMasterComponent()) {
			buildTree(node, mc);
		}
		for (SlaveComponent slc : cset.getSlaveComponent()) {
			buildTree(node, slc);
		}

		return node;
	}

	/**
	 * Builds component tree recursively.
	 * 
	 * @param parent_node
	 *            the MasterComponent's parent node
	 * @param mc
	 *            the MasterComponent instance
	 * @return the TreeNode instance of the MasterComponent
	 */
	protected TreeNode buildTree(TreeNode parent_node, MasterComponent mc) {
		log.finest("BuildTree MC");
		TreeNode node = new TreeNode(parent_node, mc);
		masterComponentMap.put(mc.getId(), node);
		return node;
	}

	/**
	 * Builds component tree recursively.
	 * 
	 * @param parent_node
	 *            the SlaveComponent parent node
	 * @param slc
	 *            the SlaveComponent instance
	 * @return the TreeNode instance of the SlaveComponent
	 */
	protected TreeNode buildTree(TreeNode parent_node, SlaveComponent slc) {
		log.finest("BuildTree SLC");
		TreeNode node = new TreeNode(parent_node, slc);
		slaveComponentMap.put(slc.getId(), node);
		return node;
	}

	/**
	 * Returns the TreeNode instance of the ComponentSet.
	 * 
	 * @param cs
	 *            the ComponentSet instance
	 * @return the TreeNode instance of the ComponentSet
	 */
	protected TreeNode getNodeByCs(ComponentSet cs) {
		return getNodeByCs(cs, root);
	}

	/**
	 * Returns the TreeNode instance of the ComponentSet.
	 * 
	 * @param cs
	 *            the ComponentSet instance
	 * @param root
	 *            the root node of component tree
	 * @return the TreeNode instance of the ComponentSet
	 */
	protected TreeNode getNodeByCs(ComponentSet cs, TreeNode root) {
		if (cs == root.getComponentSet()) {
			return root;
		}
		for (TreeNode node : root.getChildTreeNodes()) {
			TreeNode r = getNodeByCs(cs, node);
			if (r != null)
				return r;
		}
		return null;
	}

	/**
	 * Returns the TreeNode instance of the MasterComponent.
	 * 
	 * @param mc
	 *            the MasterComponent instance
	 * @return the TreeNode instance of the MasterComponent
	 */
	protected TreeNode getNodeByMc(MasterComponent mc) {
		return masterComponentMap.get(mc.getId());
	}

	/**
	 * Returns the TreeNode instance of the SlaveComponent.
	 * 
	 * @param slc
	 *            the SlaveComponent instance
	 * @return the TreeNode instance of the SlaveComponent
	 */
	protected TreeNode getNodeBySlc(SlaveComponent slc) {
		return slaveComponentMap.get(slc.getId());
	}

	/**
	 * Defines the requirements for an object that can be used as a tree node in
	 * a Shim_ComponentTree.
	 */
	class TreeNode {

		ComponentSet _parentComponent = null;

		TreeNode _parent = null;

		ComponentSet _componentSet = null;
		MasterComponent _masterComponent = null;
		SlaveComponent _slaveComponent = null;

		List<TreeNode> _childTreeNodes = new ArrayList<TreeNode>();

		/**
		 * Constructs a new instance of TreeNode (for ComponentSet).
		 * 
		 * @param parent
		 * @param cset
		 */
		TreeNode(TreeNode parent, ComponentSet cset) {
			if (parent == null) {
				_parent = this;
			} else {
				_parent = parent;
				_parent.getChildTreeNodes().add(this);
			}
			_componentSet = cset;
		}

		/**
		 * Constructs a new instance of TreeNode (for MasterComponent).
		 * 
		 * @param parent
		 * @param mc
		 */
		TreeNode(TreeNode parent, MasterComponent mc) {
			if (parent == null) {
				_parent = this;
			} else {
				_parent = parent;
				_parent.getChildTreeNodes().add(this);
			}
			_masterComponent = mc;
		}

		/**
		 * Constructs a new instance of TreeNode (for SlaveComponent).
		 * 
		 * @param parent
		 * @param slc
		 */
		TreeNode(TreeNode parent, SlaveComponent slc) {
			if (parent == null) {
				_parent = this;
			} else {
				_parent = parent;
				_parent.getChildTreeNodes().add(this);
			}
			_slaveComponent = slc;
		}

		/**
		 * Returns child tree nodes.
		 * 
		 * @return the child tree nodes
		 */
		public List<TreeNode> getChildTreeNodes() {
			return _childTreeNodes;
		}

		/**
		 * Returns the parent node.
		 * 
		 * @return the parent node
		 */
		public TreeNode getParent() {
			return _parent;
		}

		/**
		 * Returns child MasterComponents of this node.
		 * 
		 * @return child MasterComponents
		 */
		public List<MasterComponent> getChildMasterComponents() {
			return _parentComponent.getMasterComponent();
		}

		/**
		 * Returns child SlaveComponents of this node.
		 * 
		 * @return child SlaveComponents
		 */
		public List<SlaveComponent> getChildSlaveComponent() {
			return _parentComponent.getSlaveComponent();
		}

		/**
		 * Returns child ComponentSets of this node.
		 * 
		 * @return child ComponentSets
		 */
		public List<ComponentSet> getChildComponentSet() {
			return _parentComponent.getComponentSet();
		}

		/**
		 * Returns the parent ComponentSet of this node.
		 * 
		 * @return the parent ComponentSet
		 */
		public ComponentSet getParentComponentSet() {
			return _parentComponent;

		}

		/**
		 * Returns the ComponentSet instance of this node.
		 * 
		 * @return the ComponentSet instance of this node
		 */
		public ComponentSet getComponentSet() {
			return _componentSet;
		}

		/**
		 * Returns the MasterComponent instance of this node.
		 * 
		 * @return the MasterComponent instance of this node
		 */
		public MasterComponent getMasterComponent() {
			return _masterComponent;
		}

		/**
		 * Returns the SlaveComponent instance of this node.
		 * 
		 * @return the SlaveComponent instance of this node
		 */
		public SlaveComponent getSlaveComponent() {
			return _slaveComponent;
		}

		/**
		 * Returns whether this instance is the root node or not.
		 * 
		 * @return Returns true if this instance is the root node, and false
		 *         otherwise.
		 */
		boolean isRoot() {
			if (this.getParent() == this) {
				return true;
			}
			return false;
		}

		/**
		 * Returns whether this instance is the parent node of an argument or
		 * not.
		 * 
		 * @param tn
		 *            TreeNode
		 * @return Returns true if this instance is the parent node, and false
		 *         otherwise.
		 */
		@SuppressWarnings("unused")
		boolean isParent(TreeNode tn) {
			log.finest("isParent:");
			log.finest("    > this=" + this.toString());
			log.finest("    > tn.getParent()=" + tn.getParent().toString());

			String tn_name = getComponentName(tn);
			String this_name = getComponentName(this);
			String tn_parent_name = getComponentName(tn.getParent());

			if (tn.getParent() == this) {
				return true;
			}
			return false;
		}

		/**
		 * @param tn
		 * @return
		 */
		boolean isChild(TreeNode tn) {
			for (TreeNode tn1 : this.getChildTreeNodes()) {
				if (tn1 == tn) {
					return true;
				}
			}
			return false;
		}

		/**
		 * @param tn
		 * @return
		 */
		boolean isSibling(TreeNode tn) {
			for (TreeNode tn1 : this.getParent().getChildTreeNodes()) {
				if (tn1 == tn) {
					return true;
				}
			}
			return false;
		}

		/**
		 * @param tn
		 * @return
		 */
		public boolean isAncestor(TreeNode tn) {
			if (this.isParent(tn)) {
				return true;
			}
			if (tn.isRoot()) {
				return false;
			}
			return isAncestor(tn.getParent());
		}

		/**
		 * @param tn
		 * @return
		 */
		public String getComponentName(TreeNode tn) {

			if (tn.getMasterComponent() != null) {
				MasterComponent mc = tn.getMasterComponent();
				return mc.getName();
			}
			if (tn.getSlaveComponent() != null) {
				SlaveComponent slc = tn.getSlaveComponent();
				return slc.getName();
			}
			if (tn.getComponentSet() != null) {
				ComponentSet cs = tn.getComponentSet();
				return cs.getName();
			}

			return "";
		}

		/**
		 * Prints this node to the standard output stream.
		 * 
		 * @param level
		 *            non-negative level of this (N.B. start number is 0.)
		 */
		void printTreeNode(int level) {
			TreeNode node = this;

			ComponentSet cs = node.getComponentSet();
			if (cs != null) {
				System.out.println(sp(level) + "Node(level" + level
						+ "): CS Name=" + cs.getName() + "(node="
						+ node.toString() + ")");
			}
			MasterComponent mc = node.getMasterComponent();
			if (mc != null) {
				System.out.println(sp(level) + "Node(level" + level
						+ "): MC Name=" + mc.getName() + "(node="
						+ node.toString() + ")");
			}
			SlaveComponent slc = node.getSlaveComponent();
			if (slc != null) {
				System.out.println(sp(level) + "Node(level" + level
						+ "): SLC Name=" + slc.getName() + "(node="
						+ node.toString() + ")");
			}

			for (TreeNode childNode : node.getChildTreeNodes()) {
				childNode.printTreeNode(level + 1);
			}
		}
	}

	/**
	 * Prints this component tree to the standard output stream.
	 */
	public void printIt() {
		System.out.println("print Shim_ComponentTree");
		root.printTreeNode(0);
	}

	/**
	 * Spaces according to level.
	 * 
	 * @param level
	 *            non-negative level of element
	 * @return space
	 */
	private String sp(int level) {
		String s = "";
		for (int i = 0; i < level; i++) {
			s = s + "    ";
		}
		return s;
	}

}
