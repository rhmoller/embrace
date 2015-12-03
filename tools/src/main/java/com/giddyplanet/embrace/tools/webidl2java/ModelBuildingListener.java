package com.giddyplanet.embrace.tools.webidl2java;

import com.giddyplanet.embrace.tools.model.webidl.*;
import com.giddyplanet.embrace.webidl.parser.WebIDLBaseListener;
import com.giddyplanet.embrace.webidl.parser.WebIDLParser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashSet;
import java.util.Set;

public class ModelBuildingListener extends WebIDLBaseListener {
    Set<String> typeExtendedAttributes = new HashSet<>();
    Model model = new Model();
    Interface currentType = null;
    HasArguments currentMethod = null;
    Enumeration currentEnum = null;
    Set<String> unionMembers = new HashSet<>();

    @Override
    public void enterOther(WebIDLParser.OtherContext ctx) {
        super.enterOther(ctx);
        if (currentType == null) {
            typeExtendedAttributes.add(ctx.getText());
        }
    }

    @Override
    public void enterInterface_(WebIDLParser.Interface_Context ctx) {
        super.enterInterface_(ctx);
        String name = ctx.IDENTIFIER_WEBIDL().toString();
        System.out.println("Entering interface " + name);
        currentType = model.getOrCreateInterface(name);
        // todo: iterate over extended attributes and detect all constructors (with args)
        boolean hasConstructor = typeExtendedAttributes.contains("Constructor");
        if (hasConstructor) {
            currentType.addConstructor(new Operation("<init>>"));
        }
        if (ctx.inheritance() != null && ctx.inheritance().IDENTIFIER_WEBIDL() != null) {
            String superTypeName = ctx.inheritance().IDENTIFIER_WEBIDL().getText();
            Interface superType = model.getOrCreateInterface(superTypeName);
            currentType.setSuperType(superType);
        }
    }

    @Override
    public void exitInterface_(WebIDLParser.Interface_Context ctx) {
        super.exitInterface_(ctx);
        currentType = null;
        typeExtendedAttributes.clear();
    }

    @Override
    public void enterPartialInterface(WebIDLParser.PartialInterfaceContext ctx) {
        super.enterPartialInterface(ctx);
        String name = ctx.IDENTIFIER_WEBIDL().toString();
        currentType = model.getOrCreateInterface(name);
        // todo: iterate over extended attributes and detect all constructors (with args)
        boolean hasConstructor = typeExtendedAttributes.contains("Constructor");
        if (hasConstructor) {
            currentType.addConstructor(new Operation("<init>>"));
        }
    }

    @Override
    public void exitPartialInterface(WebIDLParser.PartialInterfaceContext ctx) {
        super.exitPartialInterface(ctx);
        currentType = null;
        typeExtendedAttributes.clear();
    }

    @Override
    public void enterOperation(WebIDLParser.OperationContext ctx) {
        super.enterOperation(ctx);
        WebIDLParser.SpecialOperationContext specialOp = ctx.specialOperation();
        if (specialOp == null) {
            WebIDLParser.ReturnTypeContext returnType = ctx.returnType();

            WebIDLParser.OperationRestContext rest = ctx.operationRest();
            if (rest != null && rest.optionalIdentifier() != null) {
                String name = rest.optionalIdentifier().IDENTIFIER_WEBIDL().toString();
                currentMethod = new Operation(name);

                WebIDLParser.TypeContext type = returnType.type();
                String text1;
                if (type != null && type.unionType() != null) {
                    text1 = "Object";
                } else {
                    text1 = returnType.getText();
                }
                String text = text1;
                currentMethod.setReturnType(text);
            }
        } else {
            Set<String> specials = new HashSet<>();
            getSpecials(specialOp, specials);
            if (specials.contains("getter") || specials.contains("setter")) {

                WebIDLParser.OperationRestContext rest = specialOp.operationRest();
                if (rest != null && rest.optionalIdentifier() != null) {
                    TerminalNode id = rest.optionalIdentifier().IDENTIFIER_WEBIDL();
                    // if a getter or setter has an id we can treat it as an operation

                    if (id != null) {
                        String name = id.toString();
                        currentMethod = new Operation(name);

                        WebIDLParser.TypeContext type = specialOp.returnType().type();
                        String text1;
                        if (type != null && type.unionType() != null) {
                            text1 = "Object";
                        } else {
                            text1 = specialOp.returnType().getText();
                        }
                        String text = text1;
                        currentMethod.setReturnType(text);
                    }
                }
            }
        }
    }

    private void getSpecials(WebIDLParser.SpecialOperationContext specialOp, Set<String> specials) {
        if (specialOp.special() != null) {
            specials.add(specialOp.special().getText());

            WebIDLParser.SpecialsContext specialsContext = specialOp.specials();
            while (specialsContext != null) {
                WebIDLParser.SpecialContext special = specialsContext.special();
                if (special == null) break;
                specials.add(special.getText());
                specialsContext = specialsContext.specials();
            }
        }
    }

    @Override
    public void enterCallbackRest(WebIDLParser.CallbackRestContext ctx) {
        String name = ctx.IDENTIFIER_WEBIDL().getText();

        WebIDLParser.TypeContext type = ctx.returnType().type();
        String returnType;
        if (type != null && type.unionType() != null) {
            returnType = "Object";
        } else if (type == null) {
            returnType ="void";
        } else {
            returnType = type.getText();
        }

        Callback callback = new Callback(name);
        callback.setReturnType(returnType);
        model.addType(callback);

        currentMethod = callback;
    }

    @Override
    public void exitCallbackRest(WebIDLParser.CallbackRestContext ctx) {
        currentMethod = null;
    }

    @Override
    public void enterImplementsStatement(WebIDLParser.ImplementsStatementContext ctx) {
        String baseTypeName = ctx.IDENTIFIER_WEBIDL(0).getText();
        String superTypeName = ctx.IDENTIFIER_WEBIDL(1).getText();
        Interface baseType = model.getOrCreateInterface(baseTypeName);
        Interface superType = model.getOrCreateInterface(superTypeName);
        baseType.addInterface(superType);
    }


    @Override
    public void enterOptionalOrRequiredArgument(WebIDLParser.OptionalOrRequiredArgumentContext ctx) {
        super.enterOptionalOrRequiredArgument(ctx);
        WebIDLParser.TypeContext typeCtx = ctx.type();
        String text;
        if (typeCtx != null && typeCtx.unionType() != null) {
            text = "Object";
        } else {
            text = typeCtx.getText();
        }
        String type = text;
        String name = ctx.argumentName().getText();
        if (currentMethod != null) {
            Argument arg = currentMethod.addArgument(type, name);
            if (ctx.ellipsis() != null && "...".equals(ctx.ellipsis().getText())) {
                arg.setVarArgs(true);
            }
            if (ctx.ellipsis() == null) {
                System.out.println("optional: " + ctx.toString());
                arg.setOptional(true);
            }
        }
    }

    @Override
    public void enterConst_(WebIDLParser.Const_Context ctx) {
        super.enterConst_(ctx);

        WebIDLParser.ConstTypeContext typeCtx = ctx.constType();
        if (typeCtx.primitiveType() != null) {
            String type = typeCtx.primitiveType().getText();
            String name = ctx.IDENTIFIER_WEBIDL().getText();
            String value = ctx.constValue().getText();
            Constant constant = new Constant(type, name, value);
            if (currentType != null) {
                currentType.addConstant(constant);
            }
        } else {
            // todo: identifier
        }
    }

    @Override
    public void exitOperation(WebIDLParser.OperationContext ctx) {
        if (currentMethod != null && currentType != null) {
            currentType.addOperation((Operation) currentMethod);
        }
        currentMethod = null;
    }

    @Override
    public void enterReadonlyMemberRest(WebIDLParser.ReadonlyMemberRestContext ctx) {
        super.enterReadonlyMemberRest(ctx);
        WebIDLParser.AttributeRestContext attributeCtx = ctx.attributeRest();
        if (currentType != null && attributeCtx != null) {
            WebIDLParser.SingleTypeContext singleType = attributeCtx.type().singleType();
            if (singleType != null) {
                String type = singleType.getText();
                WebIDLParser.AttributeNameContext nameContext = attributeCtx.attributeName();
                String name = (nameContext.attributeNameKeyword() == null) ? nameContext.IDENTIFIER_WEBIDL().getText() : "required";
                Attribute attribute = new Attribute(type, name);
                attribute.setReadOnly(true);
                currentType.addAttribute(attribute);
            }
        }
    }

    @Override
    public void enterReadWriteAttribute(WebIDLParser.ReadWriteAttributeContext ctx) {
        super.enterReadWriteAttribute(ctx);
        WebIDLParser.AttributeRestContext attributeCtx = ctx.attributeRest();
        if (currentType != null && attributeCtx != null) {
            WebIDLParser.SingleTypeContext singleType = attributeCtx.type().singleType();
            String type = null;
            if (singleType != null) {
                type = singleType.getText();
            } else {
                WebIDLParser.TypeContext typeCtx = attributeCtx.type();
                if (typeCtx != null && typeCtx.unionType() != null) {
                    type = "Object";
                } else {
                    type = typeCtx.getText();
                }
            }

            WebIDLParser.AttributeNameContext nameContext = attributeCtx.attributeName();
            String name = (nameContext.attributeNameKeyword() == null) ? nameContext.IDENTIFIER_WEBIDL().getText() : "required";
            Attribute attribute = new Attribute(type, name);
            currentType.addAttribute(attribute);
        }
    }

    @Override
    public void enterTypedef(WebIDLParser.TypedefContext ctx) {
        super.enterTypedef(ctx);
        unionMembers.clear();
    }

    @Override
    public void enterEnum_(WebIDLParser.Enum_Context ctx) {
        super.enterEnum_(ctx);
        currentEnum = new Enumeration(ctx.IDENTIFIER_WEBIDL().getText());
        model.addType(currentEnum);
    }

    @Override
    public void exitEnum_(WebIDLParser.Enum_Context ctx) {
        super.exitEnum_(ctx);
        currentEnum = null;
    }

    @Override
    public void enterEnumValueListString(WebIDLParser.EnumValueListStringContext ctx) {
        super.enterEnumValueListString(ctx);
        TerminalNode id = ctx.STRING_WEBIDL();
        if (id != null) {
            String text = id.getText();
            currentEnum.addValue(text.substring(1, text.length() - 1));
        }
    }

    public Model getModel() {
        return model;
    }
}
