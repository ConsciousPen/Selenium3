package aaa.helpers;

import java.util.ArrayList;

public class CommonUseMethods
{
    public static Boolean DoesArrayListContainValue(ArrayList<String> arrayValuesReturned, String valueToValidate) {
        Boolean bFoundValue = false;
        for (String value : arrayValuesReturned) {
            if (value.equalsIgnoreCase(valueToValidate)) {
                bFoundValue = true;
            }
        }
        return bFoundValue;
    }
}
