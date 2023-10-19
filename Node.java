public class Node{

	String col;
	int val;
	Node parent;
	Node left;
	Node right;
	boolean left_is_leaf;
	boolean right_is_leaf;
	boolean is_leaf;
	int class_value;

	/***
	 * 'col' refers to what this node splits into
	 * 'val' refers to what the parent node travels to this on (0 or 1)
	 * 'parent' refers to the parent node
	 * 'left' refers to the next 0 node
	 * 'right' refers to the next 1 node
	***/

	public Node(){
		this.col = null;
		this.val = -1;
		this.parent = null;
		this.left = null;
		this.right = null;
		this.left_is_leaf = false;
		this.right_is_leaf = false;
		this.is_leaf = false;
		this.class_value = -1;

	}
	public Node(String col, int val, Node parent){
		this.col = col;
		this.val = val;
		this.parent = parent;
		this.left = null;
		this.right = null;
		this.left_is_leaf = false;
		this.right_is_leaf = false;
		this.is_leaf = false;
		this.class_value = -1;
	}

	public void set_col(String s){
		col = s;
	}
	public void set_val(int s){
		val = s;
	}
	public void set_parent(Node s){
		parent = s;
	}
	public void set_left(Node s){
		left = s;
	}
	public void set_right(Node s){
		right = s;
	}
	public void set_left_is_leaf(boolean s){
		left_is_leaf = s;
	}
	public void set_right_is_leaf(boolean s){
		right_is_leaf = s;
	}
	public void set_is_leaf(boolean s){
		is_leaf = s;
	}
	public void set_class_value(int s){
		class_value = s;
	}

	public String get_col(){
		return col;
	}
	public int get_val(){
		return val;
	}
	public Node get_parent(){
		return parent;
	}
	public Node get_left(){
		return left;
	}
	public Node get_right(){
		return right;
	}
	public boolean get_left_is_leaf(){
		return left_is_leaf;
	}
	public boolean get_right_is_leaf(){
		return right_is_leaf;
	}
	public boolean get_is_leaf(){
		return is_leaf;
	}
	public int get_class_value(){
		return class_value;
	}

}