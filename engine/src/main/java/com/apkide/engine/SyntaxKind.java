package com.apkide.engine;

public enum SyntaxKind {
    Plain,
    Keyword,
    Operator,
    Separator,
    StringLiteral,
    NumberLiteral,
    Metadata,
    Identifier,
    NamespaceIdentifier,
    ClassIdentifier,
    TypeIdentifier,
    VariableIdentifier,
    FunctionIdentifier,
    ParameterIdentifier,
    Comment,
    DocumentationComment
    ;


    public int intValue(){
        return  ordinal();
    }
    public static SyntaxKind of(int kind) {
        return values()[kind];
    }
}
