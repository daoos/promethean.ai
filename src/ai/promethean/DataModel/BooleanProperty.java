package ai.promethean.DataModel;

public class BooleanProperty extends Property {
    private Boolean value;

    public BooleanProperty(String _name, Boolean _value){
        super(_name);
        setValue(_value);
    }

    public void setValue(Boolean _value){
        this.value=_value;
    }

    public Boolean getValue(){
        return this.value;
    }

    @Override
    public String toString(){
        return "Property Name: "+ super.name + ", Property Value: " + this.value;
    }
}
