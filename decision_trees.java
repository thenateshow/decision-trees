import java.util.*;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

public class decision_trees{

	static File training_set;
	static File validation_set;
	static File test_set;
	static Boolean to_print;
	static Boolean heuristic;
	static String[] headers;
	static List<int[]> training_list = new ArrayList<int[]>();
	static List<int[]> validation_list = new ArrayList<int[]>();
	static List<int[]> test_list = new ArrayList<int[]>();

	//heuristic: 1 for information gain, 2 (or anything else) for variance impurity
	public static void main(String[] args){
		if(args.length != 5){
			System.out.println("not 5 args");
			//ERROR
		}
		training_set = new File(args[0]);
		validation_set = new File(args[1]);
		test_set = new File(args[2]);
		if(args[3].equals("yes")){
			to_print = true;
			//System.out.println("to_print = true");
		}
		else{
			to_print = false;
			//System.out.println("to_print = false");
		}
		if(args[4].equals("1")){
			//information gain
			heuristic = true;
		}
		else{
			//variance impurity
			heuristic = false;
		}
		try{
			Scanner tr = new Scanner(training_set);
			String line = tr.nextLine();
			headers = line.split(",");
			String[] data = headers;
			int[] temp = new int[data.length];
			//System.out.println(temp.length + " " + data[data.length - 1]);
			while(tr.hasNextLine()){
				line = tr.nextLine();
				data = line.split(",");
				for(int i = 0; i < data.length; i++){
					temp[i] = Integer.parseInt(data[i]);
				}
				training_list.add(temp);
				temp = new int[data.length];
			}
			Scanner va = new Scanner(validation_set);
			line = va.nextLine();
			while(va.hasNextLine()){
				line = va.nextLine();
				data = line.split(",");
				for(int i = 0; i < data.length; i++){
					temp[i] = Integer.parseInt(data[i]);
				}
				validation_list.add(temp);
				temp = new int[data.length];
			}
			Scanner te = new Scanner(test_set);
			line = te.nextLine();
			while(te.hasNextLine()){
				line = te.nextLine();
				data = line.split(",");
				for(int i = 0; i < data.length; i++){
					temp[i] = Integer.parseInt(data[i]);
				}
				test_list.add(temp);
				temp = new int[data.length];
			}
			//System.out.println("Training list size: " + training_list.size());
			//System.out.println("Validation list size: " + validation_list.size());
			//System.out.println("Test list size: " + test_list.size());
		}
		catch(FileNotFoundException e){
			System.out.println("file not found");
		}

		Run_2 main_2 = new Run_2(headers, training_list, validation_list, test_list);
		main_2.ig_flow(to_print, heuristic);

		/*
		Run main = new Run(headers, training_list, validation_list, test_list);
		main.ig_flow();
		
		main.class_entropy_ig(main.get_training_list());
		main.class_entropy_ig(main.get_validation_list());
		main.class_entropy_ig(main.get_test_list());
		main.gain_ig(main.get_training_list());
		*/
	}

}