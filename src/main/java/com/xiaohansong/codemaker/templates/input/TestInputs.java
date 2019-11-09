package com.xiaohansong.codemaker.templates.input;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

public class TestInputs {
    private static List<TestInput> inputs = unmodifiableList(newArrayList(
            new JavaClass(),
            new ScalaCaseClass()
    ));

    public static List<TestInput> getInputs() {
        return inputs;
    }

    public static Optional<TestInput> getById(String id) {
        return inputs.stream().filter(i -> i.getId().equals(id)).findFirst();
    }

    public static TestInput getByIdOrThrow(String id) {
        return getById(id).orElseThrow(() -> new RuntimeException("No such test input " + id));
    }
}
