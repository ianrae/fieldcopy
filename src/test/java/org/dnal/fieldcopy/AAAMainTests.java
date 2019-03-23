package org.dnal.fieldcopy;

/**
 * TODO
 * -ensure #converters and mappings doesn't grow as do transitve stuff
 *   -do copy in loop
 * -builtInConverters
 * DONE-bean detector service
 *   -everthing that isn't gets a mapping automatically
 * -only transitive stuff if needed (don't create sub-obj mapping for example)
 * -oops. for deeply nested sub-objects we only do recursion while executing copy plan
 *   -so if A contains B contains C  and C has a field that would use a built-in converter,
 *    then we don't know that while inspecting B.
 *
 * WONT DO
 * -mutli-dim arrays
 * -failIfNull on field and global
 *   
 * @author Ian Rae
 *
 */
public class AAAMainTests {

}
