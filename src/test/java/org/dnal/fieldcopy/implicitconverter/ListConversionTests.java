package org.dnal.fieldcopy.implicitconverter;
/*
   scalar: List<String> roles -> List<String>
   prims: can't have list<int>
   List,ArrayList,Array,Set

   List<String> dest = useExistingOrCreate(dest.getRoles();
   dest.addAll(src.getRoles(); //clear? or just append?  answer: clear+addAll

   Map...
 */
public class ListConversionTests {
}
