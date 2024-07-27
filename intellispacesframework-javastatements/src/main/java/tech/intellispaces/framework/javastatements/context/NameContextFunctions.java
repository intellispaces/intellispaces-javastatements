package tech.intellispaces.framework.javastatements.context;

import tech.intellispaces.framework.javastatements.statement.type.NamedType;
import tech.intellispaces.framework.javastatements.statement.type.NotPrimitiveType;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface NameContextFunctions {

  static TypeContext getActualNameContext(
      TypeContext parentNamespace, List<NamedType> typeParams, List<NotPrimitiveType> typeArguments
  ) {
    TypeContextBuilder contextBuilder = TypeContexts.build();
    NamedType type;
    NotPrimitiveType actualType;
    if (!typeParams.isEmpty() && typeParams.size() == typeArguments.size()) {
      Iterator<NamedType> paramIterator = typeParams.iterator();
      Iterator<NotPrimitiveType> argumentIterator = typeArguments.iterator();
      while (paramIterator.hasNext() && argumentIterator.hasNext()) {
        NamedType namedTypeReference = paramIterator.next();
        type = namedTypeReference;
        Optional<NamedType> parentFormalTypeReference = parentNamespace.get(namedTypeReference.name())
            .map(ContextTypeParameter::namedType);
        if (parentFormalTypeReference.isPresent()) {
          type = parentFormalTypeReference.get();
        }

        NotPrimitiveType actualTypeReference = argumentIterator.next();
        actualType = actualTypeReference;
        if (actualTypeReference.asNamed().isPresent()) {
          Optional<NotPrimitiveType> parentActualTypeReference = parentNamespace.get(actualTypeReference.asNamed().orElseThrow().name())
              .map(ContextTypeParameter::actualType);
          if (parentActualTypeReference.isPresent()) {
            actualType = parentActualTypeReference.get();
          }
        }

        contextBuilder.addTypeParam(namedTypeReference.name(), type, actualType);
      }
    }
    return contextBuilder.get();
  }
}
