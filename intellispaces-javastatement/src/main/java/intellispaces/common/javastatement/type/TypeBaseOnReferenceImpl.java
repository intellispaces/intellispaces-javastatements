package intellispaces.common.javastatement.type;

import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.common.base.type.AbstractType;
import intellispaces.common.javastatement.reference.CustomTypeReferences;
import intellispaces.common.javastatement.reference.TypeReference;

import java.util.List;

class TypeBaseOnReferenceImpl<T> extends AbstractType<T> implements Type<T> {
  private final TypeReference reference;

  TypeBaseOnReferenceImpl(TypeReference reference) {
    this.reference = reference;
  }

  @Override
  public TypeReference typeReference() {
    return reference;
  }

  @Override
  public TypeReference baseTypeReference() {
    if (reference.isCustomTypeReference()) {
      return CustomTypeReferences.get(reference.asCustomTypeReferenceOrElseThrow().targetType());
    } else {
      throw UnexpectedViolationException.withMessage("Unsupported reference type: {0}",
          reference.statementType().typename());
    }
  }

  @Override
  @SuppressWarnings("unchecked, rawtypes")
  public List<TypeReference> qualifierTypeReferences() {
    if (reference.isCustomTypeReference()) {
      return (List) reference.asCustomTypeReferenceOrElseThrow().typeArguments();
    } else {
      throw UnexpectedViolationException.withMessage("Unsupported reference type: {0}",
          reference.statementType().typename());
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<T> baseClass() {
    if (reference.isCustomTypeReference()) {
      return (Class<T>) reference.asCustomTypeReferenceOrElseThrow().targetClass();
    } else {
      throw UnexpectedViolationException.withMessage("Unsupported reference type: {0}",
        reference.statementType().typename());
    }
  }

  @Override
  @SuppressWarnings("unchecked, rawtypes")
  public List<intellispaces.common.base.type.Type<?>> qualifierTypes() {
    if (reference.isCustomTypeReference()) {
      return (List) reference.asCustomTypeReferenceOrElseThrow().typeArguments().stream()
        .map(TypeBaseOnReferenceImpl::new)
        .toList();
    } else {
      throw UnexpectedViolationException.withMessage("Unsupported reference type: {0}",
        reference.statementType().typename());
    }
  }
}
