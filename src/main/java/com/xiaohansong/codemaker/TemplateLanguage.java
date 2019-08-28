package com.xiaohansong.codemaker;

public enum TemplateLanguage {
    vm("vm"), groovy("gsp");

    TemplateLanguage(String fileType) {
        this.fileType = fileType;
    }

    public final String fileType;
}
