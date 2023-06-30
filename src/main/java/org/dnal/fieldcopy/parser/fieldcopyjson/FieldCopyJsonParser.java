package org.dnal.fieldcopy.parser.fieldcopyjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.error.FCError;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.group.ObjectConverterSpec;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.mlexer.ASTToSpecBuilder;
import org.dnal.fieldcopy.mlexer.ConvLangParser;
import org.dnal.fieldcopy.mlexer.MLexer;
import org.dnal.fieldcopy.mlexer.Token;
import org.dnal.fieldcopy.mlexer.ast.AST;
import org.dnal.fieldcopy.util.ReflectionUtil;

import java.io.IOException;
import java.util.*;

import static java.util.Objects.isNull;

/**
 *
 */
public class FieldCopyJsonParser {
    private final FieldCopyLog log;
    private final List<String> knownAttrs;
    private ObjectMapper mapper = new ObjectMapper();

    public FieldCopyJsonParser(FieldCopyLog log) {
        this.log = log;
        this.knownAttrs = Arrays.asList("version", "config", "converters", "additionalConverters", "additionalNamedConverters");
    }

    public ParserResults parse(String json, FieldCopyOptions currentOptions) {
        ParserResults res = new ParserResults();
        Map<String, Object> map = null;
        try {
            map = mapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            addError(res, "json.invalid.syntax", String.format("invalid JSON: " + e.getMessage()));
            return res;
        } catch (IOException e) {
            res.errors.add(new FCError("json.io.error", String.format("invalid JSON: " + e.getMessage())));
            return res;
        }
        if (!map.containsKey("version")) {
            addError(res, "fieldcopy.version.error", String.format("not a Seede script"));
            return res;
        }

        for (String attr : map.keySet()) {
            if (!knownAttrs.contains(attr)) {
                addError(res, "unknown.attribute", String.format("JSON contains unknown attribute '%s'", attr));
            }
        }
        if (!res.errors.isEmpty()) {
            return res;
        }

        String version = getStringValue(map, "version");
        log.log("v: %s", version);
        res.parsedItems = (List<Map<String, Object>>) map.get("converters");
        res.options = parseConfig(map, currentOptions, res);
        List<ObjectConverterSpec> globalConverters = parseGlobalConverters(map, res);
        res.converters = parseActions(res.parsedItems, globalConverters, res);
        res.ok = res.errors.isEmpty();
        return res;
    }

    private void addError(ParserResults res, String errId, String msg) {
        res.errors.add(new FCError(errId, msg));
    }

    FieldCopyOptions parseConfig(Map<String, Object> map, FieldCopyOptions currentOptions, ParserResults res) {
        Map<String, Object> optionsMap = (Map<String, Object>) map.get("config");
        if (optionsMap == null) return currentOptions;

        AttrParserHelper helper = new AttrParserHelper(optionsMap);
        FieldCopyOptions options = new FieldCopyOptions();
        options.defaultSourcePackage = helper.getString("defaultSourcePackage", currentOptions.defaultSourcePackage);
        options.defaultDestinationPackage = helper.getString("defaultDestinationPackage", currentOptions.defaultDestinationPackage);
        options.localDateFormat = helper.getString("localDateFormat", currentOptions.localDateFormat);
        options.localTimeFormat = helper.getString("localTimeFormat", currentOptions.localTimeFormat);
        options.localDateTimeFormat = helper.getString("localDateTimeFormat", currentOptions.localDateTimeFormat);
        options.zonedDateTimeFormat = helper.getString("zonedDateTimeFormat", currentOptions.zonedDateTimeFormat);
        options.utilDateFormat = helper.getString("utilDateFormat", currentOptions.utilDateFormat);

        if (helper.attrParser.areUnParsedAttrs(optionsMap)) {
            List<String> unParsed = helper.attrParser.getUnParsedAttrs(optionsMap);
            String attrList = String.join(",", unParsed);
            res.errors.add(new FCError("unknown.option", String.format("Unknown options: %s", attrList)));
        }
        return options;
    }

    private List<ObjectConverterSpec> parseGlobalConverters(Map<String, Object> map, ParserResults res) {
        Map<String, String> additionalNamedConverters = (Map<String, String>) map.get("additionalNamedConverters");
        List<String> additionalConverters = (List<String>) map.get("additionalConverters");

        List<ObjectConverterSpec> list = buildAdditionalConvertersList(additionalConverters, additionalNamedConverters);
        return list;
    }

    private List<ParsedConverterSpec> parseActions(List<Map<String, Object>> list, List<ObjectConverterSpec> globalConverters, ParserResults res) {
        ArrayList<ParsedConverterSpec> actions = new ArrayList<>();
        for (Map<String, Object> inner : list) {
            String typesStr = getStringValue(inner, "types");
            String packageStr = getStringValue(inner, "package");
            String nameStr = getStringValue(inner, "name");
            List<String> rawFields = (List<String>) inner.get("fields");
            Map<String, String> additionalNamedConverters = (Map<String, String>) inner.get("additionalNamedConverters");
            List<String> additionalConverters = (List<String>) inner.get("additionalConverters");
            //TODO error if any other attributes

            ParsedConverterSpec action = new ParsedConverterSpec(typesStr, nameStr);
            action.packageStr = packageStr;
            action.fieldStrings = rawFields;
            action.additionalConverters = buildAdditionalConvertersList(additionalConverters, additionalNamedConverters);
            action.additionalConverters.addAll(globalConverters);

            //parse typeStr
            ConvLangParser parser = new ConvLangParser();
            List<Token> toks = parser.parseIntoTokens(typesStr);
            toks = combineIntoClassNames(toks, action);
            if (toks.size() < 3) {
                addError(res, "invalid.types.syntax", String.format("invalid types string '%s'", typesStr));
            } else {
                if (toks.get(1).tokType != MLexer.TOK_ARROW) {
                    addError(res, "invalid.types.syntax", String.format("invalid types string '%s'", typesStr));
                }
                action.srcClass = toks.get(0).value;
                action.destClass = toks.get(2).value;
            }

            actions.add(action);
        }

        return actions;
    }

    private List<ObjectConverterSpec> buildAdditionalConvertersList(List<String> additionalConverters, Map<String, String> additionalNamedConverters) {
        List<ObjectConverterSpec> list = new ArrayList<>();

        if (additionalConverters != null) {
            for (String converterClassName : additionalConverters) {
                ObjectConverterSpec converterSpec = new ObjectConverterSpec(null, converterClassName); //un-named
                list.add(converterSpec);
            }
        }

        if (additionalNamedConverters != null) {
            for (String converterName : additionalNamedConverters.keySet()) {
                String converterClassName = additionalNamedConverters.get(converterName);
                ObjectConverterSpec converterSpec = new ObjectConverterSpec(converterName, converterClassName); //named
                list.add(converterSpec);
            }
        }

        return list;
    }

    private List<Token> combineIntoClassNames(List<Token> toks, ParsedConverterSpec action) {
        List<Token> resultL = new ArrayList<>();
        String str = "";
        for (Token tok : toks) {
            switch (tok.tokType) {
                case MLexer.TOK_SYMBOL:
                case MLexer.TOK_PERIOD:
                    str += tok.value;
                    break;
                case MLexer.TOK_ARROW:
                    resultL.add(new Token(MLexer.TOK_SYMBOL, buidClassName(str, action)));
                    str = "";
                    resultL.add(tok);
                default:
                    break; //TODO
            }
        }

        if (StringUtils.isNotEmpty(str)) {
            resultL.add(new Token(MLexer.TOK_SYMBOL, buidClassName(str, action)));
        }

        return resultL;
    }

    private String buidClassName(String str, ParsedConverterSpec action) {
        //only use packageStr if defined and str doesn't contain a package
        if (isNull(action.packageStr) || str.contains(".")) {
            return str;
        }
        String s = String.format("%s.%s", action.packageStr, str);
        return s;
    }

    private String getStringValue(Map<String, Object> map, String attrName) {
        return Optional.ofNullable(map.get(attrName))
                .map(x -> x.toString())
                .orElse(null);
    }

    public CopySpec buildSpecFromAction(ParsedConverterSpec action, FieldCopyOptions options) {
        ASTToSpecBuilder builder = new ASTToSpecBuilder();
        Class<?> srcClass = getClassFromName(action.srcClass, options.defaultSourcePackage);
        Class<?> destClass = getClassFromName(action.destClass, options.defaultDestinationPackage);
        CopySpec spec = builder.buildSpec(srcClass, destClass);
        spec.converterNameForUsing = Optional.ofNullable(action.nameForUsingStr);
        spec.converterName = Optional.ofNullable(action.nameStr);

        for (String convLangSrc : action.fieldStrings) {
            ConvLangParser parser = new ConvLangParser();
            List<Token> toks = parser.parseIntoTokens(convLangSrc);
            if (isNull(toks)) {
                throw new FieldCopyException(String.format("syntax error in: %s", convLangSrc));
            }

            List<AST> list = parser.parseIntoAST(toks);
            builder.addToSpec(spec, list, convLangSrc);
        }
        return spec;
    }

    private Class<?> getClassFromName(String classNameParam, String defaultPackageName) {
        ReflectionUtil helper = new ReflectionUtil();
        String className;
        if (!classNameParam.contains(".") && defaultPackageName != null) {
            className = String.format("%s.%s", defaultPackageName, classNameParam);
        } else {
            className = classNameParam;
        }

        return helper.getClassFromName(className);
    }

}
