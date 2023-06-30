package org.dnal.fieldcopy.implicitconverter;

import java.util.HashMap;
import java.util.Map;

public class ICRow {
    public Map<String, ImplicitConverter> map = new HashMap<>(); //key, handler for a src type
}
