package com.xiaohansong.codemaker.templates.input;

import com.xiaohansong.codemaker.ClassEntry;

public interface TestInput {
    String getId();
    String getName();
    String getSource(String className);
    String getLanguage();
    ClassEntry createInput(String className);
}
