//Run_2
import java.util.*;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.lang.*;
import java.io.*;
import java.lang.*;
import java.util.HashMap;

public class Run_2{

	String[] headers;
	List<int[]> training_list = new ArrayList<int[]>();
	List<int[]> validation_list = new ArrayList<int[]>();
	List<int[]> test_list = new ArrayList<int[]>();

	int class_pos;
	int class_neg;
	double class_entropy;

	double class_variance;

	double[] root_gains;

	Node root;

	HashMap<String, Integer> h_loc = new HashMap<String, Integer>();

	public Run_2(String[] headers, List<int[]> training_list, List<int[]>validation_list, List<int[]>test_list){
		this.headers = headers;
		this.training_list = training_list;
		this.validation_list = validation_list;
		this.test_list = test_list;

		root_gains = new double[headers.length];
		for(int i = 0; i < headers.length; i++){
			h_loc.put(headers[i], i);
		}
	}

	public void ig_flow(boolean is_print, boolean heuristic){
		root = new Node();
		if(heuristic){
			class_entropy_ig(training_list);
			make_tree(root, training_list, headers);
		}
		else{
			class_v(training_list);
			make_tree_2(root, training_list, headers);
		}
		if(is_print){
			print_tree(root);
		}
		System.out.print("training set --- ");
		run_data(training_list);
		System.out.print("validation set --- ");
		run_data(validation_list);
		System.out.print("test set --- ");
		run_data(test_list);
	}

	public void class_v(List<int[]> l){
		double zero_count = 0;
		double one_count = 0;
		for(int[] i : l){
			if(i[i.length - 1] == 0){
				zero_count++;
			}
			else{
				one_count++;
			}
		}
		class_variance = (zero_count*one_count)/(l.size()*l.size());
	}

	public void run_data(List<int[]> l){
		int correct = 0;
		int incorrect = 0;
		Node current = root;
		String curr_h_key = "";
		for(int[] i : l){
			while(!current.get_is_leaf()){
				curr_h_key = current.get_col();
				if(i[h_loc.get(curr_h_key)] == 0){
					if(current.get_left().get_is_leaf()){
						if(current.get_left().get_class_value() == i[i.length - 1]){
							correct++;
						}
						else{
							incorrect++;
						}
					}
					current = current.get_left();
				}
				else{
					if(current.get_right().get_is_leaf()){
						if(current.get_right().get_class_value() == i[i.length - 1]){
							correct++;
						}
						else{
							incorrect++;
						}
					}
					current = current.get_right();
				}
			}
			current = root;
		}
		System.out.println("correct: " + correct + " | incorrect: " + incorrect + " | accuracy: " + (double)correct/(correct+incorrect));
	}

	public List<int[]> get_training_list(){
		return training_list;
	}

	public List<int[]> get_validation_list(){
		return validation_list;
	}

	public List<int[]> get_test_list(){
		return test_list;
	}

	/***
	 * start with root, recursive on left and right children
	 * if 'n' is leaf, don't call on children
	***/
	public void make_tree(Node n, List<int[]> l, String[] h){
		if(n.get_is_leaf()){
			return;
		}
		else{
			int count = 0;

			Node left = new Node();
			Node right = new Node();
			//find gains of each column in n
			double best_gain = 0;
			int temp_gain = 0;
			String best_col = "";
			String temp_col = "";
			int best_split = 0;
			//(0,0)
			List<int[]> a = new ArrayList<int[]>();
			//(0,1)
			List<int[]> b = new ArrayList<int[]>();
			//(1,0)
			List<int[]> c = new ArrayList<int[]>();
			//(1,1)
			List<int[]> d = new ArrayList<int[]>();

			//(0,0)
			List<int[]> a_best = new ArrayList<int[]>();
			//(0,1)
			List<int[]> b_best = new ArrayList<int[]>();
			//(1,0)
			List<int[]> c_best = new ArrayList<int[]>();
			//(1,1)
			List<int[]> d_best = new ArrayList<int[]>();

			List<int[]> new_left_list = new ArrayList<int[]>();
			List<int[]> new_right_list = new ArrayList<int[]>();

			double[] gains = new double[h.length - 1];

			for(int i = 0; i < h.length - 1; i++){
				if(!h[i].equals("")){
					for(int[] j : l){
						//(0,0)
						if(j[i] == 0 && j[j.length - 1] == 0){
							a.add(j);
						}
						//(0,1)
						else if(j[i] == 0 && j[j.length - 1] == 1){
							b.add(j);
						}
						//(1,0)
						else if(j[i] == 1 && j[j.length - 1] == 0){
							c.add(j);
						}
						//(1,1)
						else{
							d.add(j);
						}
					}
					if(a.size() == 0 && b.size() == 0 || c.size() == 0 && d.size() == 0){
						gains[i] = 0;
					}
					//if both children will be leaves
					//best case, break loop
					else if((a.size() == 0 || b.size() == 0) && (c.size() == 0 || d.size() == 0)){
						a_best = a;
						b_best = b;
						c_best = c;
						d_best = d;

						best_gain = 2;
						best_col = h[i];
						gains[i] = 2;
						i = h.length;
					}
					//check for left_is_leaf
					else if(a.size() == 0 || b.size() == 0){
						//check if the amount of data on the leaf side is larger with this column or a previous one
						if(Math.max(a.size(), b.size()) > best_split){
							a_best = a;
							b_best = b;
							c_best = c;
							d_best = d;

							best_split = Math.max(a.size(), b.size());
							best_gain = 1;
							best_col = h[i];
						}
						gains[i] = 1;
					}
					//check for right_is_leaf
					else if(c.size() == 0 || d.size() == 0){
						//check if the amount of data on the leaf side is larger with this column or a previous one
						if(Math.max(c.size(), d.size()) > best_split){
							a_best = a;
							b_best = b;
							c_best = c;
							d_best = d;

							best_split = Math.max(c.size(), d.size());
							best_gain = 1;
							best_col = h[i];
						}
						gains[i] = 1;
					}
					//neither are leaves
					else{
						double left_side = (0-((double)b.size()/(b.size()+a.size()))) * (double)(Math.log((double)b.size()/(b.size()+a.size()))/Math.log(2)) - ((double)a.size()/(b.size()+a.size())) * (double)(Math.log((double)a.size()/(b.size()+a.size()))/Math.log(2));
						double right_side = (0-((double)d.size()/(d.size()+c.size()))) * (double)(Math.log((double)d.size()/(d.size()+c.size()))/Math.log(2)) - ((double)c.size()/(d.size()+c.size())) * (double)(Math.log((double)c.size()/(d.size()+c.size()))/Math.log(2));
						double gain = class_entropy - (((double)(d.size() + c.size())/l.size())*right_side) - (((double)(b.size() + a.size())/l.size())*left_side);
						if(gain > best_gain){
							a_best = a;
							b_best = b;
							c_best = c;
							d_best = d;

							best_gain = gain;
							best_col = h[i];
						}
						gains[i] = gain;
					}
					if(best_gain != 2){
						//System.out.println("a: " + a.size() + " | b: " + b.size() + " | c: " + c.size() + " | d: " + d.size() + " || " + h[i] + " gain: " + gains[i]);

					}
					a = new ArrayList<int[]>();
					b = new ArrayList<int[]>();
					c = new ArrayList<int[]>();
					d = new ArrayList<int[]>();
					//best_split = 0;
				}
				else{
					count++;
					//System.out.println(count);
					gains[i] = -1;
				}
				
			}
			//System.out.println("---------------------------------Best col = " + best_col + "---------------------------------");
			if(best_gain == 2){
				left.set_is_leaf(true);
				right.set_is_leaf(true);
				if(a_best.size() == 0){
					left.set_class_value(1);
				}
				if(b_best.size() == 0){
					left.set_class_value(0);
				}
				if(c_best.size() == 0){
					right.set_class_value(1);
				}
				if(d_best.size() == 0){
					right.set_class_value(0);
				}
			}
			else if(best_gain == 1){
				if(a_best.size() == 0){
					left.set_class_value(1);
					left.set_is_leaf(true);
				}
				if(b_best.size() == 0){
					left.set_class_value(0);
					left.set_is_leaf(true);
				}
				if(c_best.size() == 0){
					right.set_class_value(1);
					right.set_is_leaf(true);
				}
				if(d_best.size() == 0){
					right.set_class_value(0);
					right.set_is_leaf(true);
				}
			}
			n.set_col(best_col);
			for(int k = 0; k < h.length - 1; k++){
				if(best_col.equals(h[k])){
					h[k] = "";
				}
			}

			n.set_left(left);
			n.set_right(right);

			List<int[]> left_list = a_best;
			List<int[]> right_list = c_best;
			left_list.addAll(b_best);
			right_list.addAll(d_best);

			int test_no_col_left = 0;
			for(int k = 0; k < h.length - 1; k++){
				if(!h[k].equals("")){
					test_no_col_left++;
				}
			}

			if(best_gain == 0){
				best_gain = 2;
				left.set_is_leaf(true);
				right.set_is_leaf(true);
				left.set_class_value(0);
				right.set_class_value(1);
				for(int z = 0; z < h.length - 1; z++){
					if(!h[z].equals("")){
						n.set_col(h[z]);
						z = h.length;
					}
				}
			}

			if(test_no_col_left != 0 && !(left.get_is_leaf() && right.get_is_leaf())){
				if(left.get_is_leaf()){
					make_tree(n.get_right(), right_list, h);
				}
				else if(right.get_is_leaf()){
					make_tree(n.get_left(), left_list, h);
				}
				else{
					String[] h2 = new String[h.length];
					for(int y = 0; y < h.length; y++){
						h2[y] = h[y];
					}
					make_tree(n.get_left(), left_list, h);
					//System.out.println("in between, test_no_col_left = " + test_no_col_left);
					make_tree(n.get_right(), right_list, h2);
				}
			}
		}
	}

	public void make_tree_2(Node n, List<int[]> l, String[] h){
		if(n.get_is_leaf()){
			return;
		}
		else{
			int count = 0;

			Node left = new Node();
			Node right = new Node();
			//find gains of each column in n
			double best_gain = 0;
			int temp_gain = 0;
			String best_col = "";
			String temp_col = "";
			int best_split = 0;
			//(0,0)
			List<int[]> a = new ArrayList<int[]>();
			//(0,1)
			List<int[]> b = new ArrayList<int[]>();
			//(1,0)
			List<int[]> c = new ArrayList<int[]>();
			//(1,1)
			List<int[]> d = new ArrayList<int[]>();

			//(0,0)
			List<int[]> a_best = new ArrayList<int[]>();
			//(0,1)
			List<int[]> b_best = new ArrayList<int[]>();
			//(1,0)
			List<int[]> c_best = new ArrayList<int[]>();
			//(1,1)
			List<int[]> d_best = new ArrayList<int[]>();

			List<int[]> new_left_list = new ArrayList<int[]>();
			List<int[]> new_right_list = new ArrayList<int[]>();

			double[] gains = new double[h.length - 1];

			for(int i = 0; i < h.length - 1; i++){
				if(!h[i].equals("")){
					for(int[] j : l){
						//(0,0)
						if(j[i] == 0 && j[j.length - 1] == 0){
							a.add(j);
						}
						//(0,1)
						else if(j[i] == 0 && j[j.length - 1] == 1){
							b.add(j);
						}
						//(1,0)
						else if(j[i] == 1 && j[j.length - 1] == 0){
							c.add(j);
						}
						//(1,1)
						else{
							d.add(j);
						}
					}
					if(a.size() == 0 && b.size() == 0 || c.size() == 0 && d.size() == 0){
						gains[i] = 0;
					}
					//if both children will be leaves
					//best case, break loop
					else if((a.size() == 0 || b.size() == 0) && (c.size() == 0 || d.size() == 0)){
						a_best = a;
						b_best = b;
						c_best = c;
						d_best = d;

						best_gain = 2;
						best_col = h[i];
						gains[i] = 2;
						i = h.length;
					}
					//check for left_is_leaf
					else if(a.size() == 0 || b.size() == 0){
						//check if the amount of data on the leaf side is larger with this column or a previous one
						if(Math.max(a.size(), b.size()) > best_split){
							a_best = a;
							b_best = b;
							c_best = c;
							d_best = d;

							best_split = Math.max(a.size(), b.size());
							best_gain = 1;
							best_col = h[i];
						}
						gains[i] = 1;
					}
					//check for right_is_leaf
					else if(c.size() == 0 || d.size() == 0){
						//check if the amount of data on the leaf side is larger with this column or a previous one
						if(Math.max(c.size(), d.size()) > best_split){
							a_best = a;
							b_best = b;
							c_best = c;
							d_best = d;

							best_split = Math.max(c.size(), d.size());
							best_gain = 1;
							best_col = h[i];
						}
						gains[i] = 1;
					}
					//neither are leaves
					else{
						double left_side = (((double)a.size() + b.size())/l.size())*(((double)a.size())*b.size())/(l.size()*l.size());
						double right_side = (((double)a.size() + b.size())/l.size())*(((double)a.size())*b.size())/(l.size()*l.size());
						double gain = class_variance - left_side - right_side;
						if(gain > best_gain){
							a_best = a;
							b_best = b;
							c_best = c;
							d_best = d;

							best_gain = gain;
							best_col = h[i];
						}
						gains[i] = gain;
					}
					if(best_gain != 2){
						//System.out.println("a: " + a.size() + " | b: " + b.size() + " | c: " + c.size() + " | d: " + d.size() + " || " + h[i] + " gain: " + gains[i]);

					}
					a = new ArrayList<int[]>();
					b = new ArrayList<int[]>();
					c = new ArrayList<int[]>();
					d = new ArrayList<int[]>();
					//best_split = 0;
				}
				else{
					count++;
					//System.out.println(count);
					gains[i] = -1;
				}
				
			}
			//System.out.println("---------------------------------Best col = " + best_col + "---------------------------------");
			if(best_gain == 2){
				left.set_is_leaf(true);
				right.set_is_leaf(true);
				if(a_best.size() == 0){
					left.set_class_value(1);
				}
				if(b_best.size() == 0){
					left.set_class_value(0);
				}
				if(c_best.size() == 0){
					right.set_class_value(1);
				}
				if(d_best.size() == 0){
					right.set_class_value(0);
				}
			}
			else if(best_gain == 1){
				if(a_best.size() == 0){
					left.set_class_value(1);
					left.set_is_leaf(true);
				}
				if(b_best.size() == 0){
					left.set_class_value(0);
					left.set_is_leaf(true);
				}
				if(c_best.size() == 0){
					right.set_class_value(1);
					right.set_is_leaf(true);
				}
				if(d_best.size() == 0){
					right.set_class_value(0);
					right.set_is_leaf(true);
				}
			}
			n.set_col(best_col);
			for(int k = 0; k < h.length - 1; k++){
				if(best_col.equals(h[k])){
					h[k] = "";
				}
			}

			n.set_left(left);
			n.set_right(right);

			List<int[]> left_list = a_best;
			List<int[]> right_list = c_best;
			left_list.addAll(b_best);
			right_list.addAll(d_best);

			int test_no_col_left = 0;
			for(int k = 0; k < h.length - 1; k++){
				if(!h[k].equals("")){
					test_no_col_left++;
				}
			}

			if(best_gain == 0){
				best_gain = 2;
				left.set_is_leaf(true);
				right.set_is_leaf(true);
				left.set_class_value(0);
				right.set_class_value(1);
				for(int z = 0; z < h.length - 1; z++){
					if(!h[z].equals("")){
						n.set_col(h[z]);
						z = h.length;
					}
				}
			}

			if(test_no_col_left != 0 && !(left.get_is_leaf() && right.get_is_leaf())){
				if(left.get_is_leaf()){
					make_tree_2(n.get_right(), right_list, h);
				}
				else if(right.get_is_leaf()){
					make_tree_2(n.get_left(), left_list, h);
				}
				else{
					String[] h2 = new String[h.length];
					for(int y = 0; y < h.length; y++){
						h2[y] = h[y];
					}
					make_tree_2(n.get_left(), left_list, h);
					//System.out.println("in between, test_no_col_left = " + test_no_col_left);
					make_tree_2(n.get_right(), right_list, h2);
				}
			}
		}
	}

	//information gain
	//entropy(S) = -p(+) * logBASE2(p(+)) - p(-) * logBASE2(p(-))
	public double class_entropy_ig(List<int[]> l){
		class_pos = 0;
		class_neg = 0;
		for(int[] i : l){
			if(i[i.length - 1] == 0){
				class_neg++;
			}
			else{
				class_pos++;
			}
		}
		double e = (0-((double)class_pos/l.size())) * (double)(Math.log((double)class_pos/l.size())/(double)Math.log(2)) - ((double)class_neg/l.size()) * (Math.log((double)class_neg/l.size())/Math.log(2));
		//System.out.println("pos = " + class_pos + "\nneg = " + class_neg + "\nentropy = " + e + "\nl.size() = " + l.size());
		root_gains[root_gains.length - 1] = e;
		class_entropy = e;
		return e;
	}

	public void print_tree(Node n){
		int counter = 1;
		System.out.println(n.get_col() + " = 0 :");
		print_next_node(n.get_left(),counter);
		System.out.println(n.get_col() + " = 1 :");
		print_next_node(n.get_right(),counter);
	}

	public void print_next_node(Node n, int count){
		for(int j = 0; j < count; j++){
			System.out.print("| ");
		}
		count++;
		if(n.get_left().get_is_leaf()){
			System.out.println(n.get_col() + " = 0 : " + n.get_left().get_class_value());
		}
		else{
			System.out.println(n.get_col() + " = 0 :");
			print_next_node(n.get_left(), count);
		}
		count--;
		for(int j = 0; j < count; j++){
			System.out.print("| ");
		}
		count++;
		if(n.get_right().get_is_leaf()){
			System.out.println(n.get_col() + " = 1 : " + n.get_right().get_class_value());
		}
		else{
			System.out.println(n.get_col() + " = 1 :");
			print_next_node(n.get_right(), count);
		}
		
	}

	
}



