package enums;

import java.util.Arrays;

public enum Months {
    JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEPT, OCT, NOV, DEC;

    //https://stackoverflow.com/questions/13783295/getting-all-names-in-an-enum-as-a-string
    public static String[] getMonthsString(){
        return Arrays.stream(Months.class.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }


}
