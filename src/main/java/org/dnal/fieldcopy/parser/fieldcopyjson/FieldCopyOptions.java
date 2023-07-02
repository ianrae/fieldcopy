package org.dnal.fieldcopy.parser.fieldcopyjson;

public class FieldCopyOptions {
    //set in config part of json
    public String defaultSourcePackage;
    public String defaultDestinationPackage;

    //time and date formats are not used during code generation.
    //However it's often useful to store them in the JSON file along with definitions of converters
    //And then use FieldCopyJsonParser to read them in at runtime (see RuntimeOptionHelper)
    public boolean validateDateAndTimeValues = false;
    public String localDateFormat;
    public String localTimeFormat;
    public String localDateTimeFormat;
    public String zonedDateTimeFormat;
    public String utilDateFormat;

    public boolean outputFieldCommentFlag = true;
    public boolean createNewListWhenCopying = true;
    public boolean outputGeneratedByCommentFlag = true;
}
