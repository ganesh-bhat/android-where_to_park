package emacs.wheretopark;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by ganbhat on 7/3/2017.
 */

public class StreetDetails {
    private String nameOfTheStreat;
    private Map<String,Boolean> shopsList;
    private boolean isEvenOddRule;

    public String getNameOfTheStreat() {
        return nameOfTheStreat;
    }

    public void setNameOfTheStreat(String nameOfTheStreat) {
        this.nameOfTheStreat = nameOfTheStreat;
    }

    public Map<String, Boolean> getShopsList() {
        return shopsList;
    }

    public void setShopsList(Map<String, Boolean> shopsList) {
        this.shopsList = shopsList;
    }

    public boolean isEvenOddRule() {
        return isEvenOddRule;
    }

    public void setEvenOddRule(boolean evenOddRule) {
        isEvenOddRule = evenOddRule;
    }


}
