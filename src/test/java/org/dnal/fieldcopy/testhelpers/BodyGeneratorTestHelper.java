package org.dnal.fieldcopy.testhelpers;

import org.dnal.fieldcopy.converter.FCRegistry;
import org.dnal.fieldcopy.group.ConverterBodyGenerator;
import org.dnal.fieldcopy.group.ObjectConverterSpec;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.runtime.ObjectConverter;

import java.util.Arrays;

import static java.util.Objects.isNull;

public class BodyGeneratorTestHelper {

    public static ConverterBodyGenerator createBodyGenerator(ObjectConverter<?, ?> customConverter1, FieldCopyOptions options) {
        return createBodyGenerator(customConverter1, options, new FCRegistry());
    }

    public static ConverterBodyGenerator createBodyGenerator(ObjectConverter<?, ?> customConverter1, FieldCopyOptions options,
                                                             FCRegistry registry) {
        if (isNull(customConverter1)) {
            return new ConverterBodyGenerator(options);
        } else {
            ObjectConverterSpec converterSpec = new ObjectConverterSpec(customConverter1);
            ConverterBodyGenerator bodyGenerator = new ConverterBodyGenerator(options, Arrays.asList(converterSpec),
                    registry);
            return bodyGenerator;
        }
    }
}
