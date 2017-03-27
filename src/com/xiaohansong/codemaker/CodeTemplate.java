package com.xiaohansong.codemaker;

import com.intellij.openapi.util.text.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hansong.xhs
 * @version $Id: CodeTemplate.java, v 0.1 2017-01-28 9:41 hansong.xhs Exp $$
 */
@Data
@AllArgsConstructor
public class CodeTemplate {

    public CodeTemplate() {}

    /**
     * template name
     */
    private String name;

    /**
     * the generated class name, support velocity
     */
    private String classNameVm;

    /**
     * code template in velocity
     */
    private String codeTemplate;

    /**
     * the number of template context class
     */
    private int classNumber;

    public boolean isValid() {
        return StringUtil.isNotEmpty(getClassNameVm()) && StringUtil.isNotEmpty(getName())
                && StringUtil.isNotEmpty(getCodeTemplate()) && classNumber != -1;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        CodeTemplate that = (CodeTemplate) o;

        if (classNumber != that.classNumber) { return false; }
        if (name != null ? !name.equals(that.name) : that.name != null) { return false; }
        if (classNameVm != null ? !classNameVm.equals(that.classNameVm) : that.classNameVm != null) { return false; }
        return codeTemplate != null ? codeTemplate.equals(that.codeTemplate)
                : that.codeTemplate == null;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (classNameVm != null ? classNameVm.hashCode() : 0);
        result = 31 * result + (codeTemplate != null ? codeTemplate.hashCode() : 0);
        result = 31 * result + classNumber;
        return result;
    }

    public static final CodeTemplate EMPTY_TEMPLATE = new CodeTemplate("", "", "", 1);

}
