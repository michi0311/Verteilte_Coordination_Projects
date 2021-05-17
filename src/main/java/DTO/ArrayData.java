package DTO;

import java.io.Serializable;


/****************************
 * Created by Michael Marolt *
 *****************************/

public class ArrayData implements Serializable {
    private int[][] partitionedArray;
    private int[][] fullArray;
    private int[][] solutionsArray;

    public ArrayData(){

    }

    public ArrayData(int[][] partitionedArray, int[][] fullArray) {
        this.partitionedArray =partitionedArray;
        this.fullArray = fullArray;
    }

    public void setPartitionedArray(int[][] partitionedArray) {
        this.partitionedArray = partitionedArray;
    }

    public void setFullArray(int[][] fullArray) {
        this.fullArray = fullArray;
    }

    public int[][] getPartitionedArray() {
        return partitionedArray;
    }

    public int[][] getFullArray() {
        return fullArray;
    }

    public void setSolutionsArray(int[][] solutionsArray) {
        this.solutionsArray = solutionsArray;
    }

    public int[][] getSolutionsArray() {
        return solutionsArray;
    }
}
