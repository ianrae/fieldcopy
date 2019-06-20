package org.dnal.fieldcopy.converter;

/**
 * A custom converter. 
 * There are two basic types of converters
 * 
 * -a converter for a specific source field.  Use it to control how that field is converted.
 * 
 * -a converter from one class to another. Use it in cases where there are multiple occurrences
 * of a particular source class to destination class conversion, that you want to customize.
 *
 * @author Ian Rae
 *
 */

public interface ValueConverter {
	/**
	 * FieldCopy is about to copy a field from a source object to a destination object.
	 * It asks each registered converter if it wants to do the conversion. The first converter
	 * to return true from its canConvert method is used.
	 * 
	 * @param source source field information
	 * @param dest destination field informat.
	 * @return true if this value converter wants to convert the given source and destnation.
	 */
	boolean canConvert(FieldInfo source, FieldInfo dest);
	
	/**
	 * Convert a value.
	 * 
	 * @param srcBean source object
	 * @param value  value to be converted
	 * @param ctx   context object
	 * @return   the converted value
	 */
	Object convertValue(Object srcBean, Object value, ConverterContext ctx);
}
