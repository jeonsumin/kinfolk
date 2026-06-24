# Backend

- Spring Boot 3.1 / Java 17 / MyBatis module at workspace/backend-jwt.
- Security routes and credentials flow are under web/security; protected endpoint exceptions require explicit permit rules in SecurityConfig.
- MyBatis mapper interfaces use @Mapper; matching XML lives under src/main/resources/mappers and must end in Mapper.xml.
- User creation logic is centralized in api/admin/system/member/AdminMemberService, including duplicate login-ID checks, BCrypt hashing, and serial IDs.