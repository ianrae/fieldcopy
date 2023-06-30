package org.dnal.fieldcopy.util.render;

/**
 * Render an object into human-readable string, such as json
 * @author ian
 *
 */
public interface ObjectRenderer {
	String render(Object obj);
}
