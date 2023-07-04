package org.dnal.fieldcopy.codegeneration;

/**
 * Fluent API for doing code generation.
 */
public class CodeGenerationBuilder {
    private String json;

    public CodeGenerationBuilder(String json) {
        this.json = json;
    }

    public static CodeGenerationBuilder1 json(String json) {
        CodeGenerationBuilder1 builder = new CodeGenerationBuilder1(json);
        return builder;
    }
}
