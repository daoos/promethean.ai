package ai.promethean.DataModel;

public class StringCondition extends Condition {
    protected String value;

    public StringCondition(String _name, String _operator, String _value){
        super(_name);
        if(_operator.equals("==")|| _operator.equals("!=")) {
            super.setOperator(_operator);
            setValue(_value);
        }
        else{
            throw new IllegalArgumentException("Illegal operator argument"+_operator);
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean evaluate(Object val1) {
        if(val1 instanceof String){
            switch (this.operator){
                case("=="): return val1.equals(this.value);
                case("!="): return !val1.equals(this.value);
                default: return false;
            }
        }else return false;
    }

    @Override
    public String toString(){
        return "Requirement: " + super.name +" " +super.operator+ " " + this.value;
    }
}