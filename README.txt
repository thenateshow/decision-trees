to compile:
javac decision_trees.java
	as long as all source code files are in the same folder, this will compile project

to run:
java decision_trees training_set.csv validation_set.csv test_set.csv no 1

when running, the fourth input ('no' in above line) refers to to_print
when running, the fifth input ('1' in above line) refers to which heuristic is to be used.
	1 - information gain
	2 - variance impurity
