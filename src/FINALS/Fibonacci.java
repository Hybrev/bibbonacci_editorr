package FINALS;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Fibonacci {

    private SimpleIntegerProperty range;
    private SimpleStringProperty sequence;

    public Fibonacci(int range, String  sequence){
        this.range = new SimpleIntegerProperty(range);
        this.sequence = new SimpleStringProperty(sequence);
    }


    public int getRange(){
        return range.get();
    }

    public void setRange(int range){
        this.range = new SimpleIntegerProperty(range);
    }

    public String getSequence(){
        return  sequence.get();
    }

    public void setSequence(String sequence) {
        this.sequence = new SimpleStringProperty(sequence);
    }
}
