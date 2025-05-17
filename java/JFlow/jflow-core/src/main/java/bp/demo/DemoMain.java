package bp.demo;

import bp.da.DataType;
import bp.tools.HttpClientUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DemoMain {
    public static void main(String[] args) {
        String paras="doc～";
        String[] str =new String[0];
        if (DataType.IsNullOrEmpty(paras) == false) {
            str = paras.split("～");

        }
        System.out.println(str.length);
    }


}
