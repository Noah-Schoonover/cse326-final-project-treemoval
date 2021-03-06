package com.treemoval.data;

import static com.treemoval.data.Tags.*;
//----------------------------------------------------------------------------------------------------------------------
// ::Tree
//
/**
 * The Tree class holds euclidean coordinates for the location of a tree along with various helper functions.
 *
 * @author Garrett Evans
 * @version 1.1
 */
public class Tree {

    private double x;
    private double y;
    private double z;
    private double height;
    private double radius;
    private Tags tag = UNMARKED;
    private double dist;


    //--------------------------------------------------------------------------------------------------
    // Tree::Tree
    //
    /**
     * This constructor instantiates a new tree at the origin of the euclidean space.
     */
    public Tree() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    //--------------------------------------------------------------------------------------------------
    // Tree::Tree
    //
    /**
     * This constructor instantiates a new tree with specified coordinates.
     *
     * todo can we use default parameter values here?
     *
     * @param x the x coordinate of the tree
     * @param y the y coordinate of the tree
     * @param z the z coordinate (elevation) of the tree
     */
    public Tree(double x, double y, double z, double height, double radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.height = height;
        this.radius = radius;
    }

    //--------------------------------------------------------------------------------------------------
    // Tree::toString
    //
    /**
     * Overrides the inherited toString method to print coordinates.
     *
     * @return a formatted string of the x, y, and z coordinates.
     */
    @Override
    public String toString() {
        return "x: " + getX() + ", y: " + getY() + ", z: " + getZ() + ", height: " + getHeight() + ", radius: "
                + getRadius() + ", cut: " + getTag();
    }

    //--------------------------------------------------------------------------------------------------
    // Tree Getters and Setters
    //

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }

    public Tags getTag() {return tag;}
    public void setTag(Tags tag) {this.tag = tag;}

    public double getDist() {return this.dist;}
    public void setDist(double dist) {this.dist = dist;}

}
