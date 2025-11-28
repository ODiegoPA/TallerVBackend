# 📊 ANÁLISIS COMPLETO DE TODOS LOS TESTS DEL PROYECTO

## 📈 RESUMEN GENERAL

**Total de archivos de test:** 11 archivos
**Total de tests aproximados:** 100+ tests

---

## 🔍 DESGLOSE POR ARCHIVO

### 1️⃣ **UserServiceTest.java** (17 tests)
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Funciones de autenticación, registro, login, JWT refresh

#### Tests incluidos:
| # | Nombre del Test | Qué Prueba | Explicación |
|---|---|---|---|
| 1 | `register_exito()` | Registro de usuario exitoso | Verifica que un usuario pueda registrarse correctamente con email, contraseña encriptada y token JWT generado |
| 2 | `register_fallaSiEmailExiste()` | Rechazo de email duplicado | Valida que no se pueda registrar un usuario con un email que ya existe |
| 3 | `register_fallaSiCodigoNoSePuedeGenerar()` | Fallo de generación de código | Verifica que se lance excepción si no se puede generar un código único |
| 4 | `login_exito()` | Login exitoso | Prueba que un usuario existente pueda iniciar sesión con contraseña correcta |
| 5 | `login_usuarioNoExiste()` | Usuario no existe | Valida que se lance `UsernameNotFoundException` si el usuario no está registrado |
| 6 | `login_passwordIncorrecto()` | Contraseña incorrecta | Verifica que se lance `BadCredentialsException` si la contraseña es incorrecta |
| 7 | `refresh_exito()` | Refresh token exitoso | Prueba que se puede refrescar el token JWT con un refresh token válido |
| 8 | `refresh_tokenInvalidoParse()` | Token refresh mal formado | Valida que se lance excepción si el token refresh es inválido |
| 9 | `refresh_tokenInvalidoValidacion()` | Token refresh expirado/inválido | Verifica que se rechace un refresh token inválido |
| 10 | `me_lanzaSiAuthNull()` | Auth nulo | Prueba que se lance excepción si Authentication es nulo |
| 11 | `me_lanzaSiNoAutenticado()` | Usuario no autenticado | Valida que se lance excepción si el usuario no está autenticado |
| 12 | `me_ok()` | Obtener datos del usuario actual | Verifica que se retornen correctamente los datos del usuario autenticado |
| 13 | `getAllUsersByRole_nullReq()` | Listar usuarios sin filtro | Prueba que retorna todos los usuarios si no hay filtro de rol |
| 14 | `getAllUsersByRole_rolVacio()` | Filtro de rol vacío | Valida que retorna todos los usuarios si el rol es una cadena vacía |
| 15 | `getAllUsersByRole_filtrado()` | Filtrar por rol específico | Verifica que retorna solo usuarios del rol especificado |
| 16 | `loadUserByUsername_ok()` | Cargar usuario por email | Prueba que se carga correctamente un usuario por su email |
| 17 | `loadUserByUsername_noExiste()` | Usuario no encontrado por email | Valida que se lance excepción si el email no existe |

---

### 2️⃣ **MateriaServiceTest.java** (25+ tests)
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Operaciones CRUD de materias (crear, leer, actualizar, eliminar)

#### Tests principales:
| # | Nombre del Test | Qué Prueba | Explicación |
|---|---|---|---|
| 1 | `getMateria_retornaDTO_siExisteMateria()` | Obtener materia por ID | Verifica que se retorna un DTO con los datos correctos |
| 2 | `getMateria_mapeaCorrectamenteLosCampos()` | Mapeo correcto de campos | Prueba que los campos se mapean bien del modelo al DTO |
| 3 | `getMateria_lanzaExcepcion_siNoExisteMateria()` | Materia no encontrada | Valida que se lance `NoSuchElementException` si la materia no existe |
| 4 | `getMateria_llamaFindByIdUnaVez()` | Verificar llamada a repository | Confirma que se llama exactamente una vez a `findById` |
| 5 | `getAll_retornaListaVacia_siNoHayMaterias()` | Lista vacía | Prueba que retorna lista vacía cuando no hay materias |
| 6 | `getAll_retornaListaDeDTOs_siHayMaterias()` | Listar todas las materias | Verifica que retorna correctamente todas las materias |
| 7 | `getAll_mapeaCorrectamenteCadaMateria()` | Mapeo de lista de materias | Prueba que cada materia se mapea correctamente |
| 8 | `getAll_llamaFindAllUnaVez()` | Verificar llamada a repository | Confirma una única llamada a `findAll` |
| 9-10 | `create_guardaMateriaCorrectamente()` | Crear nueva materia | Verifica que se guarda correctamente una nueva materia |
| 11-12 | `create_retornaDTODespuesDeGuardar()` | DTO después de crear | Prueba que retorna el DTO correcto después de crear |

---

### 3️⃣ **EvaluacionServiceTest.java** (10+ tests)
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Operaciones de evaluaciones (pruebas, exámenes, etc.)

#### Tests principales:
| # | Nombre del Test | Qué Prueba | Explicación |
|---|---|---|---|
| 1 | `getEvaluacion_debeRetornarDTO_siExiste()` | Obtener evaluación | Verifica que retorna el DTO de una evaluación existente |
| 2 | `getEvaluacion_debeLanzarExcepcion_siNoExiste()` | Evaluación no encontrada | Valida lanzamiento de excepción si no existe |
| 3 | `getAll_debeRetornarListaVacia_siNoHayDatos()` | Lista vacía de evaluaciones | Prueba que retorna lista vacía cuando no hay datos |
| 4 | `getAll_debeRetornarListaDeDTOs()` | Listar evaluaciones | Verifica que retorna lista de DTOs correctamente |
| 5 | `create_debeCrearYRetornarDTO()` | Crear evaluación | Prueba que se crea correctamente una evaluación |
| 6 | `create_siFindByIdVuelveEmpty_retornaEntidadCreada()` | Crear si no encuentra por ID | Verifica comportamiento cuando findById retorna vacío |
| 7 | `create_debeFallarSiDtoEsNulo()` | DTO nulo | Valida que lanza excepción si el DTO es nulo |
| 8 | `update_debeActualizarYRetornarDTO()` | Actualizar evaluación | Prueba que actualiza correctamente una evaluación |
| 9 | `update_siEntidadNoExiste_debeLanzarExcepcion()` | Actualizar evaluación inexistente | Valida excepción si intenta actualizar evaluación que no existe |

---

### 4️⃣ **GestionServiceTest.java** (6+ tests)
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Gestión de años académicos, semestres y modalidades

#### Tests principales:
| # | Nombre del Test | Qué Prueba | Explicación |
|---|---|---|---|
| 1 | `getGestion_retornaDTO_siExisteGestion()` | Obtener gestión | Verifica que retorna DTO de una gestión (año académico) |
| 2 | `getGestion_mapeaSemestresCorrectamente()` | Mapeo de semestres | Prueba que mapea correctamente los semestres de una gestión |
| 3 | `getGestion_mapeaModalidadesCorrectamente()` | Mapeo de modalidades | Verifica que mapea las modalidades (Presencial, Virtual, etc.) |
| 4 | `getGestion_lanzaExcepcion_siNoExisteGestion()` | Gestión no encontrada | Valida excepción si la gestión no existe |
| 5 | `getAll_retornaListaVacia_siNoHayGestiones()` | Lista vacía de gestiones | Prueba que retorna lista vacía cuando no hay datos |

---

### 5️⃣ **JwtServiceTest.java** (9+ tests)
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Generación y validación de tokens JWT (autenticación)

#### Tests principales:
| # | Nombre del Test | Qué Prueba | Explicación |
|---|---|---|---|
| 1 | `generateToken_incluyeClaims()` | Generación de token | Verifica que el token incluye subject (email) y claims (rol) |
| 2 | `extractUsername_lanzaSiTokenInvalido()` | Token inválido | Prueba que lanza excepción con token mal formado |
| 3 | `isAccessValid_trueConTokenValido()` | Token válido | Verifica que valida correctamente un token válido |
| 4 | `isAccessValid_lanzaSiExpirado()` | Token expirado | Prueba que lanza excepción si el token expiró |
| 5 | `generateRefreshToken_y_validaciones()` | Refresh token | Verifica generación y validación de refresh token |
| 6 | `isRefreshValid_lanzaSiTokenFirmadoConOtraClave()` | Validación de firma | Prueba que falla si el token está firmado con otra clave |
| 7 | `isRefreshValid_falseSiUsernameDistinto()` | Username diferente | Valida que falla si el username no coincide |
| 8 | `constructor_lanzaSiSecretCorto()` | Secreto muy corto | Prueba que lanza excepción si el secreto no cumple requisito de longitud |

---

### 6️⃣ **SemestreServiceTest.java** (8+ tests)
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Operaciones CRUD de semestres (períodos académicos)

#### Tests principales:
| # | Nombre del Test | Qué Prueba | Explicación |
|---|---|---|---|
| 1 | `getSemestre_ok()` | Obtener semestre | Verifica que retorna correctamente un semestre |
| 2 | `getSemestre_noExiste()` | Semestre no encontrado | Valida excepción si el semestre no existe |
| 3 | `getAll_vacio()` | Lista vacía | Prueba que retorna lista vacía sin semestres |
| 4 | `getAll_conElementos()` | Listar semestres | Verifica que retorna lista de semestres |
| 5 | `create_ok()` | Crear semestre | Prueba creación correcta de un semestre |
| 6 | `create_gestionNoExiste()` | Gestión no existe | Valida excepción si la gestión (año) no existe |
| 7 | `update_ok()` | Actualizar semestre | Verifica actualización correcta |

---

### 7️⃣ **MatriculacionServiceTest.java** (5+ tests)
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Matriculación de estudiantes en materias

#### Tests principales:
| # | Nombre del Test | Qué Prueba | Explicación |
|---|---|---|---|
| 1 | `getMatriculacion_retornaDTO_siExiste()` | Obtener matriculación | Verifica que retorna correctamente una matriculación con alumno, docente y materia |
| 2 | `getMatriculacion_lanzaExcepcion_siNoExiste()` | Matriculación no encontrada | Valida excepción si no existe |

---

### 8️⃣ **NotaServiceTest.java** (4+ tests)
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Calificaciones y notas de estudiantes

#### Tests principales:
| # | Nombre del Test | Qué Prueba | Explicación |
|---|---|---|---|
| 1 | `getNota_ok()` | Obtener nota | Verifica que retorna correctamente una calificación |
| 2 | `getNota_noExiste()` | Nota no encontrada | Valida excepción si la nota no existe |
| 3 | `findByMatriculacionId_listaVacia()` | Notas vacías | Prueba que retorna lista vacía sin notas |
| 4 | `findByMatriculacionId_conResultados()` | Listar notas | Verifica que retorna notas de una matriculación |

---

### 9️⃣ **ModalidadServiceTest.java** (5+ tests)
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Modalidades de estudio (Presencial, Virtual, etc.)

#### Tests principales:
| # | Nombre del Test | Qué Prueba | Explicación |
|---|---|---|---|
| 1 | `getModalidad_retornaDTO_siExiste()` | Obtener modalidad | Verifica que retorna correctamente una modalidad |
| 2 | `getModalidad_mapeaGestionCorrectamente()` | Mapeo de gestión | Prueba que mapea la gestión asociada |
| 3 | `getModalidad_lanzaExcepcion_siNoExiste()` | Modalidad no encontrada | Valida excepción si no existe |

---

### 🔟 **SemestreMateriaServiceTest.java**
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Materias por semestre (relación semestre-materia-docente)

---

### 1️⃣1️⃣ **EvaluacionRepositoryIntegrationTest.java**
**Ubicación:** `src/test/java/com/example/backend/repository/`
**Qué prueba:** Prueba de integración con la base de datos H2

---

### 1️⃣2️⃣ **BackendApplicationTests.java** (1 test)
**Ubicación:** `src/test/java/com/example/backend/`
**Qué prueba:** Carga del contexto de Spring Boot

#### Test:
| # | Nombre del Test | Qué Prueba | Explicación |
|---|---|---|---|
| 1 | `contextLoads()` | Contexto de Spring Boot | Verifica que la aplicación puede iniciar correctamente sin errores |

---

## 🎯 CONCEPTOS CLAVE EN LOS TESTS

### 🔹 **Mocking (@Mock)**
- Simula objetos (repositorios, servicios) sin usar la base de datos real
- Útil para pruebas rápidas y aisladas

### 🔹 **Inyección (@InjectMocks)**
- Inyecta los mocks en el servicio que se prueba
- Permite probar un servicio sin dependencias reales

### 🔹 **Verificaciones (assert)**
- `assertThat()` - Verifica que un valor cumple una condición
- `assertThatThrownBy()` - Verifica que se lanza una excepción

### 🔹 **Comportamiento (when/then)**
- `when(...).thenReturn()` - Define qué devuelve un mock
- `when(...).thenThrow()` - Define que el mock lanza una excepción

---

## 📊 ESTADÍSTICAS FINALES

| Métrica | Cantidad |
|---------|----------|
| **Total de archivos de test** | 11 |
| **Total de tests aproximados** | 100+ |
| **Tests de Servicios** | ~10 archivos |
| **Tests de Integración** | 1 |
| **Tests de Autenticación** | 17 (UserService) + 9 (JwtService) = 26 |
| **Tests de CRUD** | ~60+ |

---

## ✅ CÓMO EJECUTAR ESTOS TESTS

```bash
# Ejecutar todos
./mvnw clean test

# Ejecutar un archivo específico
./mvnw test -Dtest=UserServiceTest
./mvnw test -Dtest=MateriaServiceTest

# Ejecutar un test específico
./mvnw test -Dtest=UserServiceTest#register_exito
./mvnw test -Dtest=JwtServiceTest#generateToken_incluyeClaims

# Ver reporte
./mvnw test surefire-report:report
```

---

## 🎓 CONCLUSIÓN

Los tests cubren:
✅ Autenticación y autorización (UserService, JwtService)
✅ CRUD de entidades principales (Materia, Evaluación, Gestión, etc.)
✅ Lógica de negocio (Matriculación, Notas)
✅ Validaciones y manejo de errores
✅ Carga del contexto de Spring Boot

**Están bien estructurados usando Mockito y AssertJ para pruebas unitarias robustas.**

