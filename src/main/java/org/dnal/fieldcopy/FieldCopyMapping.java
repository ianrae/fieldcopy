package org.dnal.fieldcopy;

import java.util.List;

import org.dnal.fieldcopy.core.FieldPair;

/**
 * Defines the copying of non-scalar child of the parent objects that will be copied.
 * For example, if you are copying a CustomerEntity object, and CustomerEntity
 * has a field of type AddressEntity, you can use a mapping to control how it will be copied.
 * 
 * Example:
 *   FieldCopyMapping mapping = fieldCopy.copy(AddressEntity.class, AddressDTO.class).exclude("region").build();
 *   fieldCopy.copy(myAddressEntity, myAddressDTO).useMapping(mapping).autoCopy().execute();
 * 
 * Note. If a mapping is not specified for a non-scalar child field, then a default 'autocopy' is done,
 * which copies all fields with matching names in the destination object.
 * 
 * @author Ian Rae
 *
 */
public class FieldCopyMapping {
	private Class<?> clazzSrc;
	private Class<?> clazzDest;
	private List<FieldPair> fieldPairs;
	
	public FieldCopyMapping(Class<?> clazzSrc, Class<?> clazzDest, List<FieldPair> fieldsToCopy) {
		super();
		this.clazzSrc = clazzSrc;
		this.clazzDest = clazzDest;
		this.fieldPairs = fieldsToCopy;
	}

	public Class<?> getClazzSrc() {
		return clazzSrc;
	}

	public Class<?> getClazzDest() {
		return clazzDest;
	}

	public List<FieldPair> getFieldPairs() {
		return fieldPairs;
	}
}
