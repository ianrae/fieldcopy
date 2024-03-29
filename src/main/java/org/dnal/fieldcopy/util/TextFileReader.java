package org.dnal.fieldcopy.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a text file and returns the contents as a list of lines of text.
 * Generally only used in junit tests;
 *
 * @author irae
 *
 */
public class TextFileReader {
	
	

	/**
	 * Read file and return contents as single string 
	 * @param path to be read
	 * @return string that is entire contents of file
	 */
	public String readFileAsSingleString(String path) {
		List<String> lines = readFile(path);
		
		String lf = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		for(String s : lines) {
			sb.append(s);
			sb.append(lf);
		}
		return sb.toString();
	}
	
    /**
     *
     * @param path file to be read
     * @return list of all the lines of text in the file
     */
    @SuppressWarnings("PMD.AvoidPrintStackTrace")
    public List<String> readFile(final String path) {
        try {
        	Reader reader = new FileReader(path);
            return doReadFile(reader);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> readFileStream(InputStream istr) throws IOException {
    	Reader reader = new InputStreamReader(istr);
    	return doReadFile(reader);
    }
    public List<String> readFileFromReader(Reader reader) throws IOException {
    	return doReadFile(reader);
    }
    
    /**
     *
     * @param inReader file to be read
     * @return list of all the lines of text in the file
     * @throws IOException
     * @throws Exception
     */
    private List<String> doReadFile(Reader inReader) throws IOException {
        final List<String> linesL = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(inReader)) {
            final StringBuilder builder = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
                linesL.add(line);
                line = reader.readLine();
            }
        }
        return linesL;
    }
    
}