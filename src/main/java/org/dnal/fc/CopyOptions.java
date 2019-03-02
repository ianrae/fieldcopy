package org.dnal.fc;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fc.core.ValueTransformer;

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