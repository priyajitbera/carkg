package com.github.priyajitbera.carkg.service.data.jpa.entity;

public interface CommonEntity<ID, DT> {
  ID getId();

  DT getCreatedAtUtc();

  DT getUpdatedAtUtc();
}
