package intellispaces.common.javastatement.method;

import intellispaces.common.action.runner.Runner;
import intellispaces.common.base.function.Consumers;
import intellispaces.common.base.text.TextActions;
import intellispaces.common.javastatement.reference.NamedReference;
import intellispaces.common.javastatement.reference.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MethodSignatureDeclarationBuilderBasedOnMethodPrototype {
  private final MethodStatement prototype;
  private String methodName;
  private String returnTypeString;
  private TypeReference returnTypeReference;
  private boolean includeMethodTypeParams;
  private boolean includeOwnerTypeParams ;
  private List<String> additionalParams;
  private Function<TypeReference, TypeReference> paramMapper;
  private Function<TypeReference, String> paramDeclarationMapper;

  MethodSignatureDeclarationBuilderBasedOnMethodPrototype(MethodStatement prototype) {
    this.prototype = prototype;
    this.methodName = prototype.name();
    this.includeMethodTypeParams = true;
    this.includeOwnerTypeParams = false;
    this.additionalParams = null;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype methodName(String name) {
    this.methodName = name;
    return this;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype returnType(String returnType) {
    this.returnTypeString = returnType;
    return this;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype returnType(TypeReference returnType) {
    this.returnTypeReference = returnType;
    return this;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype includeMethodTypeParams(
      boolean includeMethodTypeParams
  ) {
    this.includeMethodTypeParams = includeMethodTypeParams;
    return this;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype includeOwnerTypeParams(
      boolean includeOwnerTypeParams
  ) {
    this.includeOwnerTypeParams = includeOwnerTypeParams;
    return this;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype addAdditionalParams(
      List<String> additionalParam
  ) {
    if (this.additionalParams == null) {
      this.additionalParams = new ArrayList<>();
    }
    this.additionalParams.addAll(additionalParam);
    return this;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype paramMapper(
      Function<TypeReference, TypeReference> paramMapper
  ) {
    this.paramMapper = paramMapper;
    return this;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype paramDeclarationMapper(
      Function<TypeReference, String> paramDeclarationMapper
  ) {
    this.paramDeclarationMapper = paramDeclarationMapper;
    return this;
  }

  public String get() {
    return get(Consumers.idle(), Function.identity());
  }

  public String get(
      Consumer<String> importConsumer, Function<String, String> canonicalToSimpleNameMapper
  ) {
    var sb = new StringBuilder();
    if ((includeMethodTypeParams && !prototype.typeParameters().isEmpty())
        || (includeOwnerTypeParams && !prototype.owner().typeParameters().isEmpty())
    ) {
      sb.append("<");
      appendTypeParams(sb);
      sb.append("> ");
    }
    appendReturnType(sb, importConsumer, canonicalToSimpleNameMapper);
    sb.append(" ");
    sb.append(methodName);
    sb.append("(");
    appendMethodParams(sb, importConsumer, canonicalToSimpleNameMapper);
    sb.append(")");
    appendExceptions(sb, importConsumer, canonicalToSimpleNameMapper);
    return sb.toString();
  }

  private void appendTypeParams(StringBuilder sb) {
    Runner commaAppender = TextActions.skippingFirstTimeCommaAppender(sb);
    for (NamedReference typeParam : prototype.typeParameters()) {
      commaAppender.run();
      sb.append(typeParam.formalFullDeclaration());
    }
    if (includeOwnerTypeParams) {
      for (NamedReference typeParam : prototype.owner().typeParameters()) {
        commaAppender.run();
        sb.append(typeParam.formalFullDeclaration());
      }
    }
  }

  private void appendReturnType(
      StringBuilder sb,
      Consumer<String> importConsumer,
      Function<String, String> canonicalToSimpleNameMapper
  ) {
    if (returnTypeString != null) {
      sb.append(returnTypeString);
    } else if (returnTypeReference != null) {
      returnTypeReference.dependencyTypenames().forEach(importConsumer);
      sb.append(returnTypeReference.actualDeclaration(canonicalToSimpleNameMapper));
    } else {
      if (prototype.returnType().isEmpty()) {
        sb.append("void");
      } else {
        TypeReference returnTypeReference = prototype.returnType().orElseThrow();
        returnTypeReference.dependencyTypenames().forEach(importConsumer);
        sb.append(returnTypeReference.actualDeclaration(canonicalToSimpleNameMapper));
      }
    }
  }

  private void appendMethodParams(
      StringBuilder sb,
      Consumer<String> importConsumer,
      Function<String, String> canonicalToSimpleNameMapper
  ) {
    Runner commaAppender = TextActions.skippingFirstTimeCommaAppender(sb);
    if (additionalParams != null) {
      for (String additionalParam : additionalParams) {
        commaAppender.run();
        sb.append(additionalParam);
      }
    }
    for (MethodParam param : prototype.params()) {
      commaAppender.run();
      if (paramDeclarationMapper != null) {
        sb.append(paramDeclarationMapper.apply(param.type()));
      } else if (paramMapper != null) {
        TypeReference paramType = paramMapper.apply(param.type());
        paramType.dependencyTypenames().forEach(importConsumer);
        sb.append(paramType.actualDeclaration(canonicalToSimpleNameMapper));
      } else {
        param.type().dependencyTypenames().forEach(importConsumer);
        sb.append(param.type().actualDeclaration(canonicalToSimpleNameMapper));
      }
      sb.append(" ");
      sb.append(param.name());
    }
  }

  private void appendExceptions(
      StringBuilder sb,
      Consumer<String> importConsumer,
      Function<String, String> canonicalToSimpleNameMapper
  ) {
    String exceptions = prototype.exceptions().stream()
        .map(e -> e.asCustomTypeReference().orElseThrow().targetType())
        .peek(e -> importConsumer.accept(e.canonicalName()))
        .map(e -> canonicalToSimpleNameMapper.apply(e.canonicalName()))
        .collect(Collectors.joining(", "));
    if (!exceptions.isEmpty()) {
      sb.append(" throws ").append(exceptions);
    }
  }
}
