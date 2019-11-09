package com.xiaohansong.codemaker.templates;

import com.xiaohansong.codemaker.ClassEntry;
import com.xiaohansong.codemaker.CodeTemplate;

import java.util.List;

public interface TemplateEngine {
    GeneratedSource evaluate(CodeTemplate codeTemplate, List<ClassEntry> selectClasses, ClassEntry currentClass);
}
