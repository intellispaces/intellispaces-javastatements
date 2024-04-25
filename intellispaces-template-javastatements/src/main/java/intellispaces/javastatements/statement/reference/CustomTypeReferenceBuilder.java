package intellispaces.javastatements.statement.reference;

import intellispaces.javastatements.context.TypeContext;
import intellispaces.javastatements.statement.custom.CustomType;
import intellispaces.javastatements.session.Session;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.List;

public interface CustomTypeReferenceBuilder {

  static CustomTypeReference build(CustomType targetType) {
    return build(targetType, List.of());
  }

  static CustomTypeReference build(CustomType targetType, List<NonPrimitiveTypeReference> typeArguments) {
    return new CustomTypeReferenceImpl(targetType, typeArguments);
  }

  static CustomTypeReference build(DeclaredType declaredType, TypeContext typeContext, Session session) {
    return new CustomTypeReferenceFromDeclaredTypeAdapter(declaredType, typeContext, session);
  }

  static CustomTypeReference build(TypeElement typeElement, TypeContext typeContext, Session session) {
    return new CustomTypeReferenceFromTypeElementAdapter(typeElement, typeContext, session);
  }
}
