/**
 * Author: Benjamin Baird
 * Created on: 2016-11-20
 * Last Updated on: 2016-11-20
 * Filename: Cell
 * Description:
 */
public class Cell {
    int index;
    String id;

    public Cell(int index, String id){
        this.index = index;
        this.id = id;
    }

    public String toString() {
        return id;
    }
}
