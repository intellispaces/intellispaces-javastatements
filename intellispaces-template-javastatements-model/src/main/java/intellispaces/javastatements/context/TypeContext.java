package intellispaces.javastatements.context;

import java.util.Optional;

public interface TypeContext {

  boolean contains(String typeParamName);

  Optional<ContextTypeParameter> get(String typeParamName);
}
