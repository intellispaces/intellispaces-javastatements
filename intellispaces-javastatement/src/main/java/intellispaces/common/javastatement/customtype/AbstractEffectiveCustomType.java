package intellispaces.common.javastatement.customtype;

import intellispaces.common.action.Actions;
import intellispaces.common.action.getter.Getter;
import intellispaces.common.javastatement.common.DependenciesFunctions;
import intellispaces.common.javastatement.instance.AnnotationInstance;
import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.common.javastatement.reference.CustomTypeReference;
import intellispaces.common.javastatement.reference.NamedReference;
import intellispaces.common.javastatement.reference.NotPrimitiveReference;
import intellispaces.common.javastatement.reference.TypeReference;
import intellispaces.common.javastatement.reference.TypeReferenceFunctions;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

abstract class AbstractEffectiveCustomType implements CustomType {
  protected final CustomType actualType;
  protected final Map<String, NotPrimitiveReference> typeMapping;
  private final Getter<Collection<CustomType>> dependenciesGetter;
  private final Getter<Collection<String>> dependencyTypesGetter;

  AbstractEffectiveCustomType(CustomType actualType, Map<String, NotPrimitiveReference> typeMapping) {
    this.actualType = actualType;
    this.typeMapping = typeMapping;
    this.dependenciesGetter = Actions.cachedLazyGetter(DependenciesFunctions::getCustomTypeDependencies, this);
    this.dependencyTypesGetter = Actions.cachedLazyGetter(AbstractEffectiveCustomType::collectDependencyTypenames, this);
  }

  @Override
  public List<AnnotationInstance> annotations() {
    return actualType.annotations();
  }

  @Override
  public Optional<AnnotationInstance> selectAnnotation(String annotationClass) {
    return actualType.selectAnnotation(annotationClass);
  }

  @Override
  public <A extends Annotation> Optional<A> selectAnnotation(Class<A> annotationClass) {
    return actualType.selectAnnotation(annotationClass);
  }

  @Override
  public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
    return actualType.hasAnnotation(annotationClass);
  }

  @Override
  public boolean isAbstract() {
    return actualType.isAbstract();
  }

  @Override
  public boolean isFinal() {
    return actualType.isFinal();
  }

  @Override
  public String canonicalName() {
    return actualType.canonicalName();
  }

  @Override
  public String className() {
    return actualType.className();
  }

  @Override
  public String simpleName() {
    return actualType.simpleName();
  }

  @Override
  public String packageName() {
    return actualType.packageName();
  }

  @Override
  public boolean isNested() {
    return actualType.isNested();
  }

  @Override
  public List<NamedReference> typeParameters() {
    return actualType.typeParameters();
  }

  @Override
  public Map<String, NamedReference> typeParameterMap() {
    return actualType.typeParameterMap();
  }

  @Override
  public String typeParametersFullDeclaration() {
    return actualType.typeParametersFullDeclaration();
  }

  @Override
  public String typeParametersBriefDeclaration() {
    return actualType.typeParametersBriefDeclaration();
  }

  @Override
  public List<CustomTypeReference> parentTypes() {
    List<CustomTypeReference> actualParentTypes = actualType.parentTypes();
    return actualParentTypes.stream()
        .map(p -> (CustomTypeReference) p.effective(typeMapping))
        .toList();
  }

  @Override
  public boolean hasParent(Class<?> parent) {
    return hasParent(parent.getCanonicalName());
  }

  @Override
  public boolean hasParent(CustomType parent) {
    return hasParent(parent.canonicalName());
  }

  @Override
  public boolean hasParent(String parentCanonicalName) {
    return CustomTypeFunctions.hasParent(this, parentCanonicalName);
  }

  @Override
  public List<MethodStatement> declaredMethods() {
    List<MethodStatement> actualMethods = actualType.declaredMethods();
    return actualMethods.stream()
        .map(m -> m.effective(typeMapping))
        .toList();
  }

  @Override
  public List<MethodStatement> declaredMethodsWithName(String name) {
    return declaredMethods().stream()
        .filter(m -> name.equals(m.name()))
        .toList();
  }

  @Override
  public Optional<MethodStatement> declaredMethod(String name, List<TypeReference> parameterTypes) {
    return declaredMethods().stream()
        .filter(m -> name.equals(m.name()))
        .filter(m -> m.params().size() == parameterTypes.size())
        .filter(m -> TypeReferenceFunctions.isEqualTypes(m.parameterTypes(), parameterTypes))
        .findFirst();
  }

  @Override
  public List<MethodStatement> actualMethods() {
    List<MethodStatement> actualMethods = actualType.actualMethods();
    return actualMethods.stream()
        .map(m -> m.effective(typeMapping))
        .toList();
  }

  @Override
  public List<MethodStatement> actualMethodsWithName(String name) {
    return actualMethods().stream()
        .filter(m -> name.equals(m.name()))
        .toList();
  }

  @Override
  public Optional<MethodStatement> actualMethod(String name, List<TypeReference> parameterTypes) {
    return actualMethods().stream()
        .filter(m -> name.equals(m.name()))
        .filter(m -> m.params().size() == parameterTypes.size())
        .filter(m -> TypeReferenceFunctions.isEqualTypes(m.parameterTypes(), parameterTypes))
        .findFirst();
  }

  @Override
  public Collection<CustomType> dependencies() {
    return dependenciesGetter.get();
  }

  @Override
  public Collection<String> dependencyTypenames() {
    return dependencyTypesGetter.get();
  }

  private Collection<String> collectDependencyTypenames() {
    return dependencies().stream()
        .map(CustomType::canonicalName)
        .collect(Collectors.toSet());
  }
}
