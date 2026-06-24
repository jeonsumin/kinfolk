# Conventions

- Prefer existing shared UI and API helpers over page-local implementations.
- Frontend client API responses use { success, data, messages }; apiFetch throws ApiError for unsuccessful wrapped responses.
- Backend request DTOs use Jakarta Validation and controllers apply @Valid @RequestBody.
- New backend writes belong in Service logic; public endpoints need explicit security rules. There is no global exception advice, so endpoints that need structured errors should return them directly.