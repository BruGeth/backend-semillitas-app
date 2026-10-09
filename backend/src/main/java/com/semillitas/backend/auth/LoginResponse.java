package com.semillitas.backend.auth;

/** Misma forma que ya espera auth.store.ts del frontend (role en minúsculas). */
public record LoginResponse(String token, UserDto user) {
  public record UserDto(String id, String name, String email, String role, String section) {}
}
