package org.dnal.fieldcopy;

/**
 * Creates FieldCopier objects.
 * 
 * <p>FieldCopier objects are not thread-safe. Therefore they should be
 * created as local variables and used to do a single copy.</p>
 * 
 * <p>All FieldCopier objects created by an instance of CopierFactory
 * share the same FieldCopyService object.  This is important
 * for performance, because FieldCopyService caches details of 
 * the data classes that it copies, to minimize the use of Java
 * reflection.</p>
 * 
 * <p>Your application should arrange that each data class being copied
 * is being copied by the same copier factory.  This could be by
 * having a single copier factory in the application, or by having
 * a copier member variable in each service or controller that is doing
 * copying.</p>
 * 
 * @author Ian Rae
 *
 */
public interface CopierFactory {
	FieldCopier createCopier();
}