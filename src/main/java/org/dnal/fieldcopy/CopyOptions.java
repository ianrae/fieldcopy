package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fieldcopy.converter.ValueTransformer;

/**
 * Various settings and flags to control the copy operation.
 * 
 * @author Ian Rae
 *
 */
public class CopyOptions {
	public boolean printStackTrace = false;
	public boolean logEachCopy = false;
	public int maxRecursionDepth = 100;
}