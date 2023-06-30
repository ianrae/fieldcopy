package org.dnal.fieldcopy.group;

import org.dnal.fieldcopy.codegen.CodeGenerator;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.converter.FCRegistry;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.FieldSpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvRegistry;
import org.dnal.fieldcopy.implicitconverter.ImplicitConverterRegistryBuilder;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.registry.ConverterRegistry;
import org.dnal.fieldcopy.registry.MultiSpecRegistry;
import org.dnal.fieldcopy.runtime.ObjectConverter;

import java.util.ArrayList;
import java.util.List;

public class ConverterBodyGenerator {
    private final FieldCopyOptions options;
    private final List<ObjectConverterSpec> additionalConverters;
    private final FCRegistry namedConverters; //for using
    protected FCRegistry finalReg;


    public ConverterBodyGenerator(FieldCopyOptions options) {
        this.options = options;
        additionalConverters = new ArrayList<>();
        namedConverters = null;
    }

    public ConverterBodyGenerator(FieldCopyOptions options, List<ObjectConverterSpec> additionalConverters, FCRegistry namedConverters) {
        this.options = options;
        this.additionalConverters = new ArrayList<>(additionalConverters);
        this.namedConverters = namedConverters;
    }

    public JavaSrcSpec doGen(CopySpec spec, List<CopySpec> specs) {
        MultiSpecRegistry registry = new MultiSpecRegistry();
        registry.addForSpec(spec);
        if (specs != null) {
            for (CopySpec tmp : specs) {
                if (tmp == spec) {
                    continue;
                }
                registry.addForSpec(tmp);
            }
        }

        for (ObjectConverterSpec additionalConv : additionalConverters) {
            registry.addAdditionalNamed(additionalConv.converter, additionalConv.converterName);
        }

        if (namedConverters != null) {
            for(String key: namedConverters.getMap().keySet()) {
                ObjectConverter converter = namedConverters.getMap().get(key);
                String converterName = registry.parseConverterName(key);
                registry.addAdditionalNamed(converter, converterName);
            }
        }

        //validate usingConverter for this nspec, if any
        for (CopySpec tmpSpec : specs) {
            for (FieldSpec fspec : tmpSpec.fields) {
                if (fspec instanceof NormalFieldSpec) {
                    NormalFieldSpec nspec = (NormalFieldSpec) fspec;
                    validateFieldSpec(nspec, registry);
                }
            }
        }

        ImplicitConvRegistry implicitConvRegistry = createImplicitConvRegistry();
        JavaSrcSpec srcSpec = generateCode(spec, implicitConvRegistry, registry);
        finalReg = new FCRegistry(registry.getMap());

        return srcSpec;
    }

    private void validateFieldSpec(NormalFieldSpec nspec, MultiSpecRegistry registry) {
        if (nspec.usingConverterName != null) {
            //fail if given converter not in registry.
            if (!registry.exists(nspec.usingConverterName)) {
                String msg = String.format("using(%s) specified but cannot find named converter '%s'. Did you add it?", nspec.usingConverterName,
                        nspec.usingConverterName);
                throw new FieldCopyException(msg);
            }
        }
        //validate value format
    }

    protected ImplicitConvRegistry createImplicitConvRegistry() {
        ImplicitConverterRegistryBuilder icrBuilder = new ImplicitConverterRegistryBuilder();
        icrBuilder.init();
        icrBuilder.startBuild();
        icrBuilder.buildAdditional();
        ImplicitConvRegistry implicitConvRegistry = new ImplicitConvRegistry(icrBuilder.getRenderMap(), icrBuilder.getPrimBuilder().getStringFieldTypeInfo());
        return implicitConvRegistry;
    }

    protected JavaSrcSpec generateCode(CopySpec spec, ImplicitConvRegistry implicitConvRegistry, ConverterRegistry registry) {
        CodeGenerator codegen = new CodeGenerator(implicitConvRegistry, registry, options);
        JavaSrcSpec srcSpec = codegen.generate(spec);
        return srcSpec;
    }

    public FCRegistry getFinalReg() {
        return finalReg;
    }
}
