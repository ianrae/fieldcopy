package org.dnal.fieldcopy;

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
	public boolean autoCopyCaseSensitiveMatch = false;
}