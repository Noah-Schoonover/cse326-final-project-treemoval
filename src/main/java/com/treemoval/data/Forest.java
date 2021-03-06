package com.treemoval.data;

import java.io.*;
import java.util.*;

import static com.treemoval.data.Tags.*;
import static java.lang.Math.*;
import java.util.concurrent.ThreadLocalRandom;

//----------------------------------------------------------------------------------------------------------------------
// ::Forest
//
/**
 * The Forest class holds and manipulates an ArrayList of Tree objects.
 *
 * Includes methods for reading and writing to file, calculating the distance between two trees, and printing the
 * coordinates of every tree in the forest.
 *
 * @author Garrett Evans
 * @version 1.1
 *
 */
public class Forest {

    public List<Tree> trees = new ArrayList<>(); // todo should probably be private
  
    private double minX = 0;
    private double maxX = 0;
    private double minZ = 0;
    private double maxZ = 0;

    //--------------------------------------------------------------------------------------------------
    // Forest::Forest
    //
    /**
     * The default constructor leaves the ArrayList empty
     */
    public Forest() {

    }

    //--------------------------------------------------------------------------------------------------
    // Forest::Forest
    //
    /**
     * copy constructor
     */
    public Forest(Forest forestToCopy) {

        this.trees = new ArrayList<>(forestToCopy.trees);
        this.minX = forestToCopy.getMinX();
        this.maxX = forestToCopy.getMaxX();
        this.minZ = forestToCopy.getMinZ();
        this.maxZ = forestToCopy.getMaxZ();

    }

    //--------------------------------------------------------------------------------------------------
    // Forest::Forest
    //
    /**
     * This constructor creates a forest from a CSV file
     */
    public Forest(String path) {

        readFromFile(path);
        updateBounds();

    }

    /**
     * This constructor instantiates with user defined number of trees and area of forest.
     *
     * @param num_trees the number of trees in the forest
     */
    public Forest(int num_trees, int xBound, int zBound) {

        HeightMap heightMap = new HeightMap(xBound, zBound);

        for(int i = 0; i < num_trees; i++){
            Random rand = new Random();
            double x = rand.nextInt(xBound) + rand.nextDouble();
            double z = rand.nextInt(zBound) + rand.nextDouble();
            double y = heightMap.map[(int) Math.sqrt(x)][(int) Math.sqrt(z)];
            double height = (rand.nextInt(200) + rand.nextDouble());
            double radius = height / 5; //just seemed reasonable, change if need be


            this.trees.add(new Tree(x, y, z, height, radius));
        }
        updateBounds();
    }

    //--------------------------------------------------------------------------------------------------
    // Forest::readFromFile
    //
    /**
     * Reads tree data from a CSV file and stores it in the forest.
     *
     * todo This method should probably accept an enum value (APPEND or CLEAR)
     *      to determine whether or not to clear before adding trees from the file.
     *
     * @param path the path to the CSV file to be read
     * @throws IOException if the CSV file is not found
     */
    public void readFromFile(String path) {

        File file = new File(path);
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(file));

            this.trees.clear();

            String line;

            while ((line = br.readLine()) != null) {
                String[] dets = line.split(",");
                double x = Double.parseDouble(dets[0]);
                double y = Double.parseDouble(dets[1]);
                double z = Double.parseDouble(dets[2]);
                double height = Double.parseDouble(dets[3]);
                double radius = Double.parseDouble(dets[4]);
                this.trees.add(new Tree(x, y, z, height, radius));
            }

            br.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found at location: " + path);
        } catch (IOException e) {
            System.out.println("IO exception at location: " + path);
        }

    }

    //--------------------------------------------------------------------------------------------------
    // Forest::exportForest
    //
    /**
     * Reads tree data from a CSV file and stores it in the forest.
     * Returns 0 on success, 1 if file exists, 2 if file extension is incorrect
     * @throws IOException if the CSV file is not found
     */
    public int exportForest(String fileName) throws IOException {

        File file = new File(fileName);
        String line;

        if(!fileName.substring(fileName.length() - 4).equals(".csv")) {
            System.out.println("The file name needs to with the .csv file extension.\n");
            return 2;
        }


        if(file.createNewFile()){
            System.out.println("File created: " + file.getName());
            FileWriter writer = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(writer);

            for(Tree tree: this.trees){
                line = tree.getX() + "," + tree.getY() + "," + tree.getZ() + "," + tree.getHeight() + ","
                        + tree.getRadius();
                bw.write(line);
                bw.newLine();
            }

            bw.close();
            System.out.println("File writing success.\n");

        } else {
            System.out.println("File already exists.\n");
            return 1;
        }

        return 0;
    }

    //--------------------------------------------------------------------------------------------------
    // Forest::distance
    //
    /**
     * Calculates the euclidean distance between two trees. If the bottom of tree a is above tree b, then the closest
     * distance between the two trees is from the bottom of tree a to the top of tree b. If some part of both trees are
     * the same height, then the shortest distance calculation must ignore the y value's of the trees.
     *
     * When returning the distance, the radius of both trees is subtracted.
     *
     * Essentially the trees are turned into pill shaped objects, and the distance between those is calculated.
     *
     * @param a the first tree
     * @param b the second tree
     * @return the shortest distance between trees a and b.
     */
    public static double distance(Tree a, Tree b) {
        double x1 = a.getX();
        double y1 = a.getY();
        double z1 = a.getZ();
        double h1 = a.getHeight();
        double r1 = a.getRadius();

        double x2 = b.getX();
        double y2 = b.getY();
        double z2 = b.getZ();
        double h2 = b.getHeight();
        double r2 = b.getRadius();

        if (y1 > (y2 + h2)) {//the bottom of tree a is above the top of tree b
            return sqrt(pow(x2-x1, 2) + pow((y2+h2)-y1, 2) + pow(z2-z1, 2)) - r1 - r2; //y calculation includes height
        } else if (y2 > (y1 + h1)) {//the bottom of tree b is above the top of tree a
            return sqrt(pow(x2 - x1, 2) + pow(y2 - (y1 + h1), 2) + pow(z2 - z1, 2)) - r1 - r2;
        } else { //some part of both trees are at the same height
            return sqrt(pow(x2-x1, 2) + pow(z2-z1, 2)) - r1 - r2; //does not include y since trees overlap in y value
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Forest::getTree
    //
    /**
     * Returns the tree at a specified index in the forest.
     *
     * todo should handle out of bounds indexing without crashing
     *
     * @param x the index value of the desired tree
     * @return the Tree object, if found.
     */
    public Tree getTree(int x) {
        return this.trees.get(x);
    }

    //--------------------------------------------------------------------------------------------------
    // Forest::listTrees
    //
    /**
     * Outputs the coordinates of every tree in the forest.
     */
    public void listTrees() {
        int i = 0;
        for(Tree tree : this.trees) {
            i++;
            System.out.println("Tree " + i + "; " + tree);
        }
        System.out.println();
    }

    //--------------------------------------------------------------------------------------------------
    // Forest::updateBounds
    //
    /**
     * Sets the bound for use with the visualizer.
     */
    public void updateBounds() {
        for (Tree tree: trees) {
            if (tree.getX() < minX)
                minX = tree.getX();
            if (tree.getX() > maxX)
                maxX = tree.getX();

            if (tree.getZ() < minZ)
                minZ = tree.getZ();
            if (tree.getZ() > maxZ)
                maxZ = tree.getZ();
        }
        System.out.println("Update bounds: ");
    }

    //--------------------------------------------------------------------------------------------------
    // Forest::runThinningAlgorithmNewForest
    //
    /**
     * creates a new forest to run the thinning algorithm on, and returns it
     * @return the thinned forest
     */
    public Forest runThinningAlgorithmNewForest (double safeDistance) {

        Forest thinnedForest = new Forest(this);
        thinnedForest.runThinningAlgorithm(safeDistance);
        return thinnedForest;

    }

    //--------------------------------------------------------------------------------------------------
    // Forest::runThinningAlgorithm
    //
    /**
     * The algorithm that goes through the forest and marks each tree to be cut or not. It begins by selecting the first
     * tree in the forest as the "currentTree", and marks it to not be cut. Then the distance to every tree in the
     * forest is calculated, then sorted by distance (closest to furthest). Then, the algorithm goes through each tree.
     * If the tree has not been marked yet and it is within the "safeDistance", it gets marked for removal. If the
     * tree has not been marked and it is outside the safeDistance, then it is marked to not be cut down,
     * and is then marked as the new "currentTree" since it is the closest tree at a safe distance.
     * The loop ends. If the # of marked trees is not equal to the # of total trees then the process begins again.
     *
     * todo it might be able to be more efficient but it would be a lot of work for little return
     * */
    public void runThinningAlgorithm(double safeDistance) {



        int marked = 1; //# of trees marked to be cut or not
        int treeNum = this.trees.size(); //total # of trees
        Tree currentTree = this.getTree(0);
        if (currentTree.getTag() != UNMARKED)
            return;
        currentTree.setTag(SAFE);

        while (marked < treeNum) {
            for (Tree tmpTree : this.trees) {
                tmpTree.setDist(distance(currentTree, tmpTree));
            }

            this.trees.sort(new SortByDist()); //sort by closest to furthest distance

            for (Tree tmpTree : this.trees) {

                if (tmpTree.getDist() < safeDistance && tmpTree.getTag() == UNMARKED) {
                    tmpTree.setTag(CUT); //to be cut!
                    marked++;
                } else if (tmpTree.getDist() >= safeDistance && tmpTree.getTag() == UNMARKED) {
                    currentTree = tmpTree;
                    currentTree.setTag(SAFE);//don't cut
                    marked++;
                    break;
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Forest:: Getters and Setters
    //

    public double getMinX() { return minX; }

    public double getMaxX() { return maxX; }

    public double getMinZ() { return minZ; }

    public double getMaxZ() { return maxZ; }

    //--------------------------------------------------------------------------------------------------
    // Forest::main
    //
    /**
     * Simple forest testing; instantiates and prints a random forest, then clears the forest
     * and reads data from a file to print to console as well.
     *
     * todo Should be tested with JUnit testing instead of using main function.
     *
     * @param args none
     */
    public static void main(String[] args) {

        Forest forest = new Forest(300, 30, 30);
        Forest new_forest = new Forest(100, 30, 30);


        System.out.println("This is the first forest using the default constructor.");
        forest.listTrees();

        System.out.println("This is the second forest created by setting the number of trees and area.");
        new_forest.listTrees();

        System.out.println("The distance between the first two trees is: " +
                distance(forest.getTree(0), forest.getTree(1)) + "\n");
        forest.listTrees();
        forest.runThinningAlgorithm(5);
        forest.runThinningAlgorithm(5);
        forest.listTrees();

        String export = "forest3_export.csv";

        try {
            forest.exportForest(export);
        } catch (IOException e) {
            e.printStackTrace();
        }

        forest.readFromFile("forest_export.csv");
        System.out.println("This is reading from the file forest_export.csv");
        forest.listTrees();
        System.out.println("The distance between the first two trees is: " +
                distance(forest.getTree(0), forest.getTree(1)) + "\n");
        forest.runThinningAlgorithm(5);


    }

}