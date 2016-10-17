/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.utils;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Mapper<A, B> {

  public abstract B map(A a);

  public Collection<B> mapAll(Collection<A> as) {
    ArrayList<B> bs = new ArrayList<>(as.size());
    for (A a : as) {
      bs.add(map(a));
    }
    return bs;
  }
}
